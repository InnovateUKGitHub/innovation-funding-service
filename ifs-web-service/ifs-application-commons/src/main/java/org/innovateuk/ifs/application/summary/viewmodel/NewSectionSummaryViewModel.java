package org.innovateuk.ifs.application.summary.viewmodel;

import java.util.Set;

public class NewSectionSummaryViewModel {

    private String name;

    private Set<NewQuestionSummaryViewModel> questions;

    public NewSectionSummaryViewModel(String name, Set<NewQuestionSummaryViewModel> questions) {
        this.name = name;
        this.questions = questions;
    }

    public String getName() {
        return name;
    }

    public Set<NewQuestionSummaryViewModel> getQuestions() {
        return questions;
    }
}
