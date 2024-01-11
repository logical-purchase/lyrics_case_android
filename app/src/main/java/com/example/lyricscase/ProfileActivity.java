package com.example.lyricscase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lyricscase.model.User;
import com.example.lyricscase.network.ApiClient;
import com.example.lyricscase.network.ApiResponse;
import com.example.lyricscase.network.ApiUsers;
import com.example.lyricscase.session.SessionManager;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvUsername, tvPoints;
    private ImageView ivUserImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvUsername = findViewById(R.id.tv_username);
        tvPoints = findViewById(R.id.tv_user_points);
        ivUserImage = findViewById(R.id.iv_user_image);

        loadUserData();
    }

    private void loadUserData() {
        String username = getIntent().getStringExtra("username");

        SessionManager sessionManager = new SessionManager(this);
        String authToken = sessionManager.getAuthToken();

        Call<ApiResponse<User>> call = ApiClient.getClient(authToken).create(ApiUsers.class).show(username);
        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<User> apiResponse = response.body();
                    if (apiResponse != null) {
                        User user = response.body().getUser();
                        if (user != null) {
                            updateViews(user);
                        } else {
                            Toast.makeText(ProfileActivity.this, "User object is null", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ProfileActivity.this, "ApiResponse is null", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "HTTP error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateViews(User user) {
        if (user != null) {
            String pointText = String.format("%s points", user.getUserPoints());
            Picasso.get().load(user.getUserImage()).into(ivUserImage);
            tvUsername.setText(user.getUsername());
            tvPoints.setText(pointText);
        }
    }
}