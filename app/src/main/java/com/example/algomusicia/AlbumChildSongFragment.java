package com.example.algomusicia;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AlbumChildSongFragment extends Fragment {
    ListView listView;
   public static ArrayList<ModelAudio> mySongs;
    public ArrayList<ModelAudio> ShowChildSongs;
   String albumName = null;

    FinalAdapter myAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_album_child, container, false);
        listView = view.findViewById(R.id.listViewAlbumChild);

        ShowChildSongs = new ArrayList<>();
        Bundle bundle = getArguments();
        if(bundle!=null)
        {

            albumName = bundle.getString("albumKey");

            mySongs = new ArrayList<>();
            displayAllsong();


            for (int i = 0; i < mySongs.size(); i++) {

                String album = mySongs.get(i).getaudioAlbum();

                if(albumName.equalsIgnoreCase("Untitled Albums"))
                {
                    if (album.equalsIgnoreCase("<unknown>") == true)

                    ShowChildSongs.add(mySongs.get(i));
                }
                else
                {
                    if(albumName.equalsIgnoreCase(album))
                    {
                        ShowChildSongs.add(mySongs.get(i));
                    }
                }

            }


            myAdapter =  new FinalAdapter(getActivity(),ShowChildSongs);
            listView.setAdapter(myAdapter);





        }


        return  view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    public class FinalAdapter1 extends BaseAdapter  {

        public ArrayList<ModelAudio> itemsModelsl;
        public List<ModelAudio> itemsModelListFiltered;
        private Context context;

        public FinalAdapter1(Context context, ArrayList<ModelAudio> itemsModelsl) {
            this.itemsModelsl = itemsModelsl;
            this.itemsModelListFiltered = itemsModelsl;
            this.context = context;
        }


        @Override
        public int getCount() {
            return itemsModelListFiltered.size();
        }

        @Override
        public ModelAudio getItem(int position) {
            return itemsModelListFiltered.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final View view = getLayoutInflater().inflate(R.layout.list_item_artist, null);


            TextView textSong = view.findViewById(R.id.txtSongName1);
            textSong.setText(itemsModelListFiltered.get(position).getaudioTitle());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


               //     startActivity(new Intent(getActivity(), PlayerActivity.class).putExtra("song", (ArrayList)itemsModelListFiltered).putExtra("pos", position).putExtra("songname", itemsModelListFiltered.get(position).getName()).putExtra("origin",mySongs));

                }
            });

            return view;
        }




    }
    public class FinalAdapter extends BaseAdapter {

        public ArrayList<ModelAudio> itemsModelsl;
        public List<ModelAudio> itemsModelListFiltered;
        private Context context;

        public FinalAdapter(Context context, ArrayList<ModelAudio> itemsModelsl) {
            this.itemsModelsl = itemsModelsl;
            this.itemsModelListFiltered = itemsModelsl;
            this.context = context;
        }


        @Override
        public int getCount() {
            return itemsModelListFiltered.size();
        }

        @Override
        public ModelAudio getItem(int position) {
            return itemsModelListFiltered.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final View view = getLayoutInflater().inflate(R.layout.list_item_album_child, null);


            TextView textSong = view.findViewById(R.id.txtSongNameChildAlbum);


            textSong.setText(itemsModelListFiltered.get(position).getaudioTitle());


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();
                    bundle.putString("songUrl",itemsModelListFiltered.get(position).getaudioUri().toString());
                    bundle.putString("songName", itemsModelListFiltered.get(position).getaudioTitle());
                    bundle.putSerializable("myFilteredSongs",(ArrayList) itemsModelListFiltered);
                    bundle.putInt("pos1", position);

                    Intent i = new Intent(getActivity(), PlayerActivity_final.class);
                    i.putExtra("bundle", bundle);

                    startActivity(i);

                // startActivity(new Intent(getActivity(), PlayerActivity.class).putExtra("song", (ArrayList)itemsModelListFiltered).putExtra("pos", position).putExtra("songname", itemsModelListFiltered.get(position).getName()).putExtra("origin",mySongs));

                }
            });

            return view;
        }





    }
    void displayAllsong()
    {
        ContentResolver contentResolver = getActivity().getContentResolver();
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
                if(title.equalsIgnoreCase("")==false) {
                    temp.setaudioTitle(title);
                    temp.setaudioUri(Uri.parse(uriData));
                    temp.setaudioArtist(artist);
                    temp.setaudioAlbum(album);

                    mySongs.add(temp);
                }


            } while (cursor.moveToNext());
        }


    }
}
