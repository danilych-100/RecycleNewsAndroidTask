package com.example.recyclenewstask;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import io.reactivex.CompletableSource;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recyclenewstask.enitites.ChosenNews;
import com.example.recyclenewstask.enitites.News;
import com.example.recyclenewstask.network.NetworkService;
import com.example.recyclenewstask.network.data.NewsItemDetails;
import com.example.recyclenewstask.network.data.NewsTitleDTO;
import com.example.recyclenewstask.network.data.TinkoffApiResponse;
import com.example.recyclenewstask.repository.NewsRepository;
import com.example.recyclenewstask.utils.ProgressUtils;

import java.util.Date;

public class NewsInformationActivity extends AppCompatActivity {

    private static final String NEWS_ID_EXTRA = "NewsId";
    private final String IS_NEWS_STATUS_CHANGED = "isNewsStatusChanged";

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private News currentNews;
    private boolean isNewsWasChosenOnCreate;
    private boolean currentChoice;

    private NewsRepository newsRepository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_information);

        newsRepository = NewsRepository.getInstance();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle bundle = getIntent().getExtras();
        int newsId = -1;
        if (bundle != null) {
            newsId = bundle.getInt(NEWS_ID_EXTRA);
        }

        final int finalNewsId = newsId;
        final ProgressDialog progressDialog = ProgressUtils.createNetworkProgressDialog(this);
        progressDialog.show();
        Disposable disposable = NetworkService.getInstance()
                .getNewsApi()
                .getNewsById(finalNewsId)
                .map(new Function<TinkoffApiResponse<NewsItemDetails>, News>() {
                    @Override
                    public News apply(TinkoffApiResponse<NewsItemDetails> tinkoffApiResponse) throws Exception {
                        NewsItemDetails newsItemDetails = tinkoffApiResponse.getPayload();
                        NewsTitleDTO title = newsItemDetails.getTitle();
                        final News news = new News();
                        news.id = title.getId();
                        news.date = new Date(title.getPublicationDate().getMilliseconds());
                        news.desc = title.getTitle();
                        news.title = title.getTitle();
                        news.fullContent = newsItemDetails.getContent();
                        return news;
                    }
                })
                .onErrorResumeNext(new Function<Throwable, SingleSource<? extends News>>() {
                    @Override
                    public SingleSource<? extends News> apply(Throwable throwable) throws Exception {
                        Log.e(NewsInformationActivity.class.getName(), throwable.getMessage());
                        return newsRepository.getNewsById(finalNewsId);
                    }
                })
                .flatMapCompletable(new Function<News, CompletableSource>() {
                    @Override
                    public CompletableSource apply(News news) throws Exception {
                        currentNews = news;

                        return newsRepository.saveNews(news);
                    }
                })
                .andThen(newsRepository.isChosenNewsById(finalNewsId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Boolean>() {
                    @Override
                    public void onSuccess(Boolean isChosen) {
                        progressDialog.dismiss();
                        isNewsWasChosenOnCreate = isChosen;
                        currentChoice = isNewsWasChosenOnCreate;

                        fillNewsPage();
                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.dismiss();
                        Log.e(NewsInformationActivity.class.getName(), e.getMessage());
                        finish();
                    }
                });

        compositeDisposable.add(disposable);
    }

    private void fillNewsPage() {
        TextView title = findViewById(R.id.newsHeaderTitle);
        title.setText(HtmlCompat.fromHtml(currentNews.title, HtmlCompat.FROM_HTML_MODE_COMPACT));

        TextView content = findViewById(R.id.newsMainContent);
        content.setText(HtmlCompat.fromHtml(currentNews.fullContent, HtmlCompat.FROM_HTML_MODE_COMPACT));

        if (currentChoice) {
            ImageButton imageButton = findViewById(R.id.chosenButton);
            imageButton.setActivated(true);
            imageButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star_big_on));
        }
    }

    public void onToggleStar(View view) {
        if (view.isActivated()) {
            Toast toast = Toast.makeText(this, "Новость удалена из избранного", Toast.LENGTH_LONG);
            toast.show();
            ((ImageButton) view).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star_big_off));
            currentChoice = false;
        } else {
            Toast toast = Toast.makeText(this, "Новость добавлена в избранное", Toast.LENGTH_LONG);
            toast.show();
            ((ImageButton) view).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star_big_on));
            currentChoice = true;
        }

        view.setActivated(!view.isActivated());
    }

    @Override
    public void onBackPressed() {
        setNewsResult();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setNewsResult();
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setNewsResult() {
        if (isNewsWasChosenOnCreate && !currentChoice) {
            newsRepository.deleteByNewsId(currentNews.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableCompletableObserver() {
                        @Override
                        public void onComplete() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(NewsInformationActivity.class.getName(), e.getMessage());
                        }
                    });
        }

        if (!isNewsWasChosenOnCreate && currentChoice) {
            newsRepository.insertChosenNews(new ChosenNews(currentNews.id))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableCompletableObserver() {
                        @Override
                        public void onComplete() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(NewsInformationActivity.class.getName(), e.getMessage());
                        }
                    });
        }

        Intent intent = new Intent();
        intent.putExtra(NEWS_ID_EXTRA, currentNews.id);
        intent.putExtra(IS_NEWS_STATUS_CHANGED, isNewsWasChosenOnCreate != currentChoice);
        setResult(RESULT_OK, intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}
