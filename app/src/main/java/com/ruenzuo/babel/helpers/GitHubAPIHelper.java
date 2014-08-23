package com.ruenzuo.babel.helpers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ruenzuo.babel.models.File;
import com.ruenzuo.babel.models.Language;
import com.ruenzuo.babel.models.Repository;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.Hashtable;
import java.util.concurrent.Callable;

import bolts.Task;

/**
 * Created by ruenzuo on 23/08/14.
 */
public class GitHubAPIHelper {

    private String userAgent;
    private OkHttpClient httpClient = new OkHttpClient();
    private Hashtable<String, String> repositoriesPaginationCache = new Hashtable<String, String>();
    private JsonParser jsonParser = new JsonParser();

    public GitHubAPIHelper(String userAgent) {
        this.userAgent = userAgent;
    }

    private String getCachedQuery(Language language, String token) {
        String cachedPaginationQuery = repositoriesPaginationCache.get(language.getSearch());
        if (cachedPaginationQuery != null) {
            return cachedPaginationQuery;
        } else {
            return "q=" + "language:" + language.getSearch() + "&access_token=" + token + "&per_page=5";
        }
    }

    private void processForCache(Response response, Language language) {
        String link = response.header("link");
        String[] components = link.split(",");
        for (String component : components) {
            String[] subcomponents = component.split("; rel=");
            String rel = subcomponents[1];
            if (rel.contains("next")) {
                String url = subcomponents[0].replace("<", "").replace(">", "");
                String[] urlComponents = url.split("\\?");
                repositoriesPaginationCache.put(language.getSearch(), urlComponents[1]);
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
                processForCache(response, language);
                return jsonParser.parse(response.body().string()).getAsJsonObject();
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
                return jsonParser.parse(response.body().string()).getAsJsonObject();
            }
        });
    }

    public Task<JsonObject> getBlob(final Repository repository, final File file) {
        return Task.callInBackground(new Callable<JsonObject>() {
            @Override
            public JsonObject call() throws Exception {
                Request request = new Request.Builder()
                        .url(URLHelper.getURLStringForBlob(repository, file))
                        .header("User-Agent", userAgent)
                        .get()
                        .build();
                Response response = httpClient.newCall(request).execute();
                return jsonParser.parse(response.body().string()).getAsJsonObject();
            }
        });
    }

}
