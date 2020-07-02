package org.innovateuk.ifs.question.resource;

public enum QuestionSetupType {

    ASSESSED_QUESTION("", true),
    SCOPE("Scope", true),
    PROJECT_SUMMARY("Project summary", true),
    PUBLIC_DESCRIPTION("Public description", true),
    APPLICATION_DETAILS("Application details"),
    RESEARCH_CATEGORY("Research category"),
    APPLICATION_TEAM("Application team"),
    TERMS_AND_CONDITIONS("T&C"),
    EQUALITY_DIVERSITY_INCLUSION("Equality, diversity & inclusion", true),
    /* h2020 */
    GRANT_TRANSFER_DETAILS("Application details"),
    GRANT_AGREEMENT("Horizon 2020 grant agreement");

    private String shortName;
    private boolean formInputResponses;

    QuestionSetupType(String shortName) {
        this.shortName = shortName;
        this.formInputResponses = false;
    }

    QuestionSetupType(String shortName, boolean formInputResponses) {
        this.shortName = shortName;
        this.formInputResponses = formInputResponses;
    }

    public String getShortName() {
        return this.shortName;
    }

    public boolean hasFormInputResponses() {
        return formInputResponses;
    }
}
