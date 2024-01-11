package com.example.lyricscase.session;

import android.util.Base64;

import java.io.UnsupportedEncodingException;

public class JwtUtils {

    public static String[] decodeJWT(String jwt) {
        String[] parts = jwt.split("\\.");

        try {
            String header = decodeBase64(parts[0]);
            String payload = decodeBase64(parts[1]);

            return new String[]{header, payload};
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String decodeBase64(String encoded) throws UnsupportedEncodingException {
        byte[] base64 = Base64.decode(encoded, Base64.URL_SAFE);
        return new String(base64, "UTF-8");
    }
}
