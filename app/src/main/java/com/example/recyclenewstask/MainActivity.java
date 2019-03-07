package com.example.recyclenewstask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.ViewManager;

import com.example.recyclenewstask.fragments.NewsFragmentPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createTabLayout();

        RecyclerView.LayoutManager viewManager = new LinearLayoutManager(this);
        RecyclerView.Adapter newsAdapter = new RecycleNewsAdapter(generateNews(10));

        RecyclerView recyclerView = findViewById(R.id.newsRecycleView);
        recyclerView.setLayoutManager(viewManager);
        recyclerView.setAdapter(newsAdapter);
    }

    private void createTabLayout() {
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(
                new NewsFragmentPagerAdapter(getSupportFragmentManager(), this));

        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }


    private List<NewsModel> generateNews(final int count){
        List<NewsModel> news = new ArrayList<>();
        for(int i = 0 ; i < count ; i++){
            NewsModel newsModel = new NewsModel();
            newsModel.title = "News " + i + 1;
            newsModel.desc = "Lorem Ipsum";
            newsModel.date = new Date().toString();
            news.add(newsModel);
        }

        return news;
    }
}
