package com.joelzhu.bindview;

import android.util.Log;

final class LogUtil {
    private final static String TAG = "WOWS_BindView";

    public static void v(String logMessage) {
        Log.v(TAG, logMessage);
    }

    public static void i(String logMessage) {
        Log.i(TAG, logMessage);
    }

    public static void d(String logMessage) {
        Log.d(TAG, logMessage);
    }

    public static void w(String logMessage) {
        Log.w(TAG, logMessage);
    }

    public static void e(String logMessage) {
        Log.e(TAG, logMessage);
    }
}