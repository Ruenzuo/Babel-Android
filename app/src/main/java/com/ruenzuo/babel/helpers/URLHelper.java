package com.ruenzuo.babel.helpers;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.ruenzuo.babel.R;
import com.ruenzuo.babel.models.Language;
import com.ruenzuo.babel.models.Repository;

/**
 * Created by renzocrisostomo on 17/08/14.
 */
public class URLHelper {

    public static final String GITHUB_CLIENT_ID = "134fde19a1854aa20f4f";
    public static final String GITHUB_CLIENT_SECRET = "5aecca077a31c7f35af8a21146d7738ad47f1390";
    public static final String GITHUB_API_BASE_URL = "https://api.github.com/";

    public static String getURLStringForAuthorization() {
        return "https://github.com/login/oauth/authorize?client_id=" + GITHUB_CLIENT_ID;
    }

    public static String getURLStringForAccessToken(String code) {
        return "https://github.com/login/oauth/access_token?client_id=" + GITHUB_CLIENT_ID +
                "&client_secret=" + GITHUB_CLIENT_SECRET +
                "&code=" + code;
    }

    public static String getURLStringForTokenValidity(String token) {
        return GITHUB_API_BASE_URL + "applications/" + GITHUB_CLIENT_ID + "/tokens/" + token;
    }

    public static String getURLStringForRepositories(String query) {
        return GITHUB_API_BASE_URL + "search/repositories?" + query;
    }

    public static String getURLStringForFiles(Language language, Repository repository, String token) {
        return GITHUB_API_BASE_URL + "search/code?q=language:" + language.getSearch() + "+repo:" + repository.getName() + "&token=" + token;
    }

    public static String getUserAgent(Context context) {
        String versionName;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException ex) {
            versionName = "Unknown";
        }
        return context.getString(R.string.app_name) + "/" + versionName;
    }

}
