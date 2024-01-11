package com.example.lyricscase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lyricscase.model.Song;
import com.example.lyricscase.network.ApiClient;
import com.example.lyricscase.network.ApiResponse;
import com.example.lyricscase.network.ApiSongs;
import com.example.lyricscase.session.SessionManager;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SongDetailActivity extends AppCompatActivity {

    private ImageView ivArtwork;
    private TextView tvToolbarTitle, tvTitle, tvArtists, tvViews, tvLyrics;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_detail);

        sessionManager = new SessionManager(this);

        ImageButton backButton = findViewById(R.id.btn_back);
        ImageButton shareButton = findViewById(R.id.btn_share);

        shareButton.setOnClickListener(view -> shareSongLink());
        backButton.setOnClickListener(view -> finish());

        Button editLyricsButton = findViewById(R.id.btn_edit_lyrics);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        tvToolbarTitle = findViewById(R.id.toolbarTitle);
        ivArtwork = findViewById(R.id.iv_artwork);
        tvTitle = findViewById(R.id.tv_title);
        tvArtists = findViewById(R.id.tv_artists);
        tvViews = findViewById(R.id.tv_views);
        tvLyrics = findViewById(R.id.tv_lyrics);

        swipeRefreshLayout.setOnRefreshListener(this::loadSongData);

        loadSongData();

        int songId = getIntent().getIntExtra("song_id", -1);

        editLyricsButton.setOnClickListener(view -> {
            Intent intent = new Intent(SongDetailActivity.this, EditLyricsActivity.class);
            intent.putExtra("song_id", songId);
            startActivity(intent);
        });

        if (sessionManager.getAuthToken() != null) {
            editLyricsButton.setVisibility(View.VISIBLE);
        } else {
            editLyricsButton.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        swipeRefreshLayout.setOnRefreshListener(this::loadSongData);

        loadSongData();
    }

    private void loadSongData() {
        int songId = getIntent().getIntExtra("song_id", -1);

        sessionManager = new SessionManager(this);
        String authToken = sessionManager.getAuthToken();

        swipeRefreshLayout.setRefreshing(true);

        if (songId != -1) {
            Call<ApiResponse<Song>> call = ApiClient.getClient(authToken).create(ApiSongs.class).show(songId);
            call.enqueue(new Callback<ApiResponse<Song>>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse<Song>> call, @NonNull Response<ApiResponse<Song>> response) {
                    swipeRefreshLayout.setRefreshing(false);

                    Log.d("API Response Code", "Code: " + response.code());
                    if (response.isSuccessful()) {
                        ApiResponse<Song> apiResponse = response.body();
                        if (apiResponse != null) {
                            Song song = response.body().getData();
                            Log.d("SDA", "Song: " + song);
                            if (song != null) {
                                updateViews(song);
                            } else {
                                Toast.makeText(SongDetailActivity.this, "Song object is null", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SongDetailActivity.this, "ApiResponse is null", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SongDetailActivity.this, "HTTP error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Song>> call, Throwable t) {
                    swipeRefreshLayout.setRefreshing(false);

                    Log.e("API Failure", "Error: " + t.getMessage());
                    Toast.makeText(SongDetailActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(SongDetailActivity.this, "Song not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateViews(Song song) {
        if (song != null) {
            Picasso.get().load(song.getSongArtwork()).into(ivArtwork);
            String toolbarTitleText = String.format("\"%s\" lyrics", song.getSongTitle());
            String viewsText = String.format("%s views", song.getSongViews());
            tvToolbarTitle.setText(toolbarTitleText);
            tvTitle.setText(song.getSongTitle());
            tvArtists.setText(song.getArtistNames());
            tvViews.setText(viewsText);
            tvLyrics.setText(song.getSongLyrics());
        } else {
            Toast.makeText(SongDetailActivity.this, "Song is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareSongLink() {
        // Obtener el ID de la canción desde los extras del intent
        int songId = getIntent().getIntExtra("song_id", -1);

        // Verificar si se ha proporcionado un ID de canción válido
        if (songId != -1) {
            // Construir el enlace de la canción utilizando el ID
            String songLink = "https://lyricscase.000webhostapp.com/songs/" + songId;

            // Crear un nuevo intent para compartir
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            // Establecer el tipo de contenido del mensaje como texto plano
            shareIntent.setType("text/plain");
            // Agregar el enlace de la canción como texto extra al mensaje
            shareIntent.putExtra(Intent.EXTRA_TEXT, songLink);

            // Iniciar la actividad de compartir con un selector de aplicaciones
            startActivity(Intent.createChooser(shareIntent, "Share song lyrics"));
        } else {
            // Mostrar un mensaje de error si no se encuentra el ID de la canción
            Toast.makeText(SongDetailActivity.this, "Song not found", Toast.LENGTH_SHORT).show();
        }
    }
}