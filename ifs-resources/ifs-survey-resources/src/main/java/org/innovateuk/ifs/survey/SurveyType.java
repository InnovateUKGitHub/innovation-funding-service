package org.innovateuk.ifs.survey;

public enum SurveyType {
    APPLICATION_SUBMISSION,
    PROJECT_SUBMISSION;

    public static SurveyType getSurveyTypeFromString(String type) {
        for (SurveyType surveyType : SurveyType.values()) {
            if (surveyType.name().equals(type)) {
                return surveyType;
            }
        }
        return APPLICATION_SUBMISSION; // default value
    }
}