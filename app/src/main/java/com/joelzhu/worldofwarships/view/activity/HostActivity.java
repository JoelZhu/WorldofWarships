package com.joelzhu.worldofwarships.view.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Environment;
import android.util.DisplayMetrics;

import com.joelzhu.worldofwarships.presenter.DynamicLoadUtil;
import com.joelzhu.base.presenter.LogUtil;
import com.joelzhu.base.view.AbsBaseActivity;
import com.joelzhu.base.view.AbsRemoteActivity;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public final class HostActivity extends AbsBaseActivity {
    private final static String TAG = HostActivity.class.getSimpleName();
    
    private AssetManager assetManager;
    
    private Resources resources;
    
    private AbsRemoteActivity remoteActivity;
    
    @Override
    public void createActivity() {
        if (remoteActivity != null) {
            remoteActivity.createActivity();
        }
    }
    
    @Override
    public void resumeActivity() {
        if (remoteActivity != null) {
            remoteActivity.resumeActivity();
        }
    }
    
    @Override
    public void startActivity() {
        if (remoteActivity != null) {
            remoteActivity.startActivity();
        }
    }
    
    @Override
    public void restartActivity() {
        if (remoteActivity != null) {
            remoteActivity.restartActivity();
        }
    }
    
    @Override
    public void pauseActivity() {
        if (remoteActivity != null) {
            remoteActivity.pauseActivity();
        }
    }
    
    @Override
    public void stopActivity() {
        if (remoteActivity != null) {
            remoteActivity.stopActivity();
        }
    }
    
    @Override
    public void destroyActivity() {
        if (remoteActivity != null) {
            remoteActivity.destroyActivity();
        }
    }
    
    @Override
    public AssetManager getAssets() {
        return assetManager == null ? super.getAssets() : assetManager;
    }
    
    @Override
    public Resources getResources() {
        return resources == null ? super.getResources() : resources;
    }
    
    @Override
    protected void doBeforeSetContentView() {
        parseApkParameters();
        remoteActivity.attachActivity(this);
    }
    
    @Override
    protected AbsRemoteActivity activity2BeBound() {
        return remoteActivity;
    }
    
    private void parseApkParameters() {
        final Intent intent = getIntent();
        if (intent == null) {
            LogUtil.e(TAG, "Intent is null, leak of important information.");
            return;
        }
        
        final File dirFile = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (dirFile == null) {
            LogUtil.e(TAG, "The instance of directory got null.");
            return;
        }
        
        final String apkName = intent.getStringExtra(DynamicLoadUtil.INTENT_KEY_APK_NAME);
        final String apkPath = dirFile.getAbsolutePath() + File.separator + apkName;
        final String homeClassName = intent.getStringExtra(DynamicLoadUtil.INTENT_KEY_HOME_CLASS);
        loadApk(homeClassName, apkPath);
    }
    
    private void loadApk(final String homeClassName, final String apkPath) {
        // Create AssetManager.
        try {
            assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, apkPath);
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            LogUtil.e(TAG, "Create AssetManager failed.");
            LogUtil.e(TAG, e.getMessage());
            return;
        }
        
        // Create Resources.
        final DisplayMetrics displayMetrics = super.getResources().getDisplayMetrics();
        final Configuration configuration = super.getResources().getConfiguration();
        resources = new Resources(assetManager, displayMetrics, configuration);
        
        // Create loader to load the entrance in uninstalled-apk.
        DexClassLoader loader = new DexClassLoader(apkPath, null, null, getClassLoader());
        try {
            Class homeClass = loader.loadClass(homeClassName);
            if (AbsRemoteActivity.class.isAssignableFrom(homeClass)) {
                remoteActivity = (AbsRemoteActivity) homeClass.newInstance();
            } else {
                LogUtil.e(TAG,
                        String.format("Illegal home activity, %s", homeClass.getSimpleName()));
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            LogUtil.e(TAG, "Create instance of home activity failed.");
            LogUtil.e(TAG, e.getMessage());
        }
    }
}