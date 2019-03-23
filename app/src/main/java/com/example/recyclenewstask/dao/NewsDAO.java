package com.example.recyclenewstask.dao;

import com.example.recyclenewstask.enitites.News;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface NewsDAO {

    @Insert(onConflict = REPLACE)
    void insert(News news);

    @Insert
    void insertMany(List<News> newsList);

    @Update
    void update(News news);

    @Delete
    void delete(News news);

    @Nullable
    @Query("SELECT * FROM news WHERE id=:idToSelect")
    News getNewsById(int idToSelect);

    @Query("SELECT * FROM news")
    List<News> getAll();

    @Query("SELECT * FROM news WHERE id in (:ids)")
    List<News> getAllNewsByIds(List<Integer> ids);
}
