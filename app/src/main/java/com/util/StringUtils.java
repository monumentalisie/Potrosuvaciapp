package com.util;

public final class StringUtils {

    private StringUtils() {
    }

    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static boolean notEmpty(String str) {
        return str != null && !str.isEmpty();
    }
}
