package com.joelzhu.base.view;

import android.app.Activity;
import android.view.View;
import android.view.Window;

public abstract class AbsRemoteActivity extends AbsBaseActivity {
    private Activity hostActivity;

    @Override
    public void setContentView(int layoutResID) {
        hostActivity.setContentView(layoutResID);
    }

    @Override
    public Window getWindow() {
        return hostActivity.getWindow();
    }

    @Override
    public <T extends View> T findViewById(int id) {
        return hostActivity.findViewById(id);
    }

    public void attachActivity(Activity activity) {
        this.hostActivity = activity;
    }
}