package com.khwish.app.retrofit;

import com.khwish.app.constants.APIConstants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class ServerAPIBuilder {

    private static ServerApiService apiService;

    private static void initRetrofit() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

        Retrofit retrofitObj = new Retrofit.Builder()
                .baseUrl(APIConstants.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        apiService = retrofitObj.create(ServerApiService.class);
    }

    static ServerApiService getApiService() {
        if (apiService == null) {
            initRetrofit();
        }
        return apiService;
    }
}
