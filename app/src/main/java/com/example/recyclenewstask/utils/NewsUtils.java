package com.example.recyclenewstask.utils;

import com.example.recyclenewstask.model.NewsHeaderModel;
import com.example.recyclenewstask.model.NewsModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class NewsUtils {

    private final static Random RANDOM = new Random();

    private final static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

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
            Date curDate = new Date();
            int dayMinus = RANDOM.nextInt(5);
            newsModel.date = new Date(curDate.getTime() - getDayInMillis(dayMinus));
            news.add(newsModel);
        }

        return news;
    }

    public static Map<String, List<NewsModel>> groupNewsByDate(List<NewsModel> newsModels){
        Map<String, List<NewsModel>> map = new TreeMap<>(Collections.reverseOrder());

        for(NewsModel news : newsModels){
            String strDate = sdf.format(news.date);
            List<NewsModel> group = new ArrayList<>();
            if(map.containsKey(strDate)){
                group = map.get(strDate);
            }
            group.add(news);
            map.put(strDate, group);
        }

        return map;
    }

    public static List<Object> createNewsObjectsForDateGroups(Map<String, List<NewsModel>> map){
        List<Object> objects = new ArrayList<>();
        for(Map.Entry<String, List<NewsModel>> entry : map.entrySet()){
            objects.add(new NewsHeaderModel(formatDateStr(entry.getKey())));
            objects.addAll(entry.getValue());
        }

        return objects;
    }

    public static String formatDate(Date date){
        return sdf.format(date);
    }

    private static String formatDateStr(String dateStr){
        Date curDate = new Date();
        Date yesterdayDate = new Date(curDate.getTime() - getDayInMillis(1));

        if(dateStr.equals(sdf.format(curDate))){
            return "Сегодня";
        } else if (dateStr.equals(sdf.format(yesterdayDate))) {
            return "Вчера";
        }

        String[] splittedDate = dateStr.split("/");
        String day = splittedDate[0];
        String monthName = getMonthName(splittedDate[1]);
        String year = splittedDate[2];
        return String.format("%s %s, %s", day, monthName, year);
    }

    private static String getMonthName(String monthStr){
        switch (monthStr){
            case "01":
                return "января";
            case "02":
                return "февраля";
            case "03":
                return "марта";
            case "04":
                return "апреля";
            case "05":
                return "мая";
            case "06":
                return "июня";
            case "07":
                return "июля";
            case "08":
                return "августа";
            case "09":
                return "сентября";
            case "10":
                return "октября";
            case "11":
                return "ноября";
            case "12":
                return "декабря";
        }

        return null;
    }

    private static long getDayInMillis(int dayNum){
        return 24 * 60 * dayNum * 60 * 1000;
    }

}
