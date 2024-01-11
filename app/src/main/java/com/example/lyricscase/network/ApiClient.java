package com.example.lyricscase.network;

import android.util.Log;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://lyricscase.000webhostapp.com/";

    public static Retrofit getClient(String authToken) {
        Log.d("Interceptor pre", "Token: " + authToken);

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        if (authToken != null && !authToken.isEmpty()) {
            Log.d("authtoken", "true");
            // Interceptor para agregar el encabezado de autorización a todas las solicitudes
            httpClientBuilder.addInterceptor(chain -> {
                okhttp3.Request original = chain.request();
                Log.d("Interceptor post", "Token: " + authToken);
                okhttp3.Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", "Bearer " + authToken)
                        .method(original.method(), original.body());
                okhttp3.Request request = requestBuilder.build();

                return chain.proceed(request);
            });
        }

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClientBuilder.build())
                .build();
    }
}
