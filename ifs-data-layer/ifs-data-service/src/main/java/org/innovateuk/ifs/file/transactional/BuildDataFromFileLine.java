package org.innovateuk.ifs.file.transactional;

public class BuildDataFromFileLine {

    private String competitionName;
    private String externalCompId;
    private String applicationName;
    private String questionName;
    private String response;

    public BuildDataFromFileLine(String competitionName, String externalCompId, String applicationName, String questionName, String response) {
        this.competitionName = competitionName;
        this.externalCompId = externalCompId;
        this.applicationName = applicationName;
        this.questionName = questionName;
        this.response = response;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public String getExternalCompId() {
        return externalCompId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getQuestionName() {
        return questionName;
    }

    public String getResponse() {
        return response;
    }
}
