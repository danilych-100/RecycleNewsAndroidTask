package com.example.recyclenewstask.database;

import com.example.recyclenewstask.dao.ChosenNewsDAO;
import com.example.recyclenewstask.dao.NewsDAO;
import com.example.recyclenewstask.enitites.ChosenNews;
import com.example.recyclenewstask.enitites.News;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {News.class, ChosenNews.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class NewsDatabase extends RoomDatabase {
    public abstract NewsDAO newsDAO();

    public abstract ChosenNewsDAO chosenNewsDAO();
}
