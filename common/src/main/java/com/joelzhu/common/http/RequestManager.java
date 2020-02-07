package com.joelzhu.common.http;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public final class RequestManager {
    private Set<OnGetRequestListener> getListeners;

    private Set<OnPostRequestListener> postListeners;

    private static RequestManager instance;

    private static final Object LOCKER = new Object();

    public static RequestManager getInstance() {
        if (instance == null) {
            synchronized (LOCKER) {
                if (instance == null) {
                    instance = new RequestManager();
                }
            }
        }
        return instance;
    }

    private RequestManager() {
        getListeners = new CopyOnWriteArraySet<>();
        postListeners = new CopyOnWriteArraySet<>();
    }

    void onPreGetRequest(String request) {
        for (OnGetRequestListener listener : getListeners) {
            listener.onPreRequest(request);
        }
    }

    void onGetErrorOccurred(ErrorCode errorCode) {
        for (OnGetRequestListener listener : getListeners) {
            listener.onErrorOccurred(errorCode);
        }
    }

    void onPostGetRequest(String response) {
        for (OnGetRequestListener listener : getListeners) {
            listener.onPostRequest(response);
        }
    }

    void onPrePostRequest(String request) {
        for (OnPostRequestListener listener : postListeners) {
            listener.onPreRequest(request);
        }
    }

    void onPostErrorOccurred(ErrorCode errorCode) {
        for (OnPostRequestListener listener : postListeners) {
            listener.onErrorOccurred(errorCode);
        }
    }

    void onPostPostRequest(String response) {
        for (OnPostRequestListener listener : postListeners) {
            listener.onPostRequest(response);
        }
    }

    public void subscribeGetListener(OnGetRequestListener listener) {
        getListeners.add(listener);
    }

    public void unsubscribeGetListener(OnGetRequestListener listener) {
        getListeners.remove(listener);
    }

    public void subscribePostListener(OnPostRequestListener listener) {
        postListeners.add(listener);
    }

    public void unsubscribePostListener(OnPostRequestListener listener) {
        postListeners.remove(listener);
    }

    public void requestGet(String requestUrl, Parameter... parameters) {
        HttpUtil.httpGet(requestUrl, parameters);
    }

    public void requestPost(String requestUrl, Parameter... parameters) {
        HttpUtil.httpPost(requestUrl, parameters);
    }
}