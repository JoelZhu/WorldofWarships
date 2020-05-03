package com.joelzhu.base.presenter.network;

final class RequestException extends Exception {
    @ErrorCode
    private int errorCode;
    
    public RequestException(@ErrorCode int errorCode) {
        super();
        this.errorCode = errorCode;
    }
    
    @ErrorCode
    public int getErrorCode() {
        return errorCode;
    }
}