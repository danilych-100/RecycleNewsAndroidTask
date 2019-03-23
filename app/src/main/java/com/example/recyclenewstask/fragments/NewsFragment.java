package com.example.recyclenewstask.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.recyclenewstask.NewsInformationActivity;
import com.example.recyclenewstask.R;
import com.example.recyclenewstask.adapter.RecycleNewsAdapter;
import com.example.recyclenewstask.enitites.News;
import com.example.recyclenewstask.listeners.INewsDataPassListener;
import com.example.recyclenewstask.listeners.NewsClickListener;
import com.example.recyclenewstask.model.NewsHeaderModel;
import com.example.recyclenewstask.model.NewsModel;
import com.example.recyclenewstask.repository.NewsRepository;
import com.example.recyclenewstask.utils.NewsUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.recyclenewstask.mapper.NewsMapper.mapNewsEntityToModel;
import static com.example.recyclenewstask.mapper.NewsMapper.mapNewsListEntityToModel;

public class NewsFragment extends Fragment {

    private static final String NEWS_ID = "NewsId";
    private static final String IS_NEWS_STATUS_CHANGED = "isNewsStatusChanged";

    private static final String NEWS_STATUS_ARG = "NewsStatusArg";

    private NewsStatus newsStatus;

    private INewsDataPassListener mCallback;

    private RecycleNewsAdapter chosenNewsAdapter;

    private NewsRepository newsRepository;

    public static NewsFragment newInstance(NewsStatus status) {
        Bundle args = new Bundle();
        args.putSerializable(NEWS_STATUS_ARG, status);
        NewsFragment fragment = new NewsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try
        {
            mCallback = (INewsDataPassListener) context;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(context.toString()+ " must implement INewsDataPassListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        newsRepository = NewsRepository.getInstance(getContext());
        Bundle args = getArguments();
        if (args != null){
            newsStatus = (NewsStatus) args.get(NEWS_STATUS_ARG);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_page, container, false);

        List<Object> newsObjects;
        switch (newsStatus){
            case RELATED:
                newsObjects = NewsUtils.createNewsObjectsForDateGroups(
                        NewsUtils.groupNewsByDate(mapNewsListEntityToModel(
                                newsRepository.getAllNews(),
                                false)
                        ),
                        getContext()
                );
                createRecycleViewForNews(view, newsObjects);
                break;
            case CHOSEN:
                newsObjects = NewsUtils.createNewsObjectsForDateGroups(
                        NewsUtils.groupNewsByDate(mapNewsListEntityToModel(
                                newsRepository.getAllChosenNewsByIds(),
                                true)
                        ),
                        getContext()
                );
                chosenNewsAdapter = createRecycleViewForNews(view, newsObjects);
                break;
        }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null){
            if(data.getBooleanExtra(IS_NEWS_STATUS_CHANGED, false)) {
                mCallback.passData(data.getIntExtra(NEWS_ID, -1));
            }
        }
    }

    public void onNewsChanged(int newsId){
        News news = newsRepository.getNewsById(newsId);
        NewsModel newsModel = mapNewsEntityToModel(news, newsRepository.isChosenNewsById(newsId));
        if(newsModel != null && chosenNewsAdapter != null){
            if(newsModel.isChosen){
                chosenNewsAdapter.addNews(newsModel);
            } else {
                chosenNewsAdapter.removeNewsById(newsId);
            }
        }
    }

    private RecycleNewsAdapter createRecycleViewForNews(final View view, final List<Object> newsObjects){
        LinearLayoutManager viewManager = new LinearLayoutManager(view.getContext());
        RecycleNewsAdapter newsAdapter = new RecycleNewsAdapter(
                newsObjects,
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
