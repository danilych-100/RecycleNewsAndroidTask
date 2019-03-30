package com.example.recyclenewstask.fragments;

import android.content.Context;

import com.example.recyclenewstask.R;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class NewsFragmentPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 2;

    private final String tabTitles[];
    private NewsFragment[] fragments;

    public NewsFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);

        this.tabTitles = new String[]{
                context.getString(R.string.relatedNews), context.getString(R.string.chosenNews)
        } ;

        fragments = new NewsFragment[PAGE_COUNT];
    }

    @Override public int getCount() {
        return PAGE_COUNT;
    }

    @Override public NewsFragment getItem(int position) {
        NewsFragment newsFragment;
        switch (position){
            case 0:
                newsFragment = NewsFragment.newInstance(NewsStatus.RELATED);
                break;
            case 1:
                newsFragment = NewsFragment.newInstance(NewsStatus.CHOSEN);
                break;
                default:
                    newsFragment = NewsFragment.newInstance(NewsStatus.RELATED);
        }

        this.fragments[position] = newsFragment;

        return newsFragment;
    }

    @Override public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    public NewsFragment getFragmentByPosition(int position){
        return this.fragments[position];
    }
}
