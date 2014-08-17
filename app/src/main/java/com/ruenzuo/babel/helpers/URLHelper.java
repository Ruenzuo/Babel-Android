package com.ruenzuo.babel.helpers;

/**
 * Created by renzocrisostomo on 17/08/14.
 */
public class URLHelper {

    private static final String GITHUB_CLIENT_ID = "134fde19a1854aa20f4f";
    private static final String GITHUB_CLIENT_SECRET = "5aecca077a31c7f35af8a21146d7738ad47f1390";

    public static String getURLForAuthorization() {
        return "https://github.com/login/oauth/authorize?client_id=" + GITHUB_CLIENT_ID;
    }

    public static String getURLForAccessToken(String code) {
        return "https://github.com/login/oauth/access_token?client_id=" + GITHUB_CLIENT_ID +
                "&client_secret=" + GITHUB_CLIENT_SECRET +
                "&code=" + code;
    }

}
