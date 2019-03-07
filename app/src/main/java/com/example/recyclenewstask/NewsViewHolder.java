package com.example.recyclenewstask;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NewsViewHolder extends RecyclerView.ViewHolder {

    private final TextView newsTitle;
    private final TextView newsDesc;
    private final TextView newsDate;


    public NewsViewHolder(@NonNull View itemView) {
        super(itemView);

        this.newsTitle = itemView.findViewById(R.id.newsTitle);
        this.newsDesc = itemView.findViewById(R.id.newsDesc);
        this.newsDate = itemView.findViewById(R.id.newsDate);
    }

    public TextView getNewsTitle() {
        return newsTitle;
    }

    public TextView getNewsDesc() {
        return newsDesc;
    }

    public TextView getNewsDate() {
        return newsDate;
    }
}
