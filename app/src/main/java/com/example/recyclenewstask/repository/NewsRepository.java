package com.example.recyclenewstask.repository;

import android.content.Context;

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

import androidx.room.Room;

public class NewsRepository {

    private static NewsRepository INSTANCE;

    private static NewsDAO newsDAO;

    private static ChosenNewsDAO chosenNewsDAO;

    private NewsRepository(){}

    public static NewsRepository getInstance(Context context){
        if(INSTANCE == null){
            synchronized (NewsRepository.class){
                if(INSTANCE == null){
                    INSTANCE = new NewsRepository();
                    NewsDatabase database = Room.databaseBuilder(
                            context,
                            NewsDatabase.class,
                            "news.db").allowMainThreadQueries().build();
                    database.clearAllTables();
                    newsDAO = database.newsDAO();
                    chosenNewsDAO = database.chosenNewsDAO();

                    newsDAO.insertMany(NewsMapper.mapNewsModelListToEntity(NewsUtils.generateNews(30)));
                }
            }
        }

        return INSTANCE;
    }

    public News getNewsById(final int newsId){
        return newsDAO.getNewsById(newsId);
    }

    public boolean isChosenNewsById(final int newsId){
        return chosenNewsDAO.isChosenNewsById(newsId);
    }

    public void deleteByNewsId(final int newsId){
        chosenNewsDAO.deleteByNewsId(newsId);
    }

    public void update(News news){
        newsDAO.update(news);
    }

    public List<ChosenNews> getChosenNews(){
        return chosenNewsDAO.getAllChosenNews();
    }

    public List<News> getAllNews() {
        return newsDAO.getAll();
    }

    public void insertChosenNews(ChosenNews chosenNews) {
        chosenNewsDAO.insert(chosenNews);
    }

    public List<News> getAllChosenNewsByIds(){
        return newsDAO.getAllNewsByIds(getChosenNewsIds());
    }

    private List<Integer> getChosenNewsIds(){
        return chosenNewsDAO.getChosenNewsIds();
    }


}
