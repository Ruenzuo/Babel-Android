package com.ruenzuo.babel.helpers;

import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.concurrent.Callable;

import bolts.Task;

/**
 * Created by renzocrisostomo on 17/08/14.
 */
public class AuthorisationHelper {

    private OkHttpClient httpClient = new OkHttpClient();

    public Task<Boolean> checkTokenValidity(final String token) {
        return Task.callInBackground(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                String credential = Credentials.basic(URLHelper.GITHUB_CLIENT_ID, URLHelper.GITHUB_CLIENT_SECRET);
                Request request = new Request.Builder().url(URLHelper.getURLForTokenValidity(token))
                        .get()
                        .header("Authorization", credential)
                        .build();
                Response response = httpClient.newCall(request).execute();
                return true;
            }
        });
    }

}
