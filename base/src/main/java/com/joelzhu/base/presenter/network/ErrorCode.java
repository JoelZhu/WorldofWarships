package com.joelzhu.base.presenter.network;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

@IntDef({
        ErrorCode.NO_ERROR,
        ErrorCode.NO_REQUEST_URL,
        ErrorCode.MALFORMED_URL,
        ErrorCode.IO_EXCEPTION
})
@Retention(RetentionPolicy.SOURCE)
public @interface ErrorCode {
    int NO_ERROR = 0;
    int NO_REQUEST_URL = 1;
    int MALFORMED_URL = 2;
    int IO_EXCEPTION = 3;
}