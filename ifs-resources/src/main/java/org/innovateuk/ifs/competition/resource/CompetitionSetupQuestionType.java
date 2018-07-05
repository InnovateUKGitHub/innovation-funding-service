package org.innovateuk.ifs.competition.resource;

public enum CompetitionSetupQuestionType {

    ASSESSED_QUESTION(""),
    SCOPE("Scope"),
    PROJECT_SUMMARY("Project summary"),
    PUBLIC_DESCRIPTION("Public description"),
    APPLICATION_DETAILS("Application details"),
    APPLICATION_TEAM("Application team");

    private String shortName;

    CompetitionSetupQuestionType(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return this.shortName;
    }

}
