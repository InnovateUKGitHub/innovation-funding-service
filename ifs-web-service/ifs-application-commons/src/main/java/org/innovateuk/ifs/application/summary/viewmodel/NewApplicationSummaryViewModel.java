package org.innovateuk.ifs.application.summary.viewmodel;

import org.innovateuk.ifs.application.summary.ApplicationSummarySettings;

import java.util.Set;

public class NewApplicationSummaryViewModel {

    private final ApplicationSummarySettings settings;
    private final Set<NewSectionSummaryViewModel> sections;

    public NewApplicationSummaryViewModel(ApplicationSummarySettings settings, Set<NewSectionSummaryViewModel> sections) {
        this.settings = settings;
        this.sections = sections;
    }

    public ApplicationSummarySettings getSettings() {
        return settings;
    }

    public Set<NewSectionSummaryViewModel> getSections() {
        return sections;
    }
}
