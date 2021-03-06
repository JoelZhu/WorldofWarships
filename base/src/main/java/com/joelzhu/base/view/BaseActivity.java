package com.joelzhu.base.view;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.joelzhu.bindview.JZBindView;

public abstract class BaseActivity extends FragmentActivity {
    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Bind view when activity created.
        JZBindView.bindView(this);

        createActivity();
    }

    @Override
    protected final void onResume() {
        super.onResume();

        resumeActivity();
    }

    @Override
    protected final void onStart() {
        super.onStart();

        startActivity();
    }

    @Override
    protected final void onRestart() {
        super.onRestart();

        restartActivity();
    }

    @Override
    protected final void onPause() {
        super.onPause();

        pauseActivity();
    }

    @Override
    protected final void onStop() {
        super.onStop();

        stopActivity();
    }

    @Override
    protected final void onDestroy() {
        super.onDestroy();

        destroyActivity();
    }

    protected void createActivity() {
    }

    protected void resumeActivity() {
    }

    protected void startActivity() {
    }

    protected void restartActivity() {
    }

    protected void pauseActivity() {
    }
  
    protected void stopActivity() {
    }

    protected void destroyActivity() {
    }
}