package com.joelzhu.worldofwarships.presenter;

import android.app.Activity;
import android.content.Intent;

import com.joelzhu.worldofwarships.view.activity.HostActivity;

public final class DynamicLoadUtil {
    public static final String INTENT_KEY_APK_NAME = "ApkName";
    
    public static final String INTENT_KEY_HOME_CLASS = "HomeClass";
    
    private DynamicLoadUtil() {
    }
    
    public static void startRemoteActivity(Activity activity, String apkName, String hostClass) {
        Intent intent = new Intent(activity, HostActivity.class);
        intent.putExtra(INTENT_KEY_APK_NAME, apkName);
        intent.putExtra(INTENT_KEY_HOME_CLASS, hostClass);
        activity.startActivity(intent);
    }
}