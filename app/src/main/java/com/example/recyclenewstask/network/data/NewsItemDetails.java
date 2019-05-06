package com.example.recyclenewstask.network.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NewsItemDetails implements Serializable {

    @SerializedName("content")
    private String content;

    @SerializedName("title")
    private NewsTitleDTO title;

    public String getContent() {
        return content;
    }

    public NewsTitleDTO getTitle() {
        return title;
    }
}
