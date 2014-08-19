package com.ruenzuo.babel.helpers;

import android.content.Context;
import android.widget.Toast;

import com.ruenzuo.babel.R;

/**
 * Created by ruenzuo on 19/08/14.
 */
public class ErrorNotificationHelper {

    public static void notifyError(Context context) {
        Toast.makeText(context, context.getString(R.string.error_message), Toast.LENGTH_LONG).show();
    }

}
