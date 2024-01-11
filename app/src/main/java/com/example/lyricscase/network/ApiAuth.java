package com.example.lyricscase.network;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiAuth {
    @FormUrlEncoded
    @POST("api/v1/auth/login")
    Call<ApiResponse> login(
            @Field("username") String username,
            @Field("password") String password
    );
}
