package com.ruenzuo.babel.helpers;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by renzocrisostomo on 17/08/14.
 */
public class TranslatorHelper {

    public static Map<String, String> translateToMap(String query)
    {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params) {
            String[] values = param.split("=");
            String name = values[0];
            String value;
            if (values.length > 1) {
                value = values[1];
            } else {
                value = "";
            }
            map.put(name, value);
        }
        return map;
    }

}
