package com.ruenzuo.babel.helpers;

import android.content.Context;

import com.securepreferences.SecurePreferences;

/**
 * Created by renzocrisostomo on 17/08/14.
 */
public class SecureStorageHelper {

    private SecurePreferences securePreferences;
    private static final String TOKEN_KEY = "TOKEN_KEY";
    public static final String NO_KEY_FOUND = "NO_KEY_FOUND";

    public SecureStorageHelper(Context context) {
        securePreferences = new SecurePreferences(context);
    }

    public boolean store(String token) {
        return securePreferences.edit().putString(TOKEN_KEY, token).commit();
    }

    public String retrieveToken() {
        return securePreferences.getString(TOKEN_KEY, NO_KEY_FOUND);
    }

}
