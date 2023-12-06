package com.panvan.app.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogUtil {

    private static final Logger logger = Logger.getLogger(LogUtil.class.getName());

    public static void info(String tag, String message) {
        logger.log(Level.INFO, tag + ":" + message);
    }

    public static void info(String message) {
        message = DateUtil.getCurrentDateTime() + message;
        logger.log(Level.INFO, message);
    }

    public static void warning(String message) {
        logger.log(Level.WARNING, message);
    }

    public static void error(String message) {
        logger.log(Level.SEVERE, message);
    }
}
