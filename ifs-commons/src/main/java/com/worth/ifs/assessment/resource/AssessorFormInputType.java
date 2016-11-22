package com.worth.ifs.assessment.resource;

import static java.util.Arrays.stream;

public enum AssessorFormInputType {

    FEEDBACK("textarea"),
    RESEARCH_CATEGORY("assessor_research_category"),
    SCORE("assessor_score"),
    APPLICATION_IN_SCOPE("assessor_application_in_scope");

    private String title;

    AssessorFormInputType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static AssessorFormInputType getByTitle(String title) {
        return stream(values())
                .filter(assessorFormInputType -> assessorFormInputType.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("AssessorFormInputType not found: " + title));
    }
}