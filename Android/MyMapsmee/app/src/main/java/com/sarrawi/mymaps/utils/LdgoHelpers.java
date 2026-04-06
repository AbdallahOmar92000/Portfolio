package com.sarrawi.mymaps.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class LdgoHelpers {
    final static int MAX_WIDTH = 400;
    final static String BASE_URL = "https://maps.googleapis.com/maps/api/place/photo";
    String jwt;
    private SharedPreferences sp;

    public static String googpeMapsImage (String photo_referenc) {
        return BASE_URL + "?maxwidth=" + MAX_WIDTH + "&photo_reference=" + photo_referenc + "&key=" + API_KEY;
    }

    public LdgoHelpers (Context context) {
        sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        this.jwt = sp.getString( "jwt", "");
    }

    public String getJwt() {
        return jwt;
    }
}
