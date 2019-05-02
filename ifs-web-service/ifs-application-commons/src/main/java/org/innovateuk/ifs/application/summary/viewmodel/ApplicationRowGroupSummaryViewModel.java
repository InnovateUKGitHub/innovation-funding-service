package org.innovateuk.ifs.application.summary.viewmodel;

import java.util.Set;

public class ApplicationRowGroupSummaryViewModel {

    private String name;

    private Set<ApplicationRowSummaryViewModel> questions;

    public ApplicationRowGroupSummaryViewModel(String name, Set<ApplicationRowSummaryViewModel> questions) {
        this.name = name;
        this.questions = questions;
    }

    public String getName() {
        return name;
    }

    public Set<ApplicationRowSummaryViewModel> getQuestions() {
        return questions;
    }
}
