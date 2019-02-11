package org.innovateuk.ifs.setup.resource;

import static java.util.Arrays.stream;

public enum QuestionSection {
    APPLICATION_QUESTIONS("Application questions"),
    PROJECT_DETAILS("Project details"),
    FINANCES("Finances");

    private final String name;

    QuestionSection(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static QuestionSection findByName(String name) {
        return stream(values())
                .filter(e -> e.getName().equalsIgnoreCase(name))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("No QuestionSection found for name: " + name));
    }
}
