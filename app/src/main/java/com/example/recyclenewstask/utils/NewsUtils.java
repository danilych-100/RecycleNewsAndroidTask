package com.example.recyclenewstask.utils;

import com.example.recyclenewstask.model.NewsModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class NewsUtils {

    private final static Random RANDOM = new Random();

    private NewsUtils(){}

    public static List<NewsModel> generateNews(final int count) {
        List<NewsModel> news = new ArrayList<>();
        for(int i = 0 ; i < count ; i++){
            NewsModel newsModel = new NewsModel();
            newsModel.id = i;
            newsModel.title = "News " + RANDOM.nextInt(500);
            newsModel.desc = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt";
            newsModel.fullContent = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut " +
                    "labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco " +
                    "laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in" +
                    " voluptate velit esse cillum dolore eu fugiat nulla pariatur.";
            newsModel.date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            news.add(newsModel);
        }

        return news;
    }
}
