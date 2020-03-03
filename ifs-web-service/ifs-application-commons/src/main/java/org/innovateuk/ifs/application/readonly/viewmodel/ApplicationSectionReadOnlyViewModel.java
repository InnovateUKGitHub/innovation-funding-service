package org.innovateuk.ifs.application.readonly.viewmodel;

import java.util.Set;

public class ApplicationSectionReadOnlyViewModel {

    private String name;

    private boolean finances;

    private Set<ApplicationQuestionReadOnlyViewModel> questions;

    public ApplicationSectionReadOnlyViewModel(String name, boolean finances, Set<ApplicationQuestionReadOnlyViewModel> questions) {
        this.name = name;
        this.finances = finances;
        this.questions = questions;
    }

    public String getName() {
        return name;
    }

    public boolean isFinances() {
        return finances;
    }

    public Set<ApplicationQuestionReadOnlyViewModel> getQuestions() {
        return questions;
    }
}
