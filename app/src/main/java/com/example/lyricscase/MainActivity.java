package com.example.lyricscase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lyricscase.adapter.SongAdapter;
import com.example.lyricscase.model.Song;
import com.example.lyricscase.network.ApiClient;
import com.example.lyricscase.network.ApiSongs;
import com.example.lyricscase.network.ApiResponse;
import com.example.lyricscase.session.SessionManager;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    private RecyclerView recyclerView;
    private SongAdapter songAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SessionManager sessionManager = new SessionManager(this);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ImageButton searchButton = findViewById(R.id.btn_search);
        ImageButton openDrawerButton = findViewById(R.id.btnOpenDrawer);
        recyclerView = findViewById(R.id.rvSongs);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        Menu menu = navigationView.getMenu();
        MenuItem loginItem = menu.findItem(R.id.login);
        MenuItem profileItem = menu.findItem(R.id.profile);
        MenuItem logoutItem = menu.findItem(R.id.logout);

        // HEADER
        View headerView = navigationView.getHeaderView(0);
        TextView tvUsername = headerView.findViewById(R.id.tv_username);
        TextView tvRole = headerView.findViewById(R.id.tv_user_role);

        // LOGIN CHECK
        if (sessionManager.getAuthToken() != null) {
            String username = sessionManager.getUserInfo("username");
            String rolename = sessionManager.getUserInfo("rolename");

            tvUsername.setText(username);
            tvRole.setText(rolename);

            tvUsername.setVisibility(View.VISIBLE);
            tvRole.setVisibility(View.VISIBLE);
            loginItem.setVisible(false);
            profileItem.setVisible(true);
            logoutItem.setVisible(true);
        } else {
            tvUsername.setVisibility(View.GONE);
            tvRole.setVisibility(View.GONE);
            loginItem.setVisible(true);
            profileItem.setVisible(false);
            logoutItem.setVisible(false);
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId())
            {
                case R.id.search:
                {
                    // Cierra el cajón de navegación antes de abrir SearchActivity
                    drawerLayout.closeDrawer(GravityCompat.START);

                    // Lanza la actividad de búsqueda
                    Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
                    startActivity(searchIntent);
                    break;
                }
                case R.id.login:
                {
                    drawerLayout.closeDrawer(GravityCompat.START);

                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    break;
                }
                case R.id.profile:
                {
                    String username = sessionManager.getUserInfo("username");
                    drawerLayout.closeDrawer(GravityCompat.START);
                    Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                    profileIntent.putExtra("username", username);
                    startActivity(profileIntent);
                    break;
                }
                case R.id.logout:
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Are you sure you want to log out of Lyrics Case?")
                            .setPositiveButton(Html.fromHtml("<font color='#0D6EFD'><b>Log out</b></font>"), (dialogInterface, i) -> {
                                sessionManager.clearAuthToken();

                                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            })
                            .setNegativeButton(Html.fromHtml("<font color='#0D6EFD'><b>Cancel</b></font>"), (dialogInterface, i) -> {
                                //No pasa nada XD
                            });

                    builder.create().show();

                    break;
                }
            }
            return false;
        });

        openDrawerButton.setOnClickListener(this::openDrawer);

        searchButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        showSongs();

        swipeRefreshLayout.setOnRefreshListener(this::showSongs);
    }

    public void openDrawer(View view) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void showSongs() {
        SessionManager sessionManager = new SessionManager(this);
        String authToken = sessionManager.getAuthToken();

        swipeRefreshLayout.setRefreshing(true);

        Call<ApiResponse<List<Song>>> call = ApiClient.getClient(authToken).create(ApiSongs.class).index();
        call.enqueue(new Callback<ApiResponse<List<Song>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Song>>> call, @NonNull Response<ApiResponse<List<Song>>> response) {
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful()) {
                    ApiResponse<List<Song>> apiResponse = response.body();

                    if (apiResponse != null) {
                        List<Song> songs = apiResponse.getSongs();
                        songAdapter = new SongAdapter(songs, getApplicationContext());
                        recyclerView.setAdapter(songAdapter);
                    } else {
                        Toast.makeText(MainActivity.this, "Error in response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "HTTP error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Song>>> call, @NonNull Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
