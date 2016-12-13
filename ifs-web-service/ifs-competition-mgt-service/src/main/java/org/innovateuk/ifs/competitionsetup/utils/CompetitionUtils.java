package org.innovateuk.ifs.competitionsetup.utils;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.FormInputResource;

import java.util.List;

import static org.innovateuk.ifs.competition.resource.CompetitionStatus.COMPETITION_SETUP;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.READY_TO_OPEN;

/**
 * Utility class to keep common re-usable methods
 */
public class CompetitionUtils {

    public static boolean textToBoolean(String value) {
        return (value != null && (value.equalsIgnoreCase("yes") || value.equalsIgnoreCase("1"))) ? true : false;
    }

    public static String booleanToText(Boolean value) {
        if(value == null) {
            return "";
        }
        return value ? "yes" : "no";
    }

    public static boolean isSendToDashboard(CompetitionResource competition) {
        return competition == null ||
                (!COMPETITION_SETUP.equals(competition.getCompetitionStatus()) &&
                        !READY_TO_OPEN.equals(competition.getCompetitionStatus()));
    }
}
