package com.ruenzuo.babel.helpers;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by ruenzuo on 24/08/14.
 */
public class BabelUtils {

    public static String getAppVersion(Context context) {
        String versionName;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException ex) {
            versionName = "Unknown";
        }
        return versionName;
    }

}
