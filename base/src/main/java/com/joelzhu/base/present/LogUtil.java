package com.joelzhu.base.present;

import android.util.Log;

public final class LogUtil {
    private final static String TAG = "WOWS";

    private final static String PRE_TAG = TAG + "_";

    public static void v(String logMessage) {
        Log.v(TAG, logMessage);
    }

    public static void v(String tag, String logMessage) {
        Log.v(PRE_TAG + tag, logMessage);
    }

    public static void i(String logMessage) {
        Log.i(TAG, logMessage);
    }

    public static void i(String tag, String logMessage) {
        Log.i(PRE_TAG + tag, logMessage);
    }

    public static void d(String logMessage) {
        Log.d(TAG, logMessage);
    }

    public static void d(String tag, String logMessage) {
        Log.d(PRE_TAG + tag, logMessage);
    }

    public static void w(String logMessage) {
        Log.w(TAG, logMessage);
    }

    public static void w(String tag, String logMessage) {
        Log.w(PRE_TAG + tag, logMessage);
    }

    public static void e(String logMessage) {
        Log.e(TAG, logMessage);
    }

    public static void e(String tag, String logMessage) {
        Log.e(PRE_TAG + tag, logMessage);
    }
}