package com.example.recyclenewstask.fragments;

import android.content.Context;

import com.example.recyclenewstask.R;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class NewsFragmentPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 2;

    private final String tabTitles[];

    public NewsFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);

        this.tabTitles = new String[]{
                context.getString(R.string.relatedNews), context.getString(R.string.chosenNews)
        } ;
    }

    @Override public int getCount() {
        return PAGE_COUNT;
    }

    @Override public Fragment getItem(int position) {
        switch (position){
            case 0:
                return NewsFragment.newInstance(NewsStatus.RELATED);
            case 1:
                return NewsFragment.newInstance(NewsStatus.CHOSEN);
        }
        return NewsFragment.newInstance(NewsStatus.RELATED);
    }

    @Override public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
