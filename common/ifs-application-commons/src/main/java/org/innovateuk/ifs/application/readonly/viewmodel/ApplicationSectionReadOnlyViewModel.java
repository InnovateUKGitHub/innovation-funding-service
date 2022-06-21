package org.innovateuk.ifs.application.readonly.viewmodel;

import java.util.Set;

public class ApplicationSectionReadOnlyViewModel {

    private String name;

    private boolean finances;

    private boolean termsAndConditions;

    private boolean visible;

    private Set<ApplicationQuestionReadOnlyViewModel> questions;

    public ApplicationSectionReadOnlyViewModel(String name, boolean finances, boolean termsAndConditions, boolean visible, Set<ApplicationQuestionReadOnlyViewModel> questions) {
        this.name = name;
        this.finances = finances;
        this.questions = questions;
        this.visible = visible;
        this.termsAndConditions = termsAndConditions;
    }

    public String getName() {
        return name;
    }

    public boolean isFinances() {
        return finances;
    }

    public boolean isTermsAndConditions() { return termsAndConditions; }

    public boolean isVisible() { return visible; }

    public Set<ApplicationQuestionReadOnlyViewModel> getQuestions() {
        return questions;
    }

    public boolean isScored() {
        return questions != null && questions.stream()
                .anyMatch(ApplicationQuestionReadOnlyViewModel::hasScore);
    }
}
