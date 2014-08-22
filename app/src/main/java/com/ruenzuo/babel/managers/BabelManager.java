package com.ruenzuo.babel.managers;

import android.content.Context;
import android.util.Log;

import com.ruenzuo.babel.helpers.TranslatorHelper;
import com.ruenzuo.babel.models.Language;
import com.ruenzuo.babel.models.enums.DifficultyType;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by renzocrisostomo on 17/08/14.
 */
public class BabelManager {

    private DifficultyType difficultyType;
    private String token;
    private List<Language> languages;
    private TranslatorHelper translatorHelper = new TranslatorHelper();

    public BabelManager(DifficultyType difficultyType, String token) {
        this.difficultyType = difficultyType;
        this.token = token;
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
        Log.i("Languages", languages.toString());
    }

}
