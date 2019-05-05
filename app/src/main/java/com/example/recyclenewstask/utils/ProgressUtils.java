package com.example.recyclenewstask.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressUtils {

    private ProgressUtils(){};

    public static ProgressDialog createNetworkProgressDialog(Context context){
        ProgressDialog progress = new ProgressDialog(context);
        progress.setTitle("Загрузка");
        progress.setMessage("Загрузка данных из сети...");
        progress.setCancelable(false);
        return progress;
    }
}
