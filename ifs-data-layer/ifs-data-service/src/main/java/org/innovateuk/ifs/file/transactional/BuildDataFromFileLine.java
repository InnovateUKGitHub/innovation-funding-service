package org.innovateuk.ifs.file.transactional;

public class BuildDataFromFileLine {

    private String competitionName;
    private String applicationName;
    private String questionName;
    private String response;

    public BuildDataFromFileLine(String competitionName, String applicationName, String questionName, String response) {
        this.competitionName = competitionName;
        this.applicationName = applicationName;
        this.questionName = questionName;
        this.response = response;
    }

    public String getCompetitionName() {
        return competitionName;
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
