package com.ruenzuo.babel.helpers;

import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ruenzuo.babel.extensions.GitHubAPIRateLimitException;
import com.ruenzuo.babel.models.File;
import com.ruenzuo.babel.models.Language;
import com.ruenzuo.babel.models.Repository;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.http.HttpStatus;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Callable;

import bolts.Task;

/**
 * Created by ruenzuo on 23/08/14.
 */
public class GitHubAPIHelper {

    private String userAgent;
    private OkHttpClient httpClient = new OkHttpClient();
    private Hashtable<String, Map<String, String>> repositoriesPaginationCache = new Hashtable<String, Map<String, String>>();
    private JsonParser jsonParser = new JsonParser();

    public GitHubAPIHelper(String userAgent) {
        this.userAgent = userAgent;
    }

    private String getCachedQuery(Language language, String token) {
        Map<String, String> cachedPaginationQuery = repositoriesPaginationCache.get(language.getSearch());
        if (cachedPaginationQuery != null) {
            String cachedQuery = TranslatorHelper.translateToString(cachedPaginationQuery);
            return cachedQuery;
        } else {
            return "q=" + "language:" + language.getSearch() + "&access_token=" + token + "&per_page=5";
        }
    }

    private void processForCache(Response response, Language language, String token) {
        String link = response.header("link");
        String[] components = link.split(",");
        for (String component : components) {
            String[] subcomponents = component.split("; rel=");
            String rel = subcomponents[1];
            if (rel.contains("next")) {
                String url = subcomponents[0].replace("<", "").replace(">", "");
                String[] urlComponents = url.split("\\?");
                Map<String, String> query = TranslatorHelper.translateToMap(urlComponents[1]);
                query.put("access_token", token);
                repositoriesPaginationCache.put(language.getSearch(), query);
            }
        }
    }

    public Task<JsonObject> getRepositories(final Language language, final String token) {
        return Task.callInBackground(new Callable<JsonObject>() {
            @Override
            public JsonObject call() throws Exception {
                Request request = new Request.Builder()
                        .url(URLHelper.getURLStringForRepositories(getCachedQuery(language, token)))
                        .header("User-Agent", userAgent)
                        .get()
                        .build();
                Response response = httpClient.newCall(request).execute();
                if (response.code() == HttpStatus.SC_FORBIDDEN) {
                    throw new GitHubAPIRateLimitException("Rate limit reached.");
                } else {
                    processForCache(response, language, token);
                    return jsonParser.parse(response.body().string()).getAsJsonObject();
                }
            }
        });
    }

    public Task<JsonObject> getFiles(final Language language, final Repository repository, final String token) {
        return Task.callInBackground(new Callable<JsonObject>() {
            @Override
            public JsonObject call() throws Exception {
                Request request = new Request.Builder()
                        .url(URLHelper.getURLStringForFiles(language, repository, token))
                        .header("User-Agent", userAgent)
                        .get()
                        .build();
                Response response = httpClient.newCall(request).execute();
                if (response.code() == HttpStatus.SC_FORBIDDEN) {
                    throw new GitHubAPIRateLimitException("Rate limit reached.");
                } else {
                    return jsonParser.parse(response.body().string()).getAsJsonObject();
                }
            }
        });
    }

    public Task<JsonObject> getBlob(final Repository repository, final File file, final String token) {
        return Task.callInBackground(new Callable<JsonObject>() {
            @Override
            public JsonObject call() throws Exception {
                Request request = new Request.Builder()
                        .url(URLHelper.getURLStringForBlob(repository, file, token))
                        .header("User-Agent", userAgent)
                        .get()
                        .build();
                Response response = httpClient.newCall(request).execute();
                if (response.code() == HttpStatus.SC_FORBIDDEN) {
                    throw new GitHubAPIRateLimitException("Rate limit reached.");
                } else {
                    return jsonParser.parse(response.body().string()).getAsJsonObject();
                }
            }
        });
    }

}
