package com.example.recyclenewstask;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recyclenewstask.enitites.ChosenNews;
import com.example.recyclenewstask.enitites.News;
import com.example.recyclenewstask.fragments.NewsFragment;
import com.example.recyclenewstask.model.NewsModel;
import com.example.recyclenewstask.repository.NewsRepository;

public class NewsInformationActivity extends AppCompatActivity {

    private static final String NEWS_ID_EXTRA = "NewsId";
    private final String IS_NEWS_STATUS_CHANGED = "isNewsStatusChanged";

    private News currentNews;
    private boolean isNewsWasChosenOnCreate;
    private boolean currentChoice;

    private NewsRepository newsRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_information);

        newsRepository = NewsRepository.getInstance(this);

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
        newsRepository.getNewsById(newsId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<News>() {
                    @Override
                    public void onSuccess(News news) {
                        currentNews = news;

                        getNewsStatusAndFillUi(finalNewsId);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(NewsInformationActivity.class.getName(), e.getMessage());
                        finish();
                    }
                });
    }

    private void getNewsStatusAndFillUi(int finalNewsId) {
        newsRepository.isChosenNewsById(finalNewsId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<Boolean>() {
                    @Override
                    public void onSuccess(Boolean isChosen) {
                        isNewsWasChosenOnCreate = isChosen;
                        currentChoice = isNewsWasChosenOnCreate;

                        fillNewsPage();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(NewsInformationActivity.class.getName(), e.getMessage());
                        finish();
                    }
                });
    }

    private void fillNewsPage() {
        TextView title = findViewById(R.id.newsHeaderTitle);
        title.setText(currentNews.title);

        TextView content = findViewById(R.id.newsMainContent);
        content.setText(currentNews.fullContent);

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

    private void setNewsResult(){
        if(isNewsWasChosenOnCreate && !currentChoice){
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

        if(!isNewsWasChosenOnCreate && currentChoice){
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
}
