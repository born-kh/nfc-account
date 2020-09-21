package com.example.account.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private  static String API_BASE_URL = "http://192.168.150.139/servise/api/";
   // private  static String API_BASE_URL = "http://192.168.43.199/servise/api/";
    private static Gson gson = new GsonBuilder()
            .setLenient()
            .create();
    private static Retrofit getRetrofitInstance(){
        OkHttpClient client = new OkHttpClient();
        return new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
    public static ApiService getApiService(){
        return getRetrofitInstance().create(ApiService.class);
    }

}
