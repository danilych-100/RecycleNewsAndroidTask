package com.example.recyclenewstask.network.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NewsDTO implements Serializable {

    @SerializedName("payload")
    private Payload payload;

    public class Payload{

        @SerializedName("title")
        private NewsTitleDTO title;

        @SerializedName("content")
        private String content;

        public NewsTitleDTO getTitle() {
            return title;
        }

        public void setTitle(NewsTitleDTO title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }
}
