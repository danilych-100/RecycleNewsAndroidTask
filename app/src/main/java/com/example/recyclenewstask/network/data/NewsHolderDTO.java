package com.example.recyclenewstask.network.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class NewsHolderDTO implements Serializable {

    @SerializedName("resultCode")
    private String statusCode;

    @SerializedName("payload")
    private List<NewsTitleDTO> payloads;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public List<NewsTitleDTO> getPayloads() {
        return payloads;
    }

    public void setPayloads(List<NewsTitleDTO> payloads) {
        this.payloads = payloads;
    }
}
