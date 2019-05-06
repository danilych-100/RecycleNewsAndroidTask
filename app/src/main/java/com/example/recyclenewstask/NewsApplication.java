package com.example.recyclenewstask;

import android.app.Application;

import com.example.recyclenewstask.network.NetworkService;
import com.example.recyclenewstask.repository.NewsRepository;

public class NewsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        NewsRepository.getInstance()
                .initDB(getApplicationContext());

        NetworkService.getInstance()
                .initService();
    }
}
