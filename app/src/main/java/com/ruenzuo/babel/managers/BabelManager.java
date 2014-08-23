package com.ruenzuo.babel.managers;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ruenzuo.babel.helpers.GitHubAPIHelper;
import com.ruenzuo.babel.helpers.TranslatorHelper;
import com.ruenzuo.babel.helpers.URLHelper;
import com.ruenzuo.babel.models.File;
import com.ruenzuo.babel.models.Language;
import com.ruenzuo.babel.models.Repository;
import com.ruenzuo.babel.models.enums.DifficultyType;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by renzocrisostomo on 17/08/14.
 */
public class BabelManager {

    private DifficultyType difficultyType;
    private String token;
    private ArrayList<Language> languages;
    private TranslatorHelper translatorHelper = new TranslatorHelper();
    private GitHubAPIHelper gitHubAPIHelper;
    private Random random = new Random();

    public BabelManager(DifficultyType difficultyType, String token, Context context) {
        this.difficultyType = difficultyType;
        this.token = token;
        gitHubAPIHelper = new GitHubAPIHelper(URLHelper.getUserAgent(context));
    }

    public ArrayList<Language> getLanguages() {
        return languages;
    }

    private void setupLanguages(Context context) {
        String file = "";
        try {
            StringBuilder stringBuilder = new StringBuilder("info-");
            InputStream inputStream = context.getAssets()
                    .open(stringBuilder.append(difficultyType.toPrint().toLowerCase())
                            .append(".json")
                            .toString());
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            file = new String(buffer);
        } catch (IOException e) {
            //TODO: Handle exception.
        }
        languages = new ArrayList<Language>();
        languages.addAll(Arrays.asList(translatorHelper.translateLanguages(file)));
    }

    public void setupQueue(Context context) {
        setupLanguages(context);
        final Language language = languages.get(0);
        getRandomRepository(language).continueWithTask(new Continuation<Repository, Task<File>>() {
            @Override
            public Task<File> then(Task<Repository> task) throws Exception {
                return getRandomFile(language, task.getResult());
            }
        });
    }

    private Task<Repository> getRandomRepository(Language language) {
        return gitHubAPIHelper.getRepositories(language, token).continueWith(new Continuation<JsonObject, Repository>() {
            @Override
            public Repository then(Task<JsonObject> task) throws Exception {
                if (task.getError() != null) {
                    throw task.getError();
                }
                JsonArray items = task.getResult().getAsJsonArray("items");
                Repository[] repositories = translatorHelper.translateRepositories(items);
                return repositories[random.nextInt(5)];
            }
        });
    }

    private Task<File> getRandomFile(Language language, Repository repository) {
        return gitHubAPIHelper.getFiles(language, repository, token).continueWith(new Continuation<JsonObject, File>() {
            @Override
            public File then(Task<JsonObject> task) throws Exception {
                if (task.getError() != null) {
                    throw task.getError();
                }
                JsonArray items = task.getResult().getAsJsonArray("items");
                File[] files = translatorHelper.translateFiles(items);
                return files[random.nextInt(files.length)];
            }
        });
    }

}
