package com.example.recyclenewstask.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.recyclenewstask.NewsInformationActivity;
import com.example.recyclenewstask.R;
import com.example.recyclenewstask.adapter.RecycleNewsAdapter;
import com.example.recyclenewstask.enitites.News;
import com.example.recyclenewstask.listeners.INewsDataPassListener;
import com.example.recyclenewstask.listeners.NewsClickListener;
import com.example.recyclenewstask.mapper.NewsMapper;
import com.example.recyclenewstask.model.NewsModel;
import com.example.recyclenewstask.network.NetworkService;
import com.example.recyclenewstask.network.data.NewsTitleDTO;
import com.example.recyclenewstask.network.data.TinkoffApiResponse;
import com.example.recyclenewstask.repository.NewsRepository;
import com.example.recyclenewstask.utils.NewsUtils;
import com.example.recyclenewstask.utils.ProgressUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.SingleSource;
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

    private static final int MAX_NEWS_COUNT = 100;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private NewsStatus newsStatus;

    private INewsDataPassListener mCallback;

    private RecycleNewsAdapter chosenNewsAdapter;
    private RecycleNewsAdapter relatedNewsAdapter;

    private NewsRepository newsRepository;

    private NetworkService networkService;

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

        newsRepository = NewsRepository.getInstance();
        networkService = NetworkService.getInstance();

        Bundle args = getArguments();
        if (args != null){
            newsStatus = (NewsStatus) args.get(NEWS_STATUS_ARG);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final SwipeRefreshLayout view = (SwipeRefreshLayout) inflater.inflate(R.layout.news_page, container, false);

        Disposable disposable = null;
        switch (newsStatus){
            case RELATED:
                disposable = getNewsForRelatedPage(view);
                break;
            case CHOSEN:
                disposable = getNewsForChosenPage(view);
                break;
        }
        compositeDisposable.add(disposable);

        return view;
    }

    private Disposable getNewsForChosenPage(final SwipeRefreshLayout view) {
        return newsRepository.getAllChosenNewsByIds()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableMaybeObserver<List<News>>() {
                    @Override
                    public void onSuccess(List<News> news) {
                        chosenNewsAdapter = getRecycleViewNewsAdapterForView(view, news);
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
    }

    private Disposable getNewsForRelatedPage(final SwipeRefreshLayout view) {
        final ProgressDialog progressDialog = ProgressUtils.createNetworkProgressDialog(getContext());
        progressDialog.show();
        return networkService
                .getNewsApi()
                .getAllNews()
                .map(new Function<TinkoffApiResponse<List<NewsTitleDTO>>, List<News>>() {
                    @Override
                    public List<News> apply(TinkoffApiResponse<List<NewsTitleDTO>> listTinkoffApiResponse) throws Exception {
                        return mapApiResponse(listTinkoffApiResponse);
                    }
                })
                .onErrorResumeNext(new Function<Throwable, SingleSource<? extends List<News>>>() {
                    @Override
                    public SingleSource<? extends List<News>> apply(Throwable throwable) throws Exception {
                        showNetworkError();

                        return newsRepository.getAllNews();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<News>>() {
                    @Override
                    public void onSuccess(List<News> news) {
                        relatedNewsAdapter = getRecycleViewNewsAdapterForView(view, news);

                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.dismiss();
                        Log.e(NewsFragment.class.getName(), e.getMessage());
                    }
                });
    }

    private RecycleNewsAdapter createRecycleViewForNews(final SwipeRefreshLayout view, final List<Object> newsObjects){
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

        view.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(newsStatus == NewsStatus.RELATED){
                    Disposable disposable = networkService
                            .getNewsApi()
                            .getAllNews()
                            .map(new Function<TinkoffApiResponse<List<NewsTitleDTO>>, List<News>>() {
                                @Override
                                public List<News> apply(TinkoffApiResponse<List<NewsTitleDTO>> listTinkoffApiResponse) throws Exception {
                                    return mapApiResponse(listTinkoffApiResponse);
                                }
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<List<News>>() {
                                @Override
                                public void onSuccess(List<News> news) {
                                    view.setRefreshing(false);

                                    relatedNewsAdapter.updateAllNews(mapListNewsToGroups(news));
                                }

                                @Override
                                public void onError(Throwable e) {
                                    view.setRefreshing(false);
                                    showNetworkError();
                                }
                            });
                    compositeDisposable.add(disposable);
                }
            }
        });

        return newsAdapter;
    }

    private RecycleNewsAdapter getRecycleViewNewsAdapterForView(final SwipeRefreshLayout view, List<News> newsList){
        return createRecycleViewForNews(view, mapListNewsToGroups(newsList));
    }

    private List<News> mapApiResponse(TinkoffApiResponse<List<NewsTitleDTO>> listTinkoffApiResponse){
        List<NewsTitleDTO> newsTitles = listTinkoffApiResponse.getPayload();
        List<News> newsList = new ArrayList<>();
        for(NewsTitleDTO newsTitleDTO : newsTitles){
            News news = NewsMapper.mapNewsTitleDTOToEntity(newsTitleDTO);
            newsList.add(news);
        }

        return newsList;
    }

    private List<Object> mapListNewsToGroups(List<News> newsList){
        final int maxNews = newsList.size() > MAX_NEWS_COUNT ? MAX_NEWS_COUNT : newsList.size();
        return NewsUtils.createNewsObjectsForDateGroups(
                NewsUtils.groupNewsByDate(mapNewsListEntityToModel(newsList.subList(0, maxNews), false)),
                getContext()
        );
    }

    private void showNetworkError(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getContext(), "Ошибка доступа к интернету", Toast.LENGTH_LONG);
                toast.show();
            }
        });
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null){
            if(data.getBooleanExtra(IS_NEWS_STATUS_CHANGED, false)) {
                mCallback.passData(data.getIntExtra(NEWS_ID, -1));
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}
