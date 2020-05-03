package com.joelzhu.base.view;

import android.app.Activity;
import android.os.Bundle;

import com.joelzhu.bindview.JZBindView;

public abstract class AbsBaseActivity extends Activity {
    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        doBeforeSetContentView();
        
        // Bind view when activity created.
        AbsBaseActivity activity = activity2BeBound();
        if (activity == null) {
            activity = this;
        }
        JZBindView.bindView(activity);
        
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
    
    public void createActivity() {
    }
    
    public void resumeActivity() {
    }
    
    public void startActivity() {
    }
    
    public void restartActivity() {
    }
    
    public void pauseActivity() {
    }
    
    public void stopActivity() {
    }
    
    public void destroyActivity() {
    }
    
    protected void doBeforeSetContentView() {
    }
    
    protected AbsBaseActivity activity2BeBound() {
        return this;
    }
}