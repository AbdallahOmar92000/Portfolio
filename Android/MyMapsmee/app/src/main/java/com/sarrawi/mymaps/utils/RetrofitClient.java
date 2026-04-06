package com.sarrawi.mymaps.utils;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static SharedPreferences sp;

    public static Retrofit getRetrofitInstance(){
        String BASE_URL = "https://networksocial.xyz/";
        return logic(BASE_URL);
    }

    public static Retrofit getRetrofitInstance2(){
        String BASE_URL = "https://maps.googleapis.com/maps/api/";
        return logic(BASE_URL);
    }

    private static Retrofit logic (String url){
        Gson gson = new GsonBuilder().serializeNulls().create();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
        return retrofit;
    }
}





