package com.joelzhu.base.present;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.joelzhu.common.http.ErrorCode;
import com.joelzhu.common.http.OnPostRequestListener;
import com.joelzhu.common.http.Parameter;
import com.joelzhu.common.http.RequestManager;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class HttpRequestTask extends AsyncTask<Parameter, Integer, Void> {
    private Set<OnPostRequestListener> postListeners;

    public abstract String logTAG();

    public abstract String requestUrl();

    public abstract Parameter[] requestParam(Intent intent);

    private final OnPostRequestListener baseListener = new OnPostRequestListener() {
        @Override
        public void onPreRequest(final String request) {
            switch2UiThread(new Runnable() {
                @Override
                public void run() {
                    HttpRequestTask.this.onPreRequest(request);
                }
            });
        }

        @Override
        public void onErrorOccurred(final ErrorCode errorCode) {
            switch2UiThread(new Runnable() {
                @Override
                public void run() {
                    HttpRequestTask.this.onErrorOccurred(errorCode);
                }
            });
        }

        @Override
        public void onPostRequest(final String response) {
            switch2UiThread(new Runnable() {
                @Override
                public void run() {
                    HttpRequestTask.this.onPostRequest(response);
                    recycle();
                }
            });
        }
    };

    public HttpRequestTask() {
        postListeners = new CopyOnWriteArraySet<>();
        // Subscribe the base listener.
        RequestManager.getInstance().subscribePostListener(baseListener);
    }

    @Override
    protected final Void doInBackground(Parameter... params) {
        RequestManager.getInstance().requestPost(requestUrl(), params);
        return null;
    }

    public void subscribeRequestListener(OnPostRequestListener listener) {
        postListeners.add(listener);
    }

    private void onPreRequest(String request) {
        LogUtil.d(logTAG(), "Request: " + request);
        for (OnPostRequestListener listener : postListeners) {
            listener.onPreRequest(request);
        }
    }

    private void onErrorOccurred(ErrorCode errorCode) {
        LogUtil.e(logTAG(), "Request error, error code: " + errorCode);
        for (OnPostRequestListener listener : postListeners) {
            listener.onErrorOccurred(errorCode);
        }
    }

    private void onPostRequest(String response) {
        LogUtil.d(logTAG(), "Response: " + response);
        for (OnPostRequestListener listener : postListeners) {
            listener.onPostRequest(response);
        }
    }

    private void switch2UiThread(Runnable runnable) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(runnable);
    }

    private void recycle() {
        // Unsubscribe the base listener.
        RequestManager.getInstance().unsubscribePostListener(baseListener);
        // Clear all listener.
        postListeners.clear();
    }
}