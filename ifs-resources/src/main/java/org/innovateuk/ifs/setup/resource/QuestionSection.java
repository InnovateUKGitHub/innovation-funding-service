package org.innovateuk.ifs.setup.resource;

public enum QuestionSection {
    APPLICATION_QUESTIONS("Application questions"),
    PROJECT_DETAILS("Project details");

    private final String name;

    QuestionSection(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
