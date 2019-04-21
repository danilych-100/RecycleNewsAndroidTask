package com.example.recyclenewstask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

        updateDBFromNetwork();
        createTabLayout();
    }

    private void updateDBFromNetwork(){
        NetworkService.getInstance()
                .getNewsApi()
                .getAllNews()
                .enqueue(new Callback<NewsHolderDTO>() {
                    @Override
                    public void onResponse(Call<NewsHolderDTO> call, Response<NewsHolderDTO> response) {
                        if(!response.isSuccessful()){
                            showNetworkError();
                        } else {
                            List<NewsTitleDTO> newsTitles = response.body().getPayloads();
                            List<News> newsList = new ArrayList<>();
                            for(NewsTitleDTO newsTitleDTO : newsTitles){
                                News news = NewsMapper.mapNewsTitleDTOToEntity(newsTitleDTO);
                                newsList.add(news);
                            }

                            NewsRepository.getInstance(getApplicationContext())
                                    .insertNews(newsList)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new DisposableCompletableObserver() {
                                        @Override
                                        public void onComplete() {
                                            Log.i(NewsInformationActivity.class.getName(), "Сохранили");
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            Log.e(NewsInformationActivity.class.getName(), e.getMessage());
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onFailure(Call<NewsHolderDTO> call, Throwable t) {
                        showNetworkError();
                    }
                });
    }

    private void showNetworkError(){
        Toast toast = Toast.makeText(getApplicationContext(), "Ошибка доступа к интернету", Toast.LENGTH_LONG);
        toast.show();
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
