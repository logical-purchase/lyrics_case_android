package com.example.lyricscase.model;

import com.google.gson.annotations.SerializedName;

public class Song {
    @SerializedName("song_id")
    public int songId;

    @SerializedName("song_artwork")
    public String songArtwork;

    @SerializedName("song_title")
    public String songTitle;

    @SerializedName("song_lyrics")
    private String songLyrics;

    @SerializedName("artist_names")
    public String artistNames;

    @SerializedName("song_views")
    private int songViews;

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public String getSongArtwork() {
        return songArtwork;
    }

    public void setSongArtwork(String songArtwork) {
        this.songArtwork = songArtwork;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getArtistNames() { return artistNames; }

    public void setArtistNames(String artistNames) { this.artistNames = artistNames; }

    public int getSongViews() {
        return songViews;
    }

    public void setSongViews(int songViews) {
        this.songViews = songViews;
    }

    public String songViewsToString() {
        return String.valueOf(songViews);
    }

    public String getSongLyrics() {
        return songLyrics;
    }

    public void setSongLyrics(String songLyrics) {
        this.songLyrics = songLyrics;
    }
}
