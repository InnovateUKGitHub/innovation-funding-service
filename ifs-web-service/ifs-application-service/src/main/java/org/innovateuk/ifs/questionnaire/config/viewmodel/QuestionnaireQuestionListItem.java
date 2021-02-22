package org.innovateuk.ifs.questionnaire.config.viewmodel;

public class QuestionnaireQuestionListItem {

    private final long id;
    private final String name;

    public QuestionnaireQuestionListItem(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
