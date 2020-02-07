package com.joelzhu.common.http;

interface OnRequestListener {
    void onPreRequest(String request);

    void onErrorOccurred(ErrorCode errorCode);

    void onPostRequest(String response);
}