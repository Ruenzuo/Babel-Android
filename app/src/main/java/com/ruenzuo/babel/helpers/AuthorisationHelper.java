package com.ruenzuo.babel.helpers;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.concurrent.Callable;

import bolts.Task;

/**
 * Created by renzocrisostomo on 17/08/14.
 */
public class AuthorisationHelper {

    private HttpClient httpClient = new DefaultHttpClient();

    public Task<Boolean> checkTokenValidity(final String token) {
        return Task.callInBackground(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                HttpGet httpGet = new HttpGet(URLHelper.getURLStringForTokenValidity(token));
                httpGet.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(URLHelper.GITHUB_CLIENT_ID, URLHelper.GITHUB_CLIENT_SECRET), "UTF-8", false));
                HttpResponse httpResponse = httpClient.execute(httpGet);
                return httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
            }
        });
    }

    public Task<Boolean> revokeToken(final String token) {
        return Task.callInBackground(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                HttpDelete httpDelete = new HttpDelete(URLHelper.getURLStringForTokenValidity(token));
                httpDelete.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(URLHelper.GITHUB_CLIENT_ID, URLHelper.GITHUB_CLIENT_SECRET), "UTF-8", false));
                HttpResponse httpResponse = httpClient.execute(httpDelete);
                return httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT;
            }
        });
    }

}
