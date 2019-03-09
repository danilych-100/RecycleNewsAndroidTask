package com.example.recyclenewstask;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecycleNewsAdapter extends RecyclerView.Adapter<NewsViewHolder> {

    private List<NewsModel> news;

    public RecycleNewsAdapter(List<NewsModel> data) {
        this.news = data;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.news_info, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsModel curNews = this.news.get(position);

        holder.getNewsTitle().setText(curNews.title);
        holder.getNewsDesc().setText(curNews.desc);
        holder.getNewsDate().setText(curNews.date);
    }

    @Override
    public int getItemCount() {
        return this.news.size();
    }
}
