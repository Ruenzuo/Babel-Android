package com.ruenzuo.babel.helpers;

import com.ruenzuo.babel.models.Language;
import com.ruenzuo.babel.models.enums.DifficultyType;

import java.util.ArrayList;

/**
 * Created by ruenzuo on 23/08/14.
 */
public class ConfigurationHelper {

    private static final int FIXED_RANDOM_LANGUAGE_INDEX = -1;

    public boolean shouldFixRandomLanguage() {
        return FIXED_RANDOM_LANGUAGE_INDEX != -1;
    }

    public Language fixedRandomLanguage(ArrayList<Language> languages) {
        return languages.get(FIXED_RANDOM_LANGUAGE_INDEX);
    }

}
