package com.joelzhu.common;

public final class StringUtil {
    private StringUtil() {
    }

    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }
}