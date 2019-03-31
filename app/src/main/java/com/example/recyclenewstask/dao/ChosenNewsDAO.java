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
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface ChosenNewsDAO {

    @Insert(onConflict = REPLACE)
    void insert(ChosenNews news);

    @Query("DELETE FROM chosennews WHERE news_id = :newsId")
    void deleteByNewsId(int newsId);

    @Query("DELETE FROM chosennews")
    void deleteAll();

    @Nullable
    @Query("SELECT news_id FROM chosennews")
    Maybe<List<Integer>> getChosenNewsIds();

    @Query("SELECT count(*) > 0 FROM chosennews WHERE news_id=:newsId")
    Single<Boolean> isChosenNewsById(int newsId);
}
