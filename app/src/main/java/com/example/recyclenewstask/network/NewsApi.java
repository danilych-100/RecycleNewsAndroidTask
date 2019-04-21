package com.example.recyclenewstask.network;

import com.example.recyclenewstask.network.data.NewsDTO;
import com.example.recyclenewstask.network.data.NewsHolderDTO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface NewsApi {

    @GET("v1/news")
    public Call<NewsHolderDTO> getAllNews();

    @GET("v1/news_content/{id}")
    public Call<NewsDTO> getNewsById(@Path("id") int id);
}
