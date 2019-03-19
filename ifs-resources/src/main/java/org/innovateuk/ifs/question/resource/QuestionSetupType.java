package org.innovateuk.ifs.question.resource;

public enum QuestionSetupType {

    ASSESSED_QUESTION(""),
    SCOPE("Scope"),
    PROJECT_SUMMARY("Project summary"),
    PUBLIC_DESCRIPTION("Public description"),
    APPLICATION_DETAILS("Application details"),
    RESEARCH_CATEGORY("Research category"),
    APPLICATION_TEAM("Application team"),
    /* h2020 */
    GRANT_TRANSFER_DETAILS("Application details"),
    GRANT_AGREEMENT("Horizon 2020 grant agreement");

    private String shortName;

    QuestionSetupType(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return this.shortName;
    }

}
