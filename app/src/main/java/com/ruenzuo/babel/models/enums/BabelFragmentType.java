package com.ruenzuo.babel.models.enums;

import java.io.Serializable;

/**
 * Created by renzocrisostomo on 21/08/14.
 */
public enum BabelFragmentType implements Serializable {

    BABEL_FRAGMENT_TYPE_SOURCE_CODE ("Code"), BABEL_FRAGMENT_TYPE_GUESS_OPTIONS ("Guess");

    private final String print;

    BabelFragmentType(String prnt) {
        print = prnt;
    }

    public String toPrint() {
        return print;
    }

    public static CharSequence[] babelFragmentTypes() {
        int length = BabelFragmentType.values().length;
        CharSequence[] typesPrint = new CharSequence[length];
        for (int i = 0; i < length; i++) {
            typesPrint[i] = BabelFragmentType.values()[i].toPrint();
        }
        return typesPrint;
    }

}
