package com.example.lyricscase.network;

import com.example.lyricscase.model.Song;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiSongs {
    @GET("api/v1/songs")
    Call<ApiResponse<List<Song>>> index();

    @GET("api/v1/songs/{id}")
    Call<ApiResponse<Song>> show(@Path("id") int songId);

    @GET("api/v1/songs/edit/{id}")
    Call<ApiResponse<Song>> edit(@Path("id") int songId);

    @POST("api/v1/songs/update/{id}")
    Call<ApiResponse<Song>> update(@Path("id") int songId, @Body RequestData requestData);

    @GET("api/v1/search/{term}")
    Call<ApiResponse<List<Song>>> search(@Path("term") String term);
}
