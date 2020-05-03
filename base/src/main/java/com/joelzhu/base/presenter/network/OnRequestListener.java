package com.joelzhu.base.presenter.network;

public interface OnRequestListener {
    void onPreRequest(String request);

    void onErrorOccurred(@ErrorCode int errorCode);

    void onPostRequest(String response);
}