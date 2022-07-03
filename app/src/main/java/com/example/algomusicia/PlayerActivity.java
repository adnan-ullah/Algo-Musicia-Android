package com.example.algomusicia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bullhead.equalizer.DialogEqualizerFragment;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    Button buttonPlay, buttonPrev,buttonNext, buttonFF, buttonFR, buttonEq, buttonLoop;
    TextView textName, musicStart , musicStop;
    SeekBar seekMusic;
    BarVisualizer visualizer;
    ImageView imageView;
    Thread updateSeeker;
    Boolean found = false;

    int position;
    String sName;
    ArrayList<File> mySongs;
    ArrayList<File>originFile;
    public static final String EXTRA_NAME = "song_name";
    public MediaPlayer mediaPlayer;


    Thread updateSeekbar;

    /*
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()==android.R.id.home)
        {
            Intent mIntent=new Intent(PlayerActivity.this, MainActivity.class);
            sName = mySongs.get(position).getName().toString();
            mIntent.putExtra(EXTRA_NAME, sName);
            startActivity(mIntent);
        }
        return super.onOptionsItemSelected(item);
    }

     */
    //



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







        if (mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();

        }


        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        originFile =(ArrayList) bundle.getParcelableArrayList("origin");

        mySongs = (ArrayList) bundle.getParcelableArrayList("song");

        sName = mySongs.get(position).getName().toString();
        String songName = i.getStringExtra("songname");
        textName.setSelected(true);

        position = bundle.getInt("pos1",0);






        Uri uri = Uri.parse(mySongs.get(position).toString());
        sName = mySongs.get(position).getName().toString();
        textName.setText(sName);

        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();

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

        //visualizer
        int audioSessionId = mediaPlayer.getAudioSessionId();
        if (audioSessionId != -1)
            visualizer.setAudioSessionId(audioSessionId);



        String endtime = createtime(mediaPlayer.getDuration());
        musicStop.setText(endtime);


        //shutdown


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


        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();



                    for (int i = 0; i < originFile.size(); i++) {
                        if (mySongs.get(position).compareTo(originFile.get(i)) == 0) {
                            position = i;


                            break;
                        }

                }

                position = (position+1) % originFile.size();
                Uri u = Uri.parse(originFile.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),u);
                sName = originFile.get(position).getName().toString();
                textName.setText(sName);

                mediaPlayer.start();

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
                mediaPlayer.release();

                for (int i = 0; i < originFile.size(); i++) {
                    if (mySongs.get(position).compareTo(originFile.get(i)) == 0) {
                        position = i;


                        break;
                    }

                }


                position = ((position-1)<0)?(originFile.size()-1):(position-1);

                Uri u = Uri.parse(originFile.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),u);
                sName = originFile.get(position).getName().toString();
                textName.setText(sName);
                mediaPlayer.start();
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
    public  void seekThread ()
    {

        updateSeekbar = new Thread()
        {
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;

                while (currentPosition<totalDuration)
                {
                    try {
                        sleep(1000);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekMusic.setProgress(currentPosition);
                    }
                    catch (InterruptedException | IllegalStateException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };

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









}










/*
OLD CODE
package com.example.algomusicia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    Button buttonPlay, buttonPrev,buttonNext, buttonFF, buttonFR;
    TextView textName, musicStart , musicStop;
    SeekBar seekMusic;
    BarVisualizer visualizer;
    ImageView imageView;
    Thread updateSeeker;

    int position;
    String sName;
    ArrayList<File> mySongs;
    public static final String EXTRA_NAME = "song_name";
    static MediaPlayer mediaPlayer;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == (android.R.id.home))
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if(visualizer != null)
        {
            visualizer.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        getSupportActionBar().setTitle("Now playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        buttonPlay = findViewById(R.id.playButton);
        buttonPrev  =  findViewById(R.id.prevButton);
        buttonNext =  findViewById(R.id.nextButton);
        buttonFF = findViewById(R.id.btnff);
        buttonFR = findViewById(R.id.btnfr);

        textName = findViewById(R.id.txtSN);
        musicStart = findViewById(R.id.txtStart);
        musicStop = findViewById(R.id.txtEnd);

        seekMusic = findViewById(R.id.seekBar);
        visualizer = findViewById(R.id.blast);
        imageView  = findViewById(R.id.imageView);



        updateSeeker = new Thread()
        {
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentDuration = 0 ;

                while(currentDuration<totalDuration)
                {
                    try {
                        sleep(500);
                        currentDuration = mediaPlayer.getCurrentPosition();
                        seekMusic.setProgress(currentDuration);
                    }
                    catch (InterruptedException | IllegalStateException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };
        if(mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }


        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        mySongs = (ArrayList)   bundle.getParcelableArrayList("song");
        String songName = i.getStringExtra("songname");
        position = bundle.getInt("pos",0);
        textName.setSelected(true);

        Uri uri = Uri.parse(mySongs.get(position).toString());
        sName= mySongs.get(position).getName();
        textName.setText(sName);

        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();

        seekMusic.setMax(mediaPlayer.getDuration());
        updateSeeker.start();



        seekMusic.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        seekMusic.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        seekMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });




        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying())
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

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                buttonNext.performClick();

            }
        });


        int audioSessionID = mediaPlayer.getAudioSessionId();
        if(audioSessionID!=-1) {
            visualizer.setAudioSessionId(audioSessionID);
        }

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position =  (position+1)%mySongs.size();
                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),u);
                sName = mySongs.get(position).getName();
                textName.setText(sName);
                mediaPlayer.start();
                buttonPlay.setBackgroundResource(R.drawable.ic_pause);
                ImageAnimaon(imageView);


                int audioSessionID = mediaPlayer.getAudioSessionId();
                if(audioSessionID!=-1) {
                    visualizer.setAudioSessionId(audioSessionID);
                }
            }
        });


        buttonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position =  (position-1)<0? mySongs.size()-1 : (position-1);
                Uri u = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),u);
                sName = mySongs.get(position).getName();
                textName.setText(sName);
                mediaPlayer.start();
                buttonPlay.setBackgroundResource(R.drawable.ic_pause);
                ImageAnimaon(imageView);

                int audioSessionID = mediaPlayer.getAudioSessionId();
                if(audioSessionID!=-1) {
                    visualizer.setAudioSessionId(audioSessionID);
                }
            }
        });

        String endTime = createTime(mediaPlayer.getDuration());
        musicStop.setText(endTime);


        final Handler handler = new Handler();
        final int delay = 1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = createTime(mediaPlayer.getCurrentPosition());
                musicStart.setText(currentTime);
                handler.postDelayed(this,delay);
            }
        },delay);


        buttonFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
                }
            }
        });



        buttonFR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                }
            }
        });



    }
    public void ImageAnimaon(View view)
    {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageView, "rotation",0f, 360f);
        objectAnimator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator);
        animatorSet.start();

    }
    public String createTime(int duration)
    {
        String time = "";
        int min =  duration/1000/60;
        int sec= duration/1000%60;

        time+=min+":";

        if(sec<10)
        {
            time+="0";
        }
        time+=sec;
        return time;
    }

}
 */