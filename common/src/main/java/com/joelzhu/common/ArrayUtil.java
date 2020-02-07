package com.joelzhu.common;

import com.joelzhu.common.http.Parameter;

public final class ArrayUtil {
    private ArrayUtil() {
    }

    public static <T> String array2String(T[] array, String symbol) {
        StringBuilder stringBuilder = new StringBuilder();
        for (T item : array) {
            stringBuilder.append(item.toString());
            stringBuilder.append(symbol);
        }
        stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(symbol));
        return stringBuilder.toString();
    }

    public static Parameter[] string2ParameterArray(String arrayString) {
        String[] stringArray = arrayString.split(Parameter.SYMBOL_JOIN);
        final int arraySize = stringArray.length;
        Parameter[] parameters = new Parameter[arraySize];
        for (int i = 0; i < arraySize; i++) {
            String temp = stringArray[i];
            if (temp.contains(Parameter.SYMBOL_EQUAL)) {
                final String[] splitArray = temp.split(Parameter.SYMBOL_EQUAL);
                Parameter parameter = new Parameter(splitArray[0], splitArray[1]);
                parameters[i] = parameter;
            } else {
                parameters[i] = null;
            }
        }
        return parameters;
    }
}