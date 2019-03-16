package com.example.recyclenewstask.model;

import java.util.Date;

public class NewsModel {
    public int id;
    public String title;
    public String desc;
    public String fullContent;
    public Date date;
    public boolean isChosen;

    @Override
    public String toString() {
        return "NewsModel{" +
                "title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
