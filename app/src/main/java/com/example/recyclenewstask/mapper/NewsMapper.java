package com.example.recyclenewstask.mapper;

import com.example.recyclenewstask.enitites.News;
import com.example.recyclenewstask.model.NewsModel;
import com.example.recyclenewstask.network.data.NewsDTO;
import com.example.recyclenewstask.network.data.NewsTitleDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewsMapper {

    private NewsMapper(){}

    public static NewsModel mapNewsEntityToModel(News news, boolean isChosen){
        NewsModel newsModel = new NewsModel();
        newsModel.id = news.id;
        newsModel.desc = news.desc;
        newsModel.date = news.date;
        newsModel.fullContent = news.fullContent;
        newsModel.title = news.title;
        newsModel.isChosen = isChosen;

        return newsModel;
    }

    public static List<NewsModel> mapNewsListEntityToModel(List<News> newsList, boolean isChosen){
        List<NewsModel> newsModels = new ArrayList<>();
        for(News news : newsList){
            newsModels.add(mapNewsEntityToModel(news, isChosen));
        }

        return newsModels;
    }

    public static News mapNewsModelToEntity(NewsModel newsModel){
        News news = new News();
        news.id = newsModel.id;
        news.desc = newsModel.desc;
        news.date = newsModel.date;
        news.fullContent = newsModel.fullContent;
        news.title = newsModel.title;

        return news;
    }

    public static List<News> mapNewsModelListToEntity(List<NewsModel> newsModels){
        List<News> newsList = new ArrayList<>();
        for(NewsModel newsModel : newsModels){
            newsList.add(mapNewsModelToEntity(newsModel));
        }

        return newsList;
    }

    public static News mapNewsTitleDTOToEntity(final NewsTitleDTO newsTitleDTO){
        News news = new News();
        news.id = newsTitleDTO.getId();
        news.desc = newsTitleDTO.getTitle();
        news.date = new Date(newsTitleDTO.getPublicationDate().getMilliseconds());
        news.title = newsTitleDTO.getTitle();

        return news;
    }

    public static News mapNewsDTOToEntity(final NewsDTO newsDTO){
        News news = mapNewsTitleDTOToEntity(newsDTO.getPayload().getTitle());
        news.fullContent = newsDTO.getContent();

        return news;
    }
}
