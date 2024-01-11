package com.example.lyricscase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lyricscase.network.ApiAuth;
import com.example.lyricscase.network.ApiClient;
import com.example.lyricscase.network.ApiResponse;
import com.example.lyricscase.session.SessionManager;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        Button btnLogin = findViewById(R.id.btn_login);
        TextView btnSignUp = findViewById(R.id.btn_signup);
        btnSignUp.setMovementMethod(LinkMovementMethod.getInstance());

        btnLogin.setOnClickListener(view -> loginUser());
        btnSignUp.setOnClickListener(view -> signupUser());
    }

    private void signupUser() {
        String signUpUrl = "https://lyricscase.000webhostapp.com/signup";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(signUpUrl));
        startActivity(intent);
    }

    private void loginUser() {
        SessionManager sessionManager = new SessionManager(this);
        String authToken = sessionManager.getAuthToken();

        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        Context context = LoginActivity.this;

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiAuth apiAuth = ApiClient.getClient(authToken).create(ApiAuth.class);

        Call<ApiResponse> call = apiAuth.login(username, password);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse != null && apiResponse.getToken() != null) {
                        String token = apiResponse.getToken();
                        SessionManager sessionManager = new SessionManager(context);
                        sessionManager.saveAuthToken(token);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        JSONObject errorBody = new JSONObject(response.errorBody().string());
                        String errorMessage = errorBody.getJSONObject("messages").getString("error");
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}