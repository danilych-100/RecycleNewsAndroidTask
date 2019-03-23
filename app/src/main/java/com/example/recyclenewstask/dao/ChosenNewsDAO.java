package com.example.recyclenewstask.dao;

import com.example.recyclenewstask.enitites.ChosenNews;
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
public interface ChosenNewsDAO {

    @Insert(onConflict = REPLACE)
    void insert(ChosenNews news);

    @Insert
    void insertMany(List<ChosenNews> newsList);

    @Query("DELETE FROM chosennews WHERE news_id = :newsId")
    void deleteByNewsId(int newsId);

    @Update
    void update(ChosenNews news);

    @Nullable
    @Query("SELECT * FROM chosennews WHERE id=:idToSelect")
    ChosenNews getChosenNewsById(int idToSelect);

    @Nullable
    @Query("SELECT * FROM chosennews")
    List<ChosenNews> getAllChosenNews();

    @Nullable
    @Query("SELECT news_id FROM chosennews")
    List<Integer> getChosenNewsIds();

    @Query("SELECT count(*) > 0 FROM chosennews WHERE news_id=:newsId")
    boolean isChosenNewsById(int newsId);
}
