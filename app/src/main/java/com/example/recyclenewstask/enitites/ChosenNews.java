package com.example.recyclenewstask.enitites;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class ChosenNews {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "news_id")
    public int newsId;

    public ChosenNews() {
    }

    @Ignore
    public ChosenNews(int newsId) {
        this.newsId = newsId;
    }
}
