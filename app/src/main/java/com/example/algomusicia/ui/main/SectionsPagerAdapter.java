package com.example.algomusicia.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.algomusicia.AlbumFragment;
import com.example.algomusicia.ArtistFragment;
import com.example.algomusicia.HdRecording;
import com.example.algomusicia.R;
import com.example.algomusicia.SongFragment;
import com.example.algomusicia.SongFragment_1;
import com.example.algomusicia.YoutubeFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{
            R.string.first_fragment_label,
            R.string.second_fragment_label,
            R.string.third_fragment_label,
            R.string.fourth_fragment_label,
            R.string.fifth_fragment_label};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {


        switch (position)
        {
            case 0:
                return new HdRecording();

            case 1:
                return new ArtistFragment();
            case 2:
                return new SongFragment_1();

            case 3:
                return new AlbumFragment();
            case 4:
                return  new YoutubeFragment();






            default: return null;

        }

    }


    @Override
    public int getItemPosition(Object object) {
        return SectionsPagerAdapter.POSITION_NONE;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 5;
    }
}