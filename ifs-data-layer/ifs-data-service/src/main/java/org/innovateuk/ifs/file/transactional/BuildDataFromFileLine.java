package org.innovateuk.ifs.file.transactional;

import java.util.List;

public class BuildDataFromFileLine {

    private static final Integer COMPETITION_NAME = 0;
    private static final Integer EXTERNAL_COMPETITION_ID = 1;
    private static final Integer APPLICATION_NAME = 2;
    private static final Integer EXTERNAL_APPLICATION_ID = 3;
    private static final Integer EXTERNAL_APPLICANT_NAME = 4;
    private static final Integer QUESTION_NAME = 5;
    private static final Integer RESPONSE_KEY = 6;

    private String competitionName;
    private String externalCompId;
    private String applicationName;
    private String externalApplicationId;
    private String externalApplicantName;
    private String questionName;
    private String response;

    public BuildDataFromFileLine(List<String> inputData) {
        this.competitionName = inputData.get(COMPETITION_NAME);
        this.externalCompId = inputData.get(EXTERNAL_COMPETITION_ID);
        this.applicationName = inputData.get(APPLICATION_NAME);
        this.externalApplicationId = inputData.get(EXTERNAL_APPLICATION_ID);
        this.externalApplicantName = inputData.get(EXTERNAL_APPLICANT_NAME);
        this.questionName = inputData.get(QUESTION_NAME);
        this.response = inputData.get(RESPONSE_KEY);
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public String getExternalCompId() {
        return externalCompId;
    }

    public void setExternalCompId(String externalCompId) {
        this.externalCompId = externalCompId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getExternalApplicationId() {
        return externalApplicationId;
    }

    public void setExternalApplicationId(String externalApplicationId) {
        this.externalApplicationId = externalApplicationId;
    }

    public String getExternalApplicantName() {
        return externalApplicantName;
    }

    public void setExternalApplicantName(String externalApplicantName) {
        this.externalApplicantName = externalApplicantName;
    }

    public String getQuestionName() {
        return questionName;
    }

    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
