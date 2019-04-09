package org.innovateuk.ifs.application.summary.viewmodel;

import java.util.Set;

public class NewApplicationSummaryViewModel {

    private Set<NewSectionSummaryViewModel> sections;

    public NewApplicationSummaryViewModel(Set<NewSectionSummaryViewModel> sections) {
        this.sections = sections;
    }

    public Set<NewSectionSummaryViewModel> getSections() {
        return sections;
    }
}
