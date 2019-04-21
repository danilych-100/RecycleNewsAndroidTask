package com.example.recyclenewstask.network.data;


import com.google.gson.annotations.SerializedName;

public class NewsTitleDTO {

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setPublicationDate(PDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public class PDate{
        @SerializedName("milliseconds")
        private long milliseconds;

        public long getMilliseconds() {
            return milliseconds;
        }

        public void setMilliseconds(long milliseconds) {
            this.milliseconds = milliseconds;
        }
    }
}
