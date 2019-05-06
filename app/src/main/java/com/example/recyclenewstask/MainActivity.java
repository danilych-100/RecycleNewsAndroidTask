package com.example.recyclenewstask;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import com.example.recyclenewstask.fragments.NewsFragment;
import com.example.recyclenewstask.fragments.NewsFragmentPagerAdapter;
import com.example.recyclenewstask.listeners.INewsDataPassListener;
import com.example.recyclenewstask.service.ClearDBScheduler;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity implements INewsDataPassListener {

    private static final int CHOSEN_FRAGMENT_NUM = 1;

    private NewsFragmentPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createTabLayout();
        createDBCleaner();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createTabLayout() {
        adapter = new NewsFragmentPagerAdapter(getSupportFragmentManager(), this);
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public void passData(int newsId) {
        if(adapter != null){
            NewsFragment pageFragment = adapter.getFragmentByPosition(CHOSEN_FRAGMENT_NUM);
            pageFragment.onNewsChanged(newsId);
        }
    }

    private void createDBCleaner(){
        ComponentName serviceComponent = new ComponentName(this, ClearDBScheduler.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(1000); // wait at least
        builder.setOverrideDeadline(30 * 1000); // maximum delay
        JobScheduler jobScheduler = this.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }
}
