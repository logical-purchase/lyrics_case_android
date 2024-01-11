package com.example.lyricscase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lyricscase.model.Song;
import com.example.lyricscase.network.ApiClient;
import com.example.lyricscase.network.ApiResponse;
import com.example.lyricscase.network.ApiSongs;
import com.example.lyricscase.network.RequestData;
import com.example.lyricscase.session.SessionManager;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditLyricsActivity extends AppCompatActivity {

    private EditText lyricsEditText;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_lyrics);

        sessionManager = new SessionManager(this);
        String authToken = sessionManager.getAuthToken();

        ImageButton backButton = findViewById(R.id.btn_back);
        ImageButton submitButton = findViewById(R.id.btn_submit);

        lyricsEditText = findViewById(R.id.et_lyrics);

        backButton.setOnClickListener(view -> finish());

        int songId = getIntent().getIntExtra("song_id", -1);

        if (songId != -1) {

            ApiSongs apiSongs = ApiClient.getClient(authToken).create(ApiSongs.class);
            Call<ApiResponse<Song>> call = apiSongs.edit(songId);

            call.enqueue(new Callback<ApiResponse<Song>>() {
                @Override
                public void onResponse(Call<ApiResponse<Song>> call, Response<ApiResponse<Song>> response) {
                    if (response.isSuccessful()) {
                        ApiResponse<Song> apiResponse = response.body();
                        if (apiResponse != null && apiResponse.getData() != null) {
                            String lyrics = apiResponse.getData().getSongLyrics();
                            lyricsEditText.setText(lyrics);

                            submitButton.setOnClickListener(view -> updateLyrics(songId, lyricsEditText.getText().toString()));
                        } else {
                            Toast.makeText(EditLyricsActivity.this, "Error in response", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EditLyricsActivity.this, "HTTP error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Song>> call, Throwable t) {
                    Toast.makeText(EditLyricsActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void updateLyrics(int songId, String newLyrics) {
        sessionManager = new SessionManager(this);
        String authToken = sessionManager.getAuthToken();

        //Log.d("Token", authToken);
        if (authToken != null) {
            RequestData requestData = new RequestData(newLyrics);
            ApiSongs apiSongs = ApiClient.getClient(authToken).create(ApiSongs.class);

            Call<ApiResponse<Song>> call = apiSongs.update(songId, requestData);

            call.enqueue(new Callback<ApiResponse<Song>>() {
                @Override
                public void onResponse(Call<ApiResponse<Song>> call, Response<ApiResponse<Song>> response) {
                    Log.d("Response Code", String.valueOf(response.code()));
                    if (response.isSuccessful()) {
                        Toast.makeText(EditLyricsActivity.this, "Lyrics updated successfully", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(EditLyricsActivity.this, SongDetailActivity.class);
                        intent.putExtra("song_id", songId);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMessage = "You do not have access to this resource: " + response.code();
                        Toast.makeText(EditLyricsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Song>> call, Throwable t) {
                    Toast.makeText(EditLyricsActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}