package com.ruenzuo.babel.models;

import java.io.Serializable;

/**
 * Created by renzocrisostomo on 21/08/14.
 */
public class Language implements Serializable {

    private int index;
    private String name;
    private String search;
    private String css;

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getSearch() {
        return search;
    }

    public String getCss() {
        return css;
    }

}
