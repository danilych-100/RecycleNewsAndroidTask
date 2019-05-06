package com.example.recyclenewstask.dao;

import com.example.recyclenewstask.enitites.News;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao
public interface NewsDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMany(List<News> newsList);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(News news);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void update(News news);

    @Delete
    void delete(List<News> news);

    @Query("SELECT COUNT(id) FROM news")
    Single<Integer> getRowsCount();

    @Query("SELECT * FROM news ORDER BY date ASC LIMIT :limitCount")
    Maybe<List<News>> getLastRowsByLimitAndOrderByDate(int limitCount);

    @Nullable
    @Query("SELECT * FROM news WHERE id=:idToSelect")
    Single<News> getNewsById(int idToSelect);

    @Query("SELECT * FROM news")
    Single<List<News>> getAll();

    @Query("SELECT * FROM news WHERE id in (:ids)")
    Maybe<List<News>> getAllNewsByIds(List<Integer> ids);

    @Query("DELETE FROM news")
    void deleteAll();
}
