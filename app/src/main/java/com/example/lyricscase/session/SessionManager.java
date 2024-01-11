package com.example.lyricscase.session;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

public class SessionManager {

    private static final String PREF_NAME = "LyricsCaseSession";
    private static final String KEY_TOKEN = "token";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveAuthToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public String[] decodeToken() {
        String authToken = getAuthToken();

        if (authToken != null) {
            return JwtUtils.decodeJWT(authToken);
        }

        return null;
    }

    public String getUserInfo(String key) {
        String[] decodedToken = decodeToken();

        if (decodedToken != null && decodedToken.length > 1) {
            String payload = decodedToken[1];
            return parseUserInfo(payload, key);
        }

        return null;
    }

    public String getAuthToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public void clearAuthToken() {
        editor.remove(KEY_TOKEN);
        editor.apply();
    }

    private String parseUserInfo(String payload, String key) {
        try {
            JSONObject jsonPayload = new JSONObject(payload);
            JSONObject jsonData = jsonPayload.getJSONObject("data");
            return jsonData.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
