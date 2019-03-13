package com.example.recyclenewstask;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewManager;

import com.example.recyclenewstask.fragments.NewsFragment;
import com.example.recyclenewstask.fragments.NewsFragmentPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String NEWS_ID = "NewsId";
    private final String IS_NEWS_STATUS_CHANGED = "isNewsStatusChanged";

    private NewsFragmentPagerAdapter fragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createTabLayout();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(data != null){
            if(data.getBooleanExtra(IS_NEWS_STATUS_CHANGED, false)){
                NewsFragment chosenNewsFragment = fragmentPagerAdapter.getItem(1);
                chosenNewsFragment.onNewsChanged(data.getIntExtra(NEWS_ID, -1));
            }
        }
    }

    private void createTabLayout() {
        fragmentPagerAdapter = new NewsFragmentPagerAdapter(getSupportFragmentManager(), this);
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(fragmentPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }
}
