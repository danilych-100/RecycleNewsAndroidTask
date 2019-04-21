package com.example.recyclenewstask.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.example.recyclenewstask.mapper.NewsMapper.mapNewsEntityToModel;
import static com.example.recyclenewstask.mapper.NewsMapper.mapNewsListEntityToModel;

public class NewsFragment extends Fragment {

    private static final String NEWS_ID = "NewsId";
    private static final String IS_NEWS_STATUS_CHANGED = "isNewsStatusChanged";

    private static final String NEWS_STATUS_ARG = "NewsStatusArg";

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

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
        final View view = inflater.inflate(R.layout.news_page, container, false);

        Disposable disposable = null;
        switch (newsStatus){
            case RELATED:
                disposable = newsRepository.getAllNews()
                        .map(new Function<List<News>, List<News>>() {
                            @Override
                            public List<News> apply(List<News> news) throws Exception {
                                List<News> sortedList = new ArrayList<>(news);
                                Collections.sort(sortedList, new Comparator<News>() {
                                    @Override
                                    public int compare(News o1, News o2) {
                                        return o1.date.compareTo(o2.date);
                                    }
                                });
                                return sortedList;
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableMaybeObserver<List<News>>() {

                            @Override
                            public void onSuccess(List<News> news) {
                                List<Object> newsObjects = NewsUtils.createNewsObjectsForDateGroups(
                                        NewsUtils.groupNewsByDate(mapNewsListEntityToModel(news, false)),
                                        getContext()
                                );
                                createRecycleViewForNews(view, newsObjects);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(NewsFragment.class.getName(), e.getMessage());
                            }

                            @Override
                            public void onComplete() {
                                createRecycleViewForNews(view, new ArrayList<>());
                            }
                        });
                break;
            case CHOSEN:
                disposable = newsRepository.getAllChosenNewsByIds()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableMaybeObserver<List<News>>() {

                            @Override
                            public void onSuccess(List<News> news) {
                                List<Object> newsObjects = NewsUtils.createNewsObjectsForDateGroups(
                                        NewsUtils.groupNewsByDate(mapNewsListEntityToModel(news, false)),
                                        getContext()
                                );
                                chosenNewsAdapter = createRecycleViewForNews(view, newsObjects);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(NewsFragment.class.getName(), e.getMessage());
                            }

                            @Override
                            public void onComplete() {
                                chosenNewsAdapter = createRecycleViewForNews(view, new ArrayList<>());
                            }
                        });
                break;
        }
        compositeDisposable.add(disposable);

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

    public void onNewsChanged(final int newsId){
        Disposable disposable = newsRepository.getNewsById(newsId)
                .zipWith(newsRepository.isChosenNewsById(newsId), new BiFunction<News, Boolean, NewsModel>() {
                    @Override
                    public NewsModel apply(News news, Boolean isChosen) throws Exception {
                        return mapNewsEntityToModel(news, isChosen);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<NewsModel>() {
                    @Override
                    public void onSuccess(NewsModel news) {
                        if(news != null && chosenNewsAdapter != null){
                            if(news.isChosen){
                                chosenNewsAdapter.addNews(news);
                            } else {
                                chosenNewsAdapter.removeNewsById(newsId);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(NewsFragment.class.getName(), e.getMessage());
                    }
                });
        compositeDisposable.add(disposable);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}
