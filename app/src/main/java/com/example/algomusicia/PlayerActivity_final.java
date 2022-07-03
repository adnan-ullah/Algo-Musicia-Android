package com.example.algomusicia;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bullhead.equalizer.DialogEqualizerFragment;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

public class PlayerActivity_final extends AppCompatActivity  {
    Button buttonPlay, buttonPrev, buttonNext, buttonFF, buttonFR, buttonEq, buttonLoop;
    TextView textName, musicStart, musicStop;
    SeekBar seekMusic;
    BarVisualizer visualizer;
    ImageView imageView;
    Thread updateSeeker;
    Boolean found = false;

    int position;
    String sName;

    public static final String EXTRA_NAME = "song_name";
    static MediaPlayer mediaPlayer;

    ArrayList<ModelAudio>  mySongs ;
    ArrayList<ModelAudio>  originFile ;
    Thread updateSeekbar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getSupportActionBar().hide();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        buttonEq = findViewById(R.id.eqlzrButton);

        buttonPlay = findViewById(R.id.playButton);
        buttonPrev  =  findViewById(R.id.prevButton);
        buttonNext =  findViewById(R.id.nextButton);
        buttonFF = findViewById(R.id.btnff);
        buttonFR = findViewById(R.id.btnfr);
        buttonLoop = findViewById(R.id.buttonLoop);


        textName = findViewById(R.id.txtSN);
        musicStart = findViewById(R.id.txtStart);
        musicStop = findViewById(R.id.txtEnd);

        seekMusic = findViewById(R.id.seekBar);
        visualizer = findViewById(R.id.blast);
        imageView  = findViewById(R.id.imageView);






        String songUrl = null;
        String songName = null;

        Intent i = getIntent();
      //  i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        Bundle bundle = i.getBundleExtra("bundle");

        if(bundle!=null)
        {
            position = bundle.getInt("pos1",0);
             songUrl =   bundle.getString("songUrl");
             songName = bundle.getString("songName");
            mySongs = (ArrayList) bundle.getSerializable("myFilteredSongs");
            originFile = new ArrayList<>();

            displayAllsong();

            if (mediaPlayer!=null)
            {

                mediaPlayer.stop();
                mediaPlayer.reset();
            }

        }

            String s = mySongs.get(position).getaudioUri().toString();

            mediaPlayer = MediaPlayer.create(this, Uri.parse(s));
              mediaPlayer.start();

            //mediaPlayer.setDataSource(this, Uri.parse(s));






        textName.setSelected(true);
        textName.setText(songName);


        seekMusic.setMax(mediaPlayer.getDuration()/1000);

        final Handler h = new Handler();

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null){
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekMusic.setProgress(mCurrentPosition);
                    String currenttime = createtime(mediaPlayer.getCurrentPosition());
                    musicStart.setText(currenttime);
                    h.postDelayed(this, 1000);
                }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
               buttonNext.performClick();

            }
        });

        int audioSessionId = mediaPlayer.getAudioSessionId();
        if (audioSessionId != -1)
            visualizer.setAudioSessionId(audioSessionId);


        String endtime = createtime(mediaPlayer.getDuration());
        musicStop.setText(endtime);

        seekMusic.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        seekMusic.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.SRC_IN);

        seekMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer!=null && fromUser)
                {
                    musicStart.setText(createtime(mediaPlayer.getCurrentPosition()));
                    mediaPlayer.seekTo(progress*1000);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            }
        });

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (mediaPlayer.isPlaying())
                {
                    buttonPlay.setBackgroundResource(R.drawable.ic_play);
                    mediaPlayer.pause();

                }
                else
                {
                    buttonPlay.setBackgroundResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                }
            }
        });


        //buttonnext and  buttonprev

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                for (int i = 0; i < originFile.size(); i++) {
                    if (mySongs.get(position).getaudioUri().compareTo(originFile.get(i).getaudioUri()) == 0) {
                        position = i;


                        break;
                    }

                }

                position = (position+1) % originFile.size();
                try  {
                    String s = originFile.get(position).getaudioUri().toString();
                    mediaPlayer.stop();
                    mediaPlayer.reset();

                    mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(s));
                    mediaPlayer.prepare();
                    mediaPlayer.start();


                } catch (Exception e) {
                    e.printStackTrace();
                }

                sName = originFile.get(position).getaudioTitle().toString();
                textName.setText(sName);



                seekMusic.setProgress(mediaPlayer.getCurrentPosition());

                buttonPlay.setBackgroundResource(R.drawable.ic_pause);
                startAnimation(imageView);
                int audioSessionId = mediaPlayer.getAudioSessionId();
                if (audioSessionId != -1)
                    visualizer.setAudioSessionId(audioSessionId);


                String endtime = createtime(mediaPlayer.getDuration());
                musicStop.setText(endtime);

                seekMusic.setMax(mediaPlayer.getDuration()/1000);
                seekBarUpdate();
                mySongs  = originFile;



                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        buttonNext.performClick();

                    }
                });



            }
        });


        buttonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();


                for (int i = 0; i < originFile.size(); i++) {
                    if (mySongs.get(position).getaudioUri().compareTo(originFile.get(i).getaudioUri()) == 0) {
                        position = i;


                        break;
                    }

                }


                position = ((position-1)<0)?(originFile.size()-1):(position-1);

                try  {
                    String s = originFile.get(position).getaudioUri().toString();
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(s));
                    mediaPlayer.prepare();
                    mediaPlayer.start();


                } catch (Exception e) {
                    e.printStackTrace();
                }


                sName = originFile.get(position).getaudioTitle().toString();
                textName.setText(sName);

                buttonPlay.setBackgroundResource(R.drawable.ic_pause);
                startAnimation(imageView);
                int audioSessionId = mediaPlayer.getAudioSessionId();
                if (audioSessionId != -1)
                    visualizer.setAudioSessionId(audioSessionId);


                String endtime = createtime(mediaPlayer.getDuration());
                musicStop.setText(endtime);

                seekMusic.setMax(mediaPlayer.getDuration()/1000);
                seekBarUpdate();

                mySongs = originFile;

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        buttonNext.performClick();

                    }
                });

            }
        });







        buttonFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying())
                {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                }
            }
        });
        buttonFR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying())
                {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                }
            }
        });

        buttonEq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInDialog();
                // startActivity(new Intent(getApplicationContext(), EqualiserProcess_Final.class));
            }
        });

        buttonLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer!=null)
                {
                    if (mediaPlayer.isLooping())
                    {
                        mediaPlayer.setLooping(false);
                        buttonLoop.setBackgroundResource(R.drawable.ic_repeat);
                    }
                    else
                    {
                        mediaPlayer.setLooping(true);
                        buttonLoop.setBackgroundResource(R.drawable.ic_loop);
                    }
                }
            }
        });





    }
    void displayAllsong()
    {
        ContentResolver contentResolver = this.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        //looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {

                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String uriData = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                ModelAudio temp = new ModelAudio();

                if(title.equalsIgnoreCase("")==false)
                {
                    temp.setaudioTitle(title);
                    temp.setaudioUri(Uri.parse(uriData));
                    temp.setaudioArtist(artist);
                    temp.setaudioAlbum(album);

                    originFile.add(temp);


                }




            } while (cursor.moveToNext());
        }


    }

    private void showInDialog() {
        int sessionId = mediaPlayer.getAudioSessionId();
        if (sessionId > 0) {
            DialogEqualizerFragment fragment = DialogEqualizerFragment.newBuilder()
                    .setAudioSessionId(sessionId)
                    .title(R.string.app_name)
                    .textColor(ContextCompat.getColor(this, R.color.primaryLightColor))
                    .accentAlpha(ContextCompat.getColor(this, R.color.playingCardColor))
                    .darkColor(ContextCompat.getColor(this, R.color.primaryDarkColor))
                    .setAccentColor(ContextCompat.getColor(this, R.color.accencolor))
                    .build();
            fragment.show(getSupportFragmentManager(), "eq");
        }
    }


    public void seekBarUpdate()
    {
        final Handler h = new Handler();

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null){
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekMusic.setProgress(mCurrentPosition);
                    String currenttime = createtime(mediaPlayer.getCurrentPosition());
                    musicStart.setText(currenttime);
                    h.postDelayed(this, 1000);
                }
            }
        });



    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (visualizer != null)
            visualizer.release();


        super.onDestroy();
    }


    public String createtime(int duration)
    {
        String time = "";
        int min = duration/1000/60;
        int sec = duration/1000%60;
        time+= min + ":";

        if (sec<10)
        {
            time+= "0";
        }
        time+=sec;

        return time;
    }

    public void startAnimation(View view)
    {
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "rotation", 0f , 360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }



}



