package com.joelzhu.base.presenter.network;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.joelzhu.base.presenter.LogUtil;

public abstract class HttpRequestTask extends AsyncTask<Parameter, Integer, String> {
    private OnPostRequestListener postListener;
    
    protected abstract String logTAG();
    
    protected abstract String requestUrl();
    
    protected abstract Parameter[] requestParam(Intent intent);
    
    protected void registerRequestListener(OnPostRequestListener postListener) {
        this.postListener = postListener;
    }
    
    @Override
    protected final String doInBackground(Parameter... params) {
        switch2UiThread(() -> onPreRequest(requestUrl()));
        String result = null;
        try {
            result = HttpUtil.httpPost(requestUrl(), params);
        } catch (RequestException exception) {
            switch2UiThread(() -> onErrorOccurred(exception.getErrorCode()));
        }
        return result;
    }
    
    @Override
    protected void onPostExecute(String result) {
        switch2UiThread(() -> onPostRequest(result));
    }
    
    private void onPreRequest(String request) {
        LogUtil.d(logTAG(), "Request: " + request);
        if (postListener != null) {
            postListener.onPreRequest(request);
        }
    }
    
    private void onErrorOccurred(@ErrorCode int errorCode) {
        LogUtil.e(logTAG(), "Request error, error code: " + errorCode);
        if (postListener != null) {
            postListener.onErrorOccurred(errorCode);
        }
    }
    
    private void onPostRequest(String response) {
        LogUtil.d(logTAG(), "Response: " + response);
        if (postListener != null) {
            postListener.onPostRequest(response);
        }
    }
    
    private void switch2UiThread(Runnable runnable) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(runnable);
    }
}