package com.example.recyclenewstask;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recyclenewstask.model.NewsModel;
import com.example.recyclenewstask.repository.NewsRepository;

public class NewsInformationActivity extends AppCompatActivity {

    private static final String NEWS_ID_EXTRA = "NewsId";
    private final String IS_NEWS_STATUS_CHANGED = "isNewsStatusChanged";

    private NewsModel currentNews;
    private boolean isNewsWasChosenOnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_information);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle bundle = getIntent().getExtras();
        int newsId = -1;
        if (bundle != null) {
            newsId = bundle.getInt(NEWS_ID_EXTRA);
        }

        currentNews = NewsRepository.getNewsById(newsId);

        if (currentNews == null) {
            finish();
        }

        isNewsWasChosenOnCreate = currentNews.isChosen;

        fillNewsPage();
    }

    private void fillNewsPage() {
        TextView title = findViewById(R.id.newsHeaderTitle);
        title.setText(currentNews.title);

        TextView content = findViewById(R.id.newsMainContent);
        content.setText(currentNews.fullContent);

        if (currentNews.isChosen) {
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
            currentNews.isChosen = false;
        } else {
            Toast toast = Toast.makeText(this, "Новость добавлена в избранное", Toast.LENGTH_LONG);
            toast.show();
            ((ImageButton) view).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star_big_on));
            currentNews.isChosen = true;
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
        boolean isChangedStatus = isNewsWasChosenOnCreate != currentNews.isChosen;
        if (isChangedStatus) {
            NewsRepository.update(currentNews);
        }
        Intent intent = new Intent();
        intent.putExtra(NEWS_ID_EXTRA, currentNews.id);
        intent.putExtra(IS_NEWS_STATUS_CHANGED, isChangedStatus);
        setResult(RESULT_OK, intent);
    }
}
