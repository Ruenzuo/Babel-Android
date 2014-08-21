package com.ruenzuo.babel.models.enums;

/**
 * Created by renzocrisostomo on 21/08/14.
 */
public enum BabelFragmentType {

    BABEL_FRAGMENT_TYPE_SOURCE_CODE ("Code"), BABEL_FRAGMENT_TYPE_GUESS_OPTIONS ("Guess");

    private final String print;

    BabelFragmentType(String prnt) {
        print = prnt;
    }

    public String toPrint() {
        return print;
    }


}
