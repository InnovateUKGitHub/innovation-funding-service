package com.worth.ifs.competitionsetup.utils;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.form.resource.FormInputResource;

import java.util.List;

/**
 * Utility class to keep common re-usable methods
 */
public class CompetitionUtils {

    public static boolean textToBoolean(String value) {
        return (value != null && value.equalsIgnoreCase("yes")) ? true : false;
    }

    public static String booleanToText(Boolean value) {
        if(value == null) {
            return "";
        }
        return value ? "yes" : "no";
    }

    public static boolean isSendToDashboard(CompetitionResource competition) {
        return competition == null ||
                (!CompetitionResource.Status.COMPETITION_SETUP.equals(competition.getCompetitionStatus()) &&
                        !CompetitionResource.Status.READY_TO_OPEN.equals(competition.getCompetitionStatus()));
    }

    public static boolean inputsTypeMatching(List<FormInputResource> formInputs, Long typeId) {
        return formInputs
                .stream()
                .anyMatch(formInputResource -> formInputResource.getFormInputType() != null
                        && formInputResource.getFormInputType().equals(typeId));
    }
}
