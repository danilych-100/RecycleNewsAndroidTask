package com.example.recyclenewstask.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {

    private static volatile NetworkService INSTANCE;
    private static final String BASE_URL = "https://api.tinkoff.ru/";
    private Retrofit retrofit;

    private NetworkService() {}

    public static NetworkService getInstance() {
        if(INSTANCE == null){
            synchronized (NetworkService.class){
                if(INSTANCE == null){
                    INSTANCE = new NetworkService();
                }
            }
        }
        return INSTANCE;
    }

    public void initService(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .addInterceptor(interceptor);

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client.build())
                .build();
    }

    public NewsApi getNewsApi(){
        return retrofit.create(NewsApi.class);
    }
}
