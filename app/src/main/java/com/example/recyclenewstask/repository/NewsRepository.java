package com.example.recyclenewstask.repository;

import android.content.Context;
import android.util.Log;

import com.example.recyclenewstask.NewsInformationActivity;
import com.example.recyclenewstask.dao.ChosenNewsDAO;
import com.example.recyclenewstask.dao.NewsDAO;
import com.example.recyclenewstask.database.NewsDatabase;
import com.example.recyclenewstask.enitites.ChosenNews;
import com.example.recyclenewstask.enitites.News;
import com.example.recyclenewstask.mapper.NewsMapper;
import com.example.recyclenewstask.model.NewsModel;
import com.example.recyclenewstask.utils.NewsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import androidx.room.Room;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

public class NewsRepository {

    private static volatile NewsRepository INSTANCE;

    private NewsDAO newsDAO;

    private ChosenNewsDAO chosenNewsDAO;

    private NewsRepository(){}

    public static NewsRepository getInstance(Context context){
        if(INSTANCE == null){
            synchronized (NewsRepository.class){
                if(INSTANCE == null){
                    INSTANCE = new NewsRepository();
                    NewsDatabase database = Room.databaseBuilder(
                            context,
                            NewsDatabase.class,
                            "news.db").build();

                    INSTANCE.setNewsDAO(database.newsDAO());
                    INSTANCE.setChosenNewsDAO(database.chosenNewsDAO());

                    INSTANCE.deleteAllFromTables()
                            //.andThen(INSTANCE.insertNews(NewsMapper.mapNewsModelListToEntity(NewsUtils.generateNews(30))))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new DisposableCompletableObserver() {
                                @Override
                                public void onComplete() { }

                                @Override
                                public void onError(Throwable e) {
                                    Log.e(NewsInformationActivity.class.getName(), e.getMessage());
                                }
                            });
                }
            }
        }

        return INSTANCE;
    }

    public Maybe<List<News>> getAllNews() {
        return newsDAO.getAll();
    }

    public Maybe<List<News>> getAllChosenNewsByIds(){
        return getChosenNewsIds()
                .flatMap(new Function<List<Integer>, Maybe<List<News>>>() {
                    @Override
                    public Maybe<List<News>> apply(List<Integer> integers) throws Exception {
                        return newsDAO.getAllNewsByIds(integers);
                    }
                });
    }

    private Maybe<List<Integer>> getChosenNewsIds(){
        return chosenNewsDAO.getChosenNewsIds();
    }

    public Single<News> getNewsById(final int newsId){
        return newsDAO.getNewsById(newsId);
    }

    public Single<Boolean> isChosenNewsById(final int newsId){
        return chosenNewsDAO.isChosenNewsById(newsId);
    }

    public Completable insertNews(final List<News> news) {
        return Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                newsDAO.insertMany(news);
            }
        });
    }

    public Completable insertChosenNews(final ChosenNews chosenNews) {
        return Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                chosenNewsDAO.insert(chosenNews);
            }
        });
    }

    public Completable deleteByNewsId(final int newsId){
        return Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                chosenNewsDAO.deleteByNewsId(newsId);
            }
        });
    }

    public Completable deleteAllFromTables(){
        return Completable.fromRunnable(new Runnable() {
            @Override
            public void run() {
                newsDAO.deleteAll();
                chosenNewsDAO.deleteAll();
            }
        });
    }


    public NewsDAO getNewsDAO() {
        return newsDAO;
    }

    public void setNewsDAO(NewsDAO newsDAO) {
        this.newsDAO = newsDAO;
    }

    public ChosenNewsDAO getChosenNewsDAO() {
        return chosenNewsDAO;
    }

    public void setChosenNewsDAO(ChosenNewsDAO chosenNewsDAO) {
        this.chosenNewsDAO = chosenNewsDAO;
    }
}
