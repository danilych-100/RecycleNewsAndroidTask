package com.example.recyclenewstask.network.data;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NewsTitleDTO implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("text")
    private String title;

    @SerializedName("publicationDate")
    private PDate publicationDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PDate getPublicationDate() {
        return publicationDate;
    }

    public class PDate{
        @SerializedName("milliseconds")
        private long milliseconds;

        public long getMilliseconds() {
            return milliseconds;
        }
    }
}
