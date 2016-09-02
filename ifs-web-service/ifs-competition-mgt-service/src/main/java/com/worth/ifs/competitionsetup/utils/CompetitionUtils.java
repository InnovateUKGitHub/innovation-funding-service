package com.worth.ifs.competitionsetup.utils;

/**
 * Utility class to keep common re-usable methods
 */
public class CompetitionUtils {

    public static boolean textToBoolean(String value) {
        return (value != null && value.equalsIgnoreCase("yes")) ? true : false;
    }

    public static String booleanToText(Boolean value) {
        return value ? "yes" : "no";
    }

}
