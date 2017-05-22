package org.innovateuk.ifs.competitionsetup.utils;

/**
 * Utility class to keep common re-usable methods
 */
public class CompetitionUtils {

    public final static Long ALL_INNOVATION_AREAS = -1L;

    public static boolean textToBoolean(String value) {
        return (value != null && (value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("1"))) ? true : false;
    }

    public static String booleanToText(Boolean value) {
        if(value == null) {
            return "";
        }
        return value ? "yes" : "no";
    }

}
