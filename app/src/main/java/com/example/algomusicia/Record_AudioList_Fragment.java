package com.example.algomusicia;
import android.graphics.Color;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;


/**
 * A simple {@link Fragment} subclass.
 */


public class Record_AudioList_Fragment extends Fragment implements AudioListAdapter.onItemListClick {
    private Boolean ifTouched = false;
    private int prevSelect = -10;
    private ConstraintLayout playerSheet;
    private BottomSheetBehavior bottomSheetBehavior;

    private RecyclerView audioList;
    private File[] allFiles;
    private ImageButton back;
    private NavController navController1;

    private AudioListAdapter audioListAdapter;

    private MediaPlayer mediaPlayer = null;
    private boolean isPlaying = false;

    private File fileToPlay = null;

    //UI Elements
    private ImageButton playBtn;
    private TextView playerHeader;
    private TextView playerFilename;

    private SeekBar playerSeekbar;
    private Handler seekbarHandler;
    private Runnable updateSeekbar;

    public Record_AudioList_Fragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_record__audio_list_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController1= Navigation.findNavController(view);
        playerSheet = view.findViewById(R.id.player_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(playerSheet);
        audioList = view.findViewById(R.id.audio_list_view);
        back = view.findViewById(R.id.backFromRecord);
        playBtn = view.findViewById(R.id.player_play_btn);
        playerHeader = view.findViewById(R.id.player_header_title);
        playerFilename = view.findViewById(R.id.player_filename);

        playerSeekbar = view.findViewById(R.id.player_seekbar);


        String path = getActivity().getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(path);
        allFiles = directory.listFiles();

        final TimeAgo  timeAgo = new TimeAgo();
        //Sorting file via TimeAlgo
        if (allFiles != null && allFiles.length > 1) {
            Arrays.sort(allFiles, new Comparator<File>() {
                @Override
                public int compare(File object1, File object2) {

                return Long.compare(object1.lastModified() ,object2.lastModified());
                }
            });
        }
        Collections.reverse(Arrays.asList(allFiles));



        audioListAdapter = new AudioListAdapter(allFiles, this);

        audioList.setHasFixedSize(true);
        audioList.setLayoutManager(new LinearLayoutManager(getContext()));
        audioList.setAdapter(audioListAdapter);




        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                navController1.navigate(R.id.action_audioListFragment_to_recordFragment);


            }
        });


        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_HIDDEN){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                //We cant do anything here for this app
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying){
                    pauseAudio();
                } else {
                    if(fileToPlay != null){
                        resumeAudio();
                    }
                }
            }
        });

        playerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseAudio();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                mediaPlayer.seekTo(progress);
                resumeAudio();
            }
        });

    }



    @Override
    public void onClickListener(File file, int position, TextView songName, File[] files, ImageView button) {
        fileToPlay = file;
        Log.d("ankaraaa", String.valueOf(position));
        if(prevSelect== position)
        {
            if(isPlaying){
                pauseAudio();
             //button.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.list_play_btn, null));
            } else {
                if(fileToPlay != null){
                    resumeAudio();
               //  button.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.list_pause_btn, null));
                }
            }
        }
        else
        {
            if(isPlaying)
            {
                stopAudio();
            //   button.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.list_play_btn, null));
                playAudio(fileToPlay);
           // button.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.list_pause_btn, null));
            }
            else
            {

                playAudio(fileToPlay);
           //   button.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.list_pause_btn, null));
            }
            prevSelect = position;

        }




    }

    private void pauseAudio() {
        mediaPlayer.pause();
       playBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.list_play_btn, null));

        isPlaying = false;
        seekbarHandler.removeCallbacks(updateSeekbar);
    }

    private void resumeAudio() {
        mediaPlayer.start();
        playBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.list_pause_btn, null));
        isPlaying = true;


        seekbarHandler.postDelayed(updateSeekbar, 0);

    }

    private void stopAudio() {
        //Stop The Audio
        playBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.list_play_btn, null));
        playerHeader.setText("Stopped");
        isPlaying = false;
        mediaPlayer.stop();
        seekbarHandler.removeCallbacks(updateSeekbar);



    }

    private void playAudio(File fileToPlay) {

        mediaPlayer = new MediaPlayer();

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        try {
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
           mediaPlayer.prepare();
           mediaPlayer.start();






        } catch (IOException e) {
            e.printStackTrace();
        }
        playBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.list_pause_btn, null));
        playerFilename.setText(fileToPlay.getName());

        playerHeader.setText("Playing....");
        //Play the audio
        isPlaying = true;
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopAudio();
                playerHeader.setText("Finished");
            }
        });


        playerSeekbar.setMax(mediaPlayer.getDuration());
        seekbarHandler = new Handler();
        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar, 0);



    }



    private void updateRunnable() {
        updateSeekbar = new Runnable() {
            @Override
            public void run() {
                playerSeekbar.setProgress(mediaPlayer.getCurrentPosition());
                seekbarHandler.postDelayed(this, 500);
            }
        };
    }

    @Override
    public void onStop() {
        super.onStop();
        if(isPlaying) {
            stopAudio();

        }
    }

}
