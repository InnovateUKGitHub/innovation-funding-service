package org.innovateuk.ifs.question.resource;

import org.innovateuk.ifs.util.CollectionFunctions;

import java.util.Arrays;

public enum QuestionSetupType {

    ASSESSED_QUESTION(""),
    SCOPE("Scope"),
    PROJECT_SUMMARY("Project summary"),
    PUBLIC_DESCRIPTION("Public description"),
    APPLICATION_DETAILS("Application details"),
    RESEARCH_CATEGORY("Research category"),
    APPLICATION_TEAM("Application team");

    private String shortName;

    QuestionSetupType(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return this.shortName;
    }

    //TODO INFUND-6282 Remove this type and replace with an active, inactive, null checks on UI.
    public static QuestionSetupType typeFromQuestionTitle(String questionTitle) {
        return CollectionFunctions.simpleFindFirst(Arrays.asList(values()), type -> questionTitle != null && questionTitle.equals(type.shortName))
                .orElse(QuestionSetupType.ASSESSED_QUESTION);
    }
}
