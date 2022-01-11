package org.innovateuk.ifs.application.resource;

public enum QuestionStatus {
    COMPLETE("Complete"),
    INCOMPLETE("Incomplete");

    private String displayName;

    private QuestionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}

