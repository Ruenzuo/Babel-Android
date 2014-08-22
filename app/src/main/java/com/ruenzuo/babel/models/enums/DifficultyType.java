package com.ruenzuo.babel.models.enums;

import java.io.Serializable;

/**
 * Created by renzocrisostomo on 21/08/14.
 */
public enum DifficultyType implements Serializable {

    DIFFICULTY_TYPE_EASY ("Easy"), DIFFICULTY_TYPE_NORMAL ("Normal"), DIFFICULTY_TYPE_HARD ("Hard");

    private final String print;

    DifficultyType(String prnt) {
        print = prnt;
    }

    public String toPrint() {
        return print;
    }

    public static CharSequence[] difficultyTypes() {
        int length = DifficultyType.values().length;
        CharSequence[] typesPrint = new CharSequence[length];
        for (int i = 0; i < length; i++) {
            typesPrint[i] = DifficultyType.values()[i].toPrint();
        }
        return typesPrint;
    }

}
