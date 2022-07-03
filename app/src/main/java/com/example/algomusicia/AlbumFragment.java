package com.example.algomusicia;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AlbumFragment extends Fragment {
    ListView listView;
    public ArrayList<ModelAudio> mySongs;
    Bundle bundle;
    FinalAdapter myAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_album,container,false);

        listView = view.findViewById(R.id.listViewAlbum);

        Bundle bundle = getArguments();
        if(bundle!=null)
        {
            ArrayList<String> arrayList = bundle.getStringArrayList("albumNames");

            myAdapter =  new FinalAdapter(getActivity(),arrayList);
            listView.setAdapter(myAdapter);

        }


        return  view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    public class FinalAdapter extends BaseAdapter {

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
            final View view = getLayoutInflater().inflate(R.layout.list_item_album, null);


            TextView textSong = view.findViewById(R.id.txtSongName2);

            textSong.setText(itemsModelListFiltered.get(position));


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    AlbumChildSongFragment fragment = new AlbumChildSongFragment();
                   bundle = new Bundle();
                    bundle.putString("albumKey", itemsModelListFiltered.get(position));

                    fragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.albumFragment, fragment, "findThisFragment")
                            .addToBackStack(null)
                            .commit();


                    //     startActivity(new Intent(getActivity(), PlayerActivity.class).putExtra("song", (ArrayList)itemsModelListFiltered).putExtra("pos", position).putExtra("songname", itemsModelListFiltered.get(position).getName()).putExtra("origin",mySongs));

                }
            });


            return view;
        }




    }
}
