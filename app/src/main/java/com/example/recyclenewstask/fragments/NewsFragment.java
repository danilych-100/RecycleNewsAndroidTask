package com.example.recyclenewstask.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.recyclenewstask.NewsInformationActivity;
import com.example.recyclenewstask.R;
import com.example.recyclenewstask.RecycleNewsAdapter;
import com.example.recyclenewstask.listeners.NewsClickListener;
import com.example.recyclenewstask.model.NewsModel;
import com.example.recyclenewstask.repository.NewsRepository;
import com.example.recyclenewstask.utils.NewsUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.recyclenewstask.MainActivity.NEWS_ID;

public class NewsFragment extends Fragment {


    private static final String NEWS_STATUS_ARG = "NewsStatusArg";

    private NewsStatus newsStatus;

    private RecycleNewsAdapter chosenNewsAdapter;

    public static NewsFragment newInstance(NewsStatus status) {
        Bundle args = new Bundle();
        args.putSerializable(NEWS_STATUS_ARG, status);
        NewsFragment fragment = new NewsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null){
            newsStatus = (NewsStatus) args.get(NEWS_STATUS_ARG);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_page, container, false);

        switch (newsStatus){
            case RELATED:
                createRecycleViewForNews(view, NewsRepository.getStubNews());
                break;
            case CHOSEN:
                chosenNewsAdapter = createRecycleViewForNews(view, NewsRepository.getChosenNews());
                break;
        }

        return view;
    }

    public void onNewsChanged(int newsId){
        NewsModel newsModel = NewsRepository.getNewsById(newsId);
        if(newsModel != null){
            if(newsModel.isChosen){
                this.chosenNewsAdapter.addNews(newsModel);
            } else {
                this.chosenNewsAdapter.removeNewsById(newsId);
            }
        }
    }

    private RecycleNewsAdapter createRecycleViewForNews(final View view, final List<NewsModel> news){
        LinearLayoutManager viewManager = new LinearLayoutManager(view.getContext());
        RecycleNewsAdapter newsAdapter = new RecycleNewsAdapter(
                news,
                new NewsClickListener() {
                    @Override
                    public void onNewsClick(NewsModel news) {
                        Intent intent = new Intent(getActivity(), NewsInformationActivity.class);
                        intent.putExtra(NEWS_ID, news.id);
                        startActivityForResult(intent, 1);
                    }
                });

        RecyclerView recyclerView = view.findViewById(R.id.newsRecycleView);
        recyclerView.setLayoutManager(viewManager);
        recyclerView.setAdapter(newsAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(
                recyclerView.getContext(),
                viewManager.getOrientation())
        );

        return newsAdapter;
    }
}
