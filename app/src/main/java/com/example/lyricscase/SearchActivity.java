package com.example.lyricscase;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lyricscase.adapter.SongAdapter;
import com.example.lyricscase.model.Song;
import com.example.lyricscase.network.ApiClient;
import com.example.lyricscase.network.ApiResponse;
import com.example.lyricscase.network.ApiSongs;
import com.example.lyricscase.session.SessionManager;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    private EditText searchEditText;
    private RecyclerView recyclerView;
    private TextView resultsTextView;
    private TextView noResultsTextView;
    private SongAdapter songAdapter;
    private ApiSongs apiSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ImageButton cancelButton = findViewById(R.id.btn_cancel);
        ImageButton backButton = findViewById(R.id.btn_back);
        searchEditText = findViewById(R.id.et_search);
        recyclerView = findViewById(R.id.rv_results);
        resultsTextView = findViewById(R.id.tv_results);
        noResultsTextView = findViewById(R.id.tv_no_results);

        SessionManager sessionManager = new SessionManager(this);
        String authToken = sessionManager.getAuthToken();
        apiSongs = ApiClient.getClient(authToken).create(ApiSongs.class);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        songAdapter = new SongAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(songAdapter);

        searchEditText.requestFocus();
        new Handler().postDelayed(() -> showKeyboard(searchEditText), 200);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                performSearch(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Verifica si el texto en el EditText está vacío
                if (editable.toString().trim().isEmpty()) {
                    // Si está vacío, oculta el RecyclerView y el TextView
                    recyclerView.setVisibility(View.GONE);
                    resultsTextView.setVisibility(View.GONE);
                    noResultsTextView.setVisibility(View.GONE);
                }
            }
        });

        cancelButton.setOnClickListener(view -> searchEditText.getText().clear());
        backButton.setOnClickListener(view -> finish());
    }

    private void performSearch(String searchTerm) {
        if (searchTerm.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            resultsTextView.setVisibility(View.GONE);
            noResultsTextView.setVisibility(View.GONE);
        } else {
            Call<ApiResponse<List<Song>>> call = apiSongs.search(searchTerm);
            call.enqueue(new Callback<ApiResponse<List<Song>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<Song>>> call, Response<ApiResponse<List<Song>>> response) {
                    if (response.isSuccessful()) {
                        ApiResponse<List<Song>> apiResponse = response.body();
                        List<Song> songs = apiResponse.getSongs();
                        recyclerView.setVisibility(View.VISIBLE);
                        resultsTextView.setVisibility(View.VISIBLE);
                        noResultsTextView.setVisibility(View.GONE);
                        songAdapter.updateData(songs);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        resultsTextView.setVisibility(View.GONE);
                        noResultsTextView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<Song>>> call, Throwable t) {
                    Toast.makeText(SearchActivity.this, "Fallo en la llamada de búsqueda", Toast.LENGTH_SHORT).show();
                    Log.e("SearchActivity", "Fallo en la llamada de búsqueda: " + t.getMessage());
                }
            });
        }
    }

    private void showKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }
}