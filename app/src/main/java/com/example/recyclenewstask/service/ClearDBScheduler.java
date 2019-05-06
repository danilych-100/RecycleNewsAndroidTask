package com.example.recyclenewstask.service;

import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.util.Log;

import com.example.recyclenewstask.enitites.News;
import com.example.recyclenewstask.fragments.NewsFragment;
import com.example.recyclenewstask.repository.NewsRepository;
import com.example.recyclenewstask.utils.NewsUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;

public class ClearDBScheduler extends JobService {

    private static final int MAX_NEWS_COUNT = 100;

    @Override
    public boolean onStartJob(JobParameters params) {
        NewsRepository.getInstance()
                .getRowsCount()
                .flatMapMaybe(new Function<Integer, MaybeSource<List<News>>>() {
                    @Override
                    public MaybeSource<List<News>> apply(Integer rowsCount) throws Exception {
                        if(rowsCount > MAX_NEWS_COUNT){
                            return NewsRepository.getInstance()
                                    .getLastRowsByLimitAndOrderByDate(rowsCount - MAX_NEWS_COUNT);
                        }

                        return Maybe.empty();
                    }
                })
                .flatMapCompletable(new Function<List<News>, CompletableSource>() {
                    @Override
                    public CompletableSource apply(List<News> news) throws Exception {
                        if(news != null && news.size() > 0){
                            return NewsRepository.getInstance()
                                    .deleteSomeRows(news);
                        }

                        return Completable.complete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onError(Throwable e) {
                        Log.e(NewsFragment.class.getName(), e.getMessage());
                    }

                    @Override
                    public void onComplete() {}
                });
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
