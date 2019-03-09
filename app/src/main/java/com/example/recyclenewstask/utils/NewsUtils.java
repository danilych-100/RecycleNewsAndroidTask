package com.example.recyclenewstask.utils;

import com.example.recyclenewstask.NewsModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewsUtils {

    private NewsUtils(){}

    public static List<NewsModel> generateNews(final int count){
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
