package org.innovateuk.ifs.application.readonly.viewmodel;

import java.util.Set;

public class ApplicationSectionReadOnlyViewModel {

    private String name;

    private Set<ApplicationQuestionReadOnlyViewModel> questions;

    public ApplicationSectionReadOnlyViewModel(String name, Set<ApplicationQuestionReadOnlyViewModel> questions) {
        this.name = name;
        this.questions = questions;
    }

    public String getName() {
        return name;
    }

    public Set<ApplicationQuestionReadOnlyViewModel> getQuestions() {
        return questions;
    }
}
