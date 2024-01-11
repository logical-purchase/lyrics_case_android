package com.example.lyricscase.network;

import com.google.gson.annotations.SerializedName;

public class ApiResponse<T> {
    @SerializedName("songs")
    private T songs;
    @SerializedName("song")
    private T data;
    @SerializedName("token")
    private String token;
    @SerializedName("user")
    private T user;

    public T getSongs() {
        return songs;
    }
    public T getData() {
        return data;
    }
    public String getToken() {
        return token;
    }
    public T getUser() { return user; }
}
