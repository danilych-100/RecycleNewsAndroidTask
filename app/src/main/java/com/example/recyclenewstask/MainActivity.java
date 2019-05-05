package com.example.recyclenewstask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.recyclenewstask.enitites.News;
import com.example.recyclenewstask.fragments.NewsFragment;
import com.example.recyclenewstask.fragments.NewsFragmentPagerAdapter;
import com.example.recyclenewstask.listeners.INewsDataPassListener;
import com.example.recyclenewstask.mapper.NewsMapper;
import com.example.recyclenewstask.network.NetworkService;
import com.example.recyclenewstask.network.data.NewsHolderDTO;
import com.example.recyclenewstask.network.data.NewsTitleDTO;
import com.example.recyclenewstask.repository.NewsRepository;
import com.google.android.material.tabs.TabLayout;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements INewsDataPassListener {

    private static final int CHOSEN_FRAGMENT_NUM = 1;

    private NewsFragmentPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createTabLayout();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createTabLayout() {
        adapter = new NewsFragmentPagerAdapter(getSupportFragmentManager(), this);
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void passData(int newsId) {
        if(adapter != null){
            NewsFragment pageFragment = adapter.getFragmentByPosition(CHOSEN_FRAGMENT_NUM);
            pageFragment.onNewsChanged(newsId);
        }
    }
}
