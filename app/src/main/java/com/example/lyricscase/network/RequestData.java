package com.example.lyricscase.network;

import com.google.gson.annotations.SerializedName;

public class RequestData {
    @SerializedName("song_lyrics")
    private String songLyrics;

    public RequestData(String songLyrics) {
        this.songLyrics = songLyrics;
    }
}
