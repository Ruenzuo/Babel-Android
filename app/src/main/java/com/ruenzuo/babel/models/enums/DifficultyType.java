package com.ruenzuo.babel.models.enums;

import java.io.Serializable;

/**
 * Created by renzocrisostomo on 21/08/14.
 */
public enum DifficultyType implements Serializable {

    DIFFICULTY_TYPE_EASY ("Easy", 3), DIFFICULTY_TYPE_NORMAL ("Normal", 5), DIFFICULTY_TYPE_HARD ("Hard", 7);

    private final String print;
    private final int maxHints;

    DifficultyType(String prnt, int mxHnts) {
        print = prnt;
        maxHints = mxHnts;
    }

    public int toMaxHints() {
        return maxHints;
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
