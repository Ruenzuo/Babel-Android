package com.ruenzuo.babel.managers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ruenzuo.babel.R;
import com.ruenzuo.babel.helpers.ConfigurationHelper;
import com.ruenzuo.babel.helpers.GitHubAPIHelper;
import com.ruenzuo.babel.helpers.TranslatorHelper;
import com.ruenzuo.babel.helpers.URLHelper;
import com.ruenzuo.babel.models.File;
import com.ruenzuo.babel.models.Language;
import com.ruenzuo.babel.models.Repository;
import com.ruenzuo.babel.models.enums.DifficultyType;
import com.securepreferences.util.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import bolts.Capture;
import bolts.Continuation;
import bolts.Task;

/**
 * Created by renzocrisostomo on 17/08/14.
 */
public class BabelManager {

    private DifficultyType difficultyType;
    private String token;
    private String placeholder;
    private ArrayList<Language> languages;
    private TranslatorHelper translatorHelper = new TranslatorHelper();
    private GitHubAPIHelper gitHubAPIHelper;
    private Random random = new Random();
    private ConfigurationHelper configurationHelper = new ConfigurationHelper();
    private Queue<Hashtable<String, Object>> queue = new LinkedBlockingQueue<Hashtable<String, Object>>();

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
            Toast.makeText(context, context.getString(R.string.error_while_loading_languages), Toast.LENGTH_LONG).show();
        }
        languages = new ArrayList<Language>();
        languages.addAll(Arrays.asList(translatorHelper.translateLanguages(file)));
    }

    private void setupPlaceholder(Context context) {
        try {
            InputStream inputStream = context.getAssets()
                    .open("WebRoot/index.html");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            placeholder = new String(buffer);
        } catch (IOException e) {
            Toast.makeText(context, context.getString(R.string.error_while_loading_html_placeholder), Toast.LENGTH_LONG).show();
        }
    }

    public void setupQueue(Context context) {
        setupLanguages(context);
        setupPlaceholder(context);
        addNextToQueue();
        addNextToQueue();
        addNextToQueue();
    }

    public Task<Hashtable<String, Object>> loadNext() {
        addNextToQueue();
        if (queue.size() > 0) {
            return Task.forResult(queue.poll());
        } else {
            return nextTask();
        }
    }

    private Task<Hashtable<String, Object>> nextTask() {
        final Capture<Language> languageCapture = new Capture<Language>(getRandomLanguage());
        final Capture<Repository> repositoryCapture = new Capture<Repository>();
        final Capture<File> fileCapture = new Capture<File>();
        final Task<Hashtable<String, Object>>.TaskCompletionSource completionSource = Task.create();
        getRandomRepository(languageCapture.get()).continueWithTask(new Continuation<Repository, Task<File>>() {
            @Override
            public Task<File> then(Task<Repository> task) throws Exception {
                if (task.getError() != null) {
                    completionSource.setError(task.getError());
                    return null;
                } else {
                    Repository repository = task.getResult();
                    Log.d("BabelManager", "Random repository done.");
                    Log.i("BabelManager", "Repository: " +  repository.getName());
                    repositoryCapture.set(repository);
                    return getRandomFile(languageCapture.get(), repository);
                }
            }
        }).continueWithTask(new Continuation<File, Task<String>>() {
            @Override
            public Task<String> then(Task<File> task) throws Exception {
                if (task.getError() != null) {
                    completionSource.setError(task.getError());
                    return null;
                } else {
                    File file = task.getResult();
                    Log.d("BabelManager", "Random file done.");
                    Log.i("BabelManager", "File: " +  file.getName());
                    fileCapture.set(file);
                    return getHTMLString(languageCapture.get(), repositoryCapture.get(), fileCapture.get());
                }
            }
        }).continueWith(new Continuation<String, Void>() {
            @Override
            public Void then(Task<String> task) throws Exception {
                if (task.getError() != null) {
                    completionSource.setError(task.getError());
                } else {
                    Log.d("BabelManager", "HTML string done.");
                    Hashtable<String, Object> hashtable = new Hashtable<String, Object>();
                    hashtable.put("Language", languageCapture.get());
                    hashtable.put("Repository", repositoryCapture.get());
                    hashtable.put("File", fileCapture.get());
                    hashtable.put("HTML", task.getResult());
                    completionSource.setResult(hashtable);
                }
                return null;
            }
        });
        return completionSource.getTask();
    }

    private void addNextToQueue() {
        nextTask().continueWith(new Continuation<Hashtable<String, Object>, Object>() {
            @Override
            public Object then(Task<Hashtable<String, Object>> task) throws Exception {
                if (task.getError() != null) {
                    queue.add(task.getResult());
                } else {
                    Log.e("BabelManager", "Add next to queue failed with error: " + task.getError().getLocalizedMessage());
                }
                return null;
            }
        });
    }

    private Language getRandomLanguage() {
        if (configurationHelper.shouldFixRandomLanguage()) {
            return configurationHelper.fixedRandomLanguage(languages);
        } else {
            return languages.get(random.nextInt(languages.size()));
        }
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

    private Task<String> getHTMLString(final Language language, Repository repository, File file) {
        return gitHubAPIHelper.getBlob(repository, file, token).continueWith(new Continuation<JsonObject, String>() {
            @Override
            public String then(Task<JsonObject> task) throws Exception {
                if (task.getError() != null) {
                    throw task.getError();
                }
                byte[] bytes = Base64.decode(task.getResult().get("content").getAsString(), Base64.DEFAULT);
                String decodedString = new String(bytes);
                return placeholder.replace("BABEL_CODE_PLACEHOLDER", decodedString).replace("BABEL_LANGUAGE_PLACEHOLDER", language.getCss());
            }
        });
    }

}
