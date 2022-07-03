package com.example.algomusicia;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import androidx.fragment.app.FragmentTransaction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ArtistFragment extends Fragment {
    ListView listView;
    Bundle bundle;
    FinalAdapter myAdapter;
    public static  ArrayList<File> mySongs;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_artist, container, false);
        listView = view.findViewById(R.id.listViewArtist);

        Bundle bundle = getArguments();
        if(bundle!=null)
        {
            ArrayList<String> arrayList = bundle.getStringArrayList("artistNames");
            myAdapter =  new FinalAdapter(getActivity(),arrayList);
            listView.setAdapter(myAdapter);
            mySongs = (ArrayList<File>) bundle.getSerializable("allsongs");

        }


        return  view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    public class FinalAdapter extends BaseAdapter  {

        public ArrayList<String> itemsModelsl;
        public List<String> itemsModelListFiltered;
        private Context context;

        public FinalAdapter(Context context, ArrayList<String> itemsModelsl) {
            this.itemsModelsl = itemsModelsl;
            this.itemsModelListFiltered = itemsModelsl;
            this.context = context;
        }


        @Override
        public int getCount() {
            return itemsModelListFiltered.size();
        }

        @Override
        public String getItem(int position) {
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
            textSong.setText(itemsModelListFiltered.get(position));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    ArtistChildSongFragment fragment = new ArtistChildSongFragment();
                    bundle = new Bundle();
                    bundle.putString("artistKey", itemsModelListFiltered.get(position));
                    bundle.putSerializable("allsongs",mySongs);
                    fragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.artistFragment, fragment, "findThisFragment")
                            .addToBackStack(null)
                            .commit();


                    //     startActivity(new Intent(getActivity(), PlayerActivity.class).putExtra("song", (ArrayList)itemsModelListFiltered).putExtra("pos", position).putExtra("songname", itemsModelListFiltered.get(position).getName()).putExtra("origin",mySongs));

                }
            });

            return view;
        }




    }
}
