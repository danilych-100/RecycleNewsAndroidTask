package com.example.recyclenewstask.enitites;

import java.util.Date;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class News {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "description")
    public String desc;

    @ColumnInfo(name = "full_content")
    public String fullContent;

    @ColumnInfo(name = "date")
    public Date date;
}
