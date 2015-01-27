package com.github.alxwhtmr.herbzdbdatamanager;

import java.util.Date;
import java.util.Scanner;

/**
 * Created on 22.01.2015.
 */
public class Utils {
    public static void logErr(Object error) {
        System.out.println("[ERROR] " + new Date() + ": " + error);
    }

    public static void log(Object o) {
        if (Constants.LOGGING == true) {
            System.out.println("[LOG] " + new Date() + ": " + o);
        }
    }

    public static void abort(Object o) {
        System.out.println("[ABORT] | " + new Date() + ": " + o + " | " + Constants.ABORTED);
        System.exit(1);
    }
}
