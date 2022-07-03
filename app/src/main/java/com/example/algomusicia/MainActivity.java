package com.example.algomusicia;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.bullhead.equalizer.AnalogController;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.*;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.*;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends AppCompatActivity  {
    ListView listView;
    Button a;
    String[] ite,updatedSong;
    List<String> items;
    FinalAdapter myAdapter;
    Button searchButton;

    EditText searchSong;

    public static  ArrayList<File> mySongs;
    int textLength = 0;
    boolean yes = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listViewSong);
        searchSong = findViewById(R.id.songTextBox);


        runtimePermission();

        listView = findViewById(R.id.listViewSong);
        searchSong = findViewById(R.id.songTextBox);


        runtimePermission();



        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), StartingActivity.class));
            }
        });

        searchSong.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            myAdapter.getFilter().filter(s);



            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



    }

    public  void runtimePermission() {
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                displaySong();
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

    void displaySong()
    {
        //  listView.getChildAt(3).setBackgroundColor(Color.BLUE);
        //File  storage = new File();
        // File storage = new File(System.getenv("EXTERNAL_STORAGE"));
         mySongs = fileSongs(Environment.getExternalStorageDirectory());
         //arraySort = fileSongs(Environment.getExternalStorageDirectory());
       // items = new String[mySongs.size()] ;
            items = new ArrayList<>();

        for(int i  = 0 ; i< mySongs.size(); i++)
        {
            items.add( mySongs.get(i).getName().replace(".mp3","").replace(".wav",""));


        }

       // myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
      // SearchableAdapter myAdapter = new SearchableAdapter(this, items);
        myAdapter = new FinalAdapter(this, mySongs);


       // myAdapter = new ListAdapter(this, R.layout.list_item, mySongs);


        listView.setAdapter(myAdapter);
      //  MainActivity.this.myAdapter.notifyDataSetChanged();



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String songname = (String) listView.getItemAtPosition(position);
               //startActivity(new Intent(getApplicationContext(), PlayerActivity.class).putExtra("song",mySongs).putExtra("songname",songname).putExtra("pos",position));
            }

        });


    }

    public class FinalAdapter extends BaseAdapter implements Filterable {

        public ArrayList<File> itemsModelsl;
        public List<File> itemsModelListFiltered;
        private Context context;

        public FinalAdapter(Context context, ArrayList<File> itemsModelsl) {
            this.itemsModelsl = itemsModelsl;
            this.itemsModelListFiltered = itemsModelsl;
            this.context = context;
        }


        @Override
        public int getCount() {
            return itemsModelListFiltered.size();
        }

        @Override
        public File getItem(int position) {
            return itemsModelListFiltered.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.list_item, null);


            TextView textSong = view.findViewById(R.id.txtSongName);


            textSong.setText(itemsModelListFiltered.get(position).getName().replace(".mp3","").replace(".wav",""));


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                   // startActivity(new Intent(MainActivity.this, PlayerActivity.class).putExtra("song", (ArrayList)itemsModelListFiltered).putExtra("pos", position).putExtra("songname", itemsModelListFiltered.get(position).getName()));

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
                        List<File> resultsModel = new ArrayList<>();
                        String searchStr = constraint.toString().toLowerCase();

                        for (File itemsModel : itemsModelsl) {
                            if (itemsModel.toString().toLowerCase().contains(searchStr) || itemsModel.toString().toLowerCase().contains(searchStr)) {
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