package com.example.algomusicia;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.AudioViewHolder> {
    public int prevSelect  = -5;
    private int count = 0;
    private File[] allFiles;
    private TimeAgo timeAgo;
    private int selectedPos = RecyclerView.NO_POSITION;

    private onItemListClick onItemListClick;

    public AudioListAdapter(File[] allFiles, onItemListClick onItemListClick) {
        this.allFiles = allFiles;
        this.onItemListClick = onItemListClick;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item, parent, false);
        timeAgo = new TimeAgo();
        return new AudioViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        holder.list_title.setText(allFiles[position].getName());
        holder.list_date.setText(timeAgo.getTimeAgo(allFiles[position].lastModified()));
        holder.list_title.setTextColor(selectedPos == position? Color.CYAN : Color.parseColor("#FFAF84"));
      //  holder.list_image.setBackgroundResource(R.drawable.list_play_btn);

        if(prevSelect != position)
        {
            holder.list_image.setBackgroundResource(selectedPos==position?R.drawable.list_pause_btn:R.drawable.list_play_btn);
        }
        else
        {
            count++;
            if(count%2==0)
            {
                holder.list_image.setBackgroundResource(selectedPos==position?R.drawable.list_pause_btn:R.drawable.list_play_btn);
            }
            else
            {
                holder.list_image.setBackgroundResource(selectedPos==position?R.drawable.list_play_btn:R.drawable.list_pause_btn);
            }
            prevSelect = position;
        }





    }

    @Override
    public int getItemCount() {
        return allFiles.length;
    }

    public class AudioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView list_image;
        private TextView list_title;
        private TextView list_date;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);

            list_image = itemView.findViewById(R.id.list_image_view);
            list_title = itemView.findViewById(R.id.list_title);
            list_date = itemView.findViewById(R.id.list_date);

            itemView.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {

            onItemListClick.onClickListener(allFiles[getAdapterPosition()], getAdapterPosition(),list_title, allFiles, list_image);
            if (getAdapterPosition() == RecyclerView.NO_POSITION) return;

            // Updating old as well as new positions
            notifyItemChanged(selectedPos);
            selectedPos = getLayoutPosition();
            notifyItemChanged(selectedPos);

        }


    }

    public interface onItemListClick {

        void onClickListener(File file, int position , TextView songName, File[] files, ImageView button);

    }

}
