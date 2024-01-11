package com.example.lyricscase.network;

import com.example.lyricscase.model.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiUsers {
    @GET("api/v1/user/{username}")
    Call<ApiResponse<User>> show(@Path("username") String username);
}
