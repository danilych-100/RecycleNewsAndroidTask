package com.example.recyclenewstask.network.data;

import com.google.gson.annotations.SerializedName;

public class NewsDTO {

    @SerializedName("payload")
    private Payload payload;

    @SerializedName("content")
    private String content;

    public class Payload{

        @SerializedName("title")
        private NewsTitleDTO title;

        public NewsTitleDTO getTitle() {
            return title;
        }

        public void setTitle(NewsTitleDTO title) {
            this.title = title;
        }
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
