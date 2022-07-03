package com.example.algomusicia;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class SongFragment_1 extends  Fragment   {

    ListView listView;

    List<String> items;
    FinalAdapter myAdapter;
    Button searchButton;
    EditText searchSong;
    Bundle bundle , bundle1 ;
    FinalAdapter finalAdapter;
    Boolean found=false, found1=false;


    public static  ArrayList<File> mySongs;
    public static  ArrayList<ModelAudio> audioArrayList ;
public static ArrayList<String> artistNames,albumsName;
public static HashMap<String, ArrayList<String>>  artistHead;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
        {
            audioArrayList = new ArrayList<>();
              displayAllsong();
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.listViewSong);
        searchSong = view.findViewById(R.id.songTextBox);
        audioArrayList = new ArrayList<>();

        runtimePermission();


        searchSong.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               finalAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);

        return  view;
    }

    public  void runtimePermission() {
        Dexter.withContext(getActivity()).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                displayAllsong();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();

    }


    public ArrayList<File>  fileSongs(File file)
    {
        ArrayList<File>  arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        if(files!=null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    arrayList.addAll(fileSongs(singleFile));

                } else {
                    if (singleFile.getName().endsWith((".mp3")) || singleFile.getName().endsWith((".wav"))) {
                        arrayList.add(singleFile);

                    }
                }
            }
        }
        return arrayList;


    }
    void displayAllsong()
    {
        artistNames = new ArrayList<>();
        albumsName = new ArrayList<>();


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


                if(artist.equalsIgnoreCase("<unknown>") == false)
                   artistNames.add(artist);
                else
                    found = true;

                if(album.equalsIgnoreCase("<unknown>") == false)
                {
                    albumsName.add(album);
                }
                else
                    found1 = true;



                ModelAudio temp = new ModelAudio();
                if(title.equalsIgnoreCase("")==false)
                {
                    temp.setaudioTitle(title);
                    temp.setaudioUri(Uri.parse(uriData));
                    temp.setaudioArtist(artist);
                    temp.setaudioAlbum(album);

                    audioArrayList.add(temp);


                }






            } while (cursor.moveToNext());
        }









        ArrayList<File>  recordedFile = new ArrayList<>();


        String path = getActivity().getExternalFilesDir(null).toString();
        File files = new File(path);

        recordedFile = fileSongs(files);

        Log.d("recorded file",recordedFile.toString());


        if(recordedFile!=null) {
            for (File f : recordedFile) {

                MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
                metaRetriever.setDataSource(f.toString());

                String title = f.getName();
                Uri uriData = Uri.fromFile(f);
                String artist = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                String album = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);


                ModelAudio temp = new ModelAudio();
                if (title.equalsIgnoreCase("") == false) {
                    temp.setaudioTitle(title);
                    temp.setaudioUri(uriData);
                    temp.setaudioArtist(artist);
                    temp.setaudioAlbum(album);
                    audioArrayList.add(temp);

                }



            }
        }



        if(found==true)
            artistNames.add("Unknown artist");
        if(found1==true)
            albumsName.add("Untitled Albums");



        ArtistFragment fragment = new ArtistFragment();


        AlbumFragment fragment1 = new AlbumFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        bundle = new Bundle();
        bundle.putStringArrayList("artistNames", (ArrayList<String>) artistNames);
        bundle.putStringArrayList("albumNames",(ArrayList<String>) albumsName);
        bundle.putSerializable("allsongs",mySongs);
        fragment.setArguments(bundle);
        fragment1.setArguments(bundle);

        fragmentTransaction.replace(R.id.artistFragment,fragment);
        fragmentTransaction.replace(R.id.albumFragment,fragment1);
        fragmentTransaction.commit();




        finalAdapter = new FinalAdapter(getActivity(), audioArrayList);

        listView.setAdapter(finalAdapter);

    }

    void displaySong() {
        mySongs = fileSongs(Environment.getExternalStorageDirectory());
        mySongs.addAll(fileSongs(getActivity().getFilesDir()));

        items = new ArrayList<>();
        artistNames = new ArrayList<>();
        albumsName = new ArrayList<>();
        artistHead = new HashMap<>();
        boolean found = false, found1 = false;



        for (int i = 0; i < mySongs.size(); i++) {
            items.add(mySongs.get(i).getName().replace(".mp3", "").replace(".wav", ""));
            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();

             metaRetriever.setDataSource(mySongs.get(i).toString());
            String artist =  metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String album = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            if(artist!=null )
           artistNames.add(artist);
            else
                found = true;

            if(album!= null)
            {
                albumsName.add(album);
            }
            else
                found1 = true;


            //  String title = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

          //  String title = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        }
/*
        for (Map.Entry<String, ArrayList<String>> ee : artistHead.entrySet()) {
            String key = ee.getKey();
            ArrayList<String> values = ee.getValue();

            Log.d("Key", key + " @@@@@  "+values.toString() );
            // TODO: Do something.
        }



 */



        if(found==true)
           artistNames.add("Unknown artist");
        if(found1==true)
            albumsName.add("Untitled Albums");



        ArtistFragment fragment = new ArtistFragment();


        AlbumFragment fragment1 = new AlbumFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        bundle = new Bundle();
        bundle.putStringArrayList("artistNames", (ArrayList<String>) artistNames);
        bundle.putStringArrayList("albumNames",(ArrayList<String>) albumsName);
        bundle.putSerializable("allsongs",mySongs);
        fragment.setArguments(bundle);
        fragment1.setArguments(bundle);

        fragmentTransaction.replace(R.id.artistFragment,fragment);
        fragmentTransaction.replace(R.id.albumFragment,fragment1);
       // fragmentTransaction.replace(R.id.artistFragmentChild, fragment2);
        fragmentTransaction.commit();






    // myAdapter = new FinalAdapter(getActivity(), mySongs);


       listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String songname = (String) listView.getItemAtPosition(position);
              //  startActivity(new Intent(getActivity(), PlayerActivity.class).putExtra("song",mySongs).putExtra("songname",songname).putExtra("pos",position).putExtra("origin",mySongs));
            }

        });
    }




    public class FinalAdapter extends BaseAdapter implements Filterable{

        public List<ModelAudio> itemsModelsl;
        public List<ModelAudio> itemsModelListFiltered;
        public ArrayList<String> allSongURI;
        private Context context;

        public FinalAdapter(Context context, List<ModelAudio> itemsModelsl) {

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
            final View view = getLayoutInflater().inflate(R.layout.list_item, null);


            TextView textSong = view.findViewById(R.id.txtSongName);


            textSong.setText(itemsModelListFiltered.get(position).getaudioTitle().toString());


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



                }
            });

            return view;
        }
        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    FilterResults filterResults = new FilterResults();
                    if (constraint == null || constraint.length() == 0) {
                        filterResults.count = itemsModelsl.size();
                        filterResults.values = itemsModelsl;

                    } else {
                        List<ModelAudio> resultsModel = new ArrayList<>();
                        String searchStr = constraint.toString().toLowerCase();

                        for (ModelAudio itemsModel : itemsModelsl) {
                            if (itemsModel.audioTitle.toString().toLowerCase().contains(searchStr) || itemsModel.audioTitle.toString().toLowerCase().contains(searchStr)) {
                                resultsModel.add(itemsModel);


                            }
                            filterResults.count = resultsModel.size();
                            filterResults.values = resultsModel;
                        }


                    }

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    itemsModelListFiltered = (ArrayList) results.values;



                    notifyDataSetChanged();


                    Log.d("Result", String.valueOf(results.values));

                }
            };
            return filter;
        }



    }



}






