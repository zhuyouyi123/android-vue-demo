package com.panvan.app.utils;

public class RegexUtil {
    private static final String CHAR_REGEX = "^[\\da-zA-Z_.-]*$";

    public static boolean hasInvalidChars(String string) {
        return string.matches(CHAR_REGEX);
    }
}
