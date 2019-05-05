package com.example.recyclenewstask.adapter;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.recyclenewstask.R;
import com.example.recyclenewstask.holder.NewsHeaderViewHolder;
import com.example.recyclenewstask.holder.NewsViewHolder;
import com.example.recyclenewstask.listeners.NewsClickListener;
import com.example.recyclenewstask.model.NewsHeaderModel;
import com.example.recyclenewstask.model.NewsModel;

import java.text.SimpleDateFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.recyclenewstask.utils.NewsUtils.formatDate;
import static com.example.recyclenewstask.utils.NewsUtils.formatDateStr;

public class RecycleNewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int TYPE_NEWS = 1;
    private final static int TYPE_HEADER = 2;

    private List<Object> dataset;
    private NewsClickListener listener;

    public RecycleNewsAdapter(List<Object> data, NewsClickListener listener) {
        this.dataset = data;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return dataset.get(position) instanceof NewsModel
                ? TYPE_NEWS
                : TYPE_HEADER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case TYPE_NEWS: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_info, parent, false);

                final NewsViewHolder viewHolder = new NewsViewHolder(view);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = viewHolder.getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            listener.onNewsClick((NewsModel) dataset.get(pos));
                        }
                    }
                });

                return viewHolder;
            }
            case TYPE_HEADER: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_header_info, parent, false);

                return new NewsHeaderViewHolder(view);
            }
            default:
                throw new IllegalArgumentException(
                        "unknown viewType=" + viewType);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case TYPE_NEWS: {
                NewsModel curNews = (NewsModel) this.dataset.get(position);
                NewsViewHolder viewHolder = (NewsViewHolder) holder;
                viewHolder.getNewsTitle().setText(Html.fromHtml(curNews.title));
                viewHolder.getNewsDesc().setText(Html.fromHtml(curNews.desc));
                viewHolder.getNewsDate().setText(formatDate(curNews.date));
                break;
            }
            case TYPE_HEADER: {
                NewsHeaderModel curNews = (NewsHeaderModel) this.dataset.get(position);
                NewsHeaderViewHolder viewHolder = (NewsHeaderViewHolder) holder;
                viewHolder.getHeaderTitle().setText(curNews.title);
                break;
            }
            default: throw new IllegalArgumentException("unknown viewType=" + viewType);
        }


    }

    @Override
    public int getItemCount() {
        return this.dataset.size();
    }

    public void addNews(NewsModel curNews){
        if(this.dataset.size() == 0){
            addNewsWithHeader(curNews,0);
            return;
        }

        boolean isDateGroupFound = false;

        for(int i = 0; i < this.dataset.size(); i++){
            Object o = this.dataset.get(i);
            if(o instanceof NewsModel){
                NewsModel newsModel = (NewsModel) o;
                if(!isDateGroupFound){
                    if(formatDate(newsModel.date).equals(formatDate(curNews.date))){
                        isDateGroupFound = true;
                    } else if(newsModel.date.before(curNews.date)) {
                        addNewsWithHeader(curNews,i - 1);
                        return;
                    }
                } else if(!formatDate(newsModel.date).equals(formatDate(curNews.date))){
                    this.dataset.add(i - 1, curNews);
                    this.notifyItemInserted(i);
                    return;
                }
            }
        }

        if(isDateGroupFound){
            this.dataset.add(curNews);
            this.notifyItemRangeChanged(getItemCount() - 1, 1);
        } else {
            addNewsWithHeader(curNews, getItemCount());
        }

    }

    private void addNewsWithHeader(NewsModel curNews, int postitionStart){
        this.dataset.add(postitionStart, curNews);
        this.dataset.add(postitionStart, new NewsHeaderModel(formatDateStr(formatDate(curNews.date), null)));

        this.notifyItemRangeChanged(postitionStart, 2);
    }

    public void removeNewsById(int newsId){
        int removeNewsId = -1;
        boolean isNeedRemoveHeader = false;
        for(int i = 0; i < this.dataset.size(); i++){
            if(this.dataset.get(i) instanceof NewsModel){
                if(((NewsModel)this.dataset.get(i)).id == newsId){
                    boolean isPrevHeader = isModelHeader(i - 1);
                    boolean isNextHeaderOrNone = (i + 1) == this.dataset.size() || isModelHeader(i + 1);
                    if(i != 0 && isPrevHeader && isNextHeaderOrNone){
                        isNeedRemoveHeader = true;
                    }
                    removeNewsId = i;
                    break;
                }
            }
        }

        if(removeNewsId != -1){
            this.dataset.remove(removeNewsId);
            if(!isNeedRemoveHeader){
                this.notifyItemRemoved(removeNewsId);
            } else {
                this.dataset.remove(removeNewsId - 1);
                this.notifyItemRangeRemoved(removeNewsId - 1, 2);
            }
        }

    }

    private boolean isModelHeader(int index){
        return this.dataset.get(index) instanceof NewsHeaderModel;
    }
}
