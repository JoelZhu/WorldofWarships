package com.joelzhu.base.presenter;

public final class StringUtil {
    private StringUtil() {
    }

    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }
}