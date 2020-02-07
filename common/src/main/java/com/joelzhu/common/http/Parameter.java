package com.joelzhu.common.http;

public final class Parameter {
    public static final String SYMBOL_JOIN = "&";

    public static final String SYMBOL_EQUAL = "=";

    private String key;

    private String value;

    public Parameter(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return key + SYMBOL_EQUAL + value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}