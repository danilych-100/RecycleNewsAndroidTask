package com.example.recyclenewstask.repository;

import com.example.recyclenewstask.model.NewsModel;
import com.example.recyclenewstask.utils.NewsUtils;

import java.util.ArrayList;
import java.util.List;

public class NewsRepository {

    private static List<NewsModel> stubNews = NewsUtils.generateNews(20);

    public static NewsModel getNewsById(final int newsId){
        for(NewsModel model : stubNews){
            if(model.id == newsId){
                return model;
            }
        }

        return null;
    }

    public static void update(NewsModel newsModel){
        for(int i = 0; i < stubNews.size(); i++){
            if(stubNews.get(i).id == newsModel.id){
                stubNews.set(i, newsModel);
            }
        }
    }

    public static List<NewsModel> getChosenNews(){
        List<NewsModel> chosen = new ArrayList<>();
        for(NewsModel model : stubNews){
            if(model.isChosen){
                chosen.add(model);
            }
        }

        return chosen;
    }

    public static List<NewsModel> getStubNews() {
        return stubNews;
    }
}
