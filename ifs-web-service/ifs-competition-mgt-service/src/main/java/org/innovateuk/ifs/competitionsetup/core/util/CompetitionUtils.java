package org.innovateuk.ifs.competitionsetup.core.util;

/**
 * Utility class to keep common re-usable methods
 */
public final class CompetitionUtils {

    private CompetitionUtils() {}

    public final static Long ALL_INNOVATION_AREAS = -1L;

    public static boolean textToBoolean(String value) {
        return (value != null && ("yes".equalsIgnoreCase(value) || "1".equalsIgnoreCase(value))) ? true : false;
    }

    public static String booleanToText(Boolean value) {
        if(value == null) {
            return "";
        }
        return value ? "yes" : "no";
    }

}
