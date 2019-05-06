package com.example.recyclenewstask.network;

import com.example.recyclenewstask.network.data.NewsDTO;
import com.example.recyclenewstask.network.data.NewsHolderDTO;
import com.example.recyclenewstask.network.data.NewsItemDetails;
import com.example.recyclenewstask.network.data.NewsTitleDTO;
import com.example.recyclenewstask.network.data.TinkoffApiResponse;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NewsApi {

    @GET("v1/news")
    Single<TinkoffApiResponse<List<NewsTitleDTO>>> getAllNews();

    @GET("v1/news_content")
    Single<TinkoffApiResponse<NewsItemDetails>> getNewsById(@Query("id") int id);
}
