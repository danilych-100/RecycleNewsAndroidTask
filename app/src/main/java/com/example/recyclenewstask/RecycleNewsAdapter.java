package com.example.recyclenewstask;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.recyclenewstask.listeners.NewsClickListener;
import com.example.recyclenewstask.model.NewsModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecycleNewsAdapter extends RecyclerView.Adapter<NewsViewHolder> {

    private List<NewsModel> news;

    private NewsClickListener listener;

    public RecycleNewsAdapter(List<NewsModel> data, NewsClickListener listener) {
        this.news = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_info, parent, false);
        final NewsViewHolder viewHolder = new NewsViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = viewHolder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onNewsClick(news.get(pos));
                }
            }
        });

        return viewHolder;
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

    public void addNews(NewsModel newsModel){
        this.news.add(newsModel);
        this.notifyItemInserted(getItemCount() - 1);
    }
}
