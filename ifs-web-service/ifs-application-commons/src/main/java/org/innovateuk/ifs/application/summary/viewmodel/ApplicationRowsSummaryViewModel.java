package org.innovateuk.ifs.application.summary.viewmodel;

import org.innovateuk.ifs.application.summary.ApplicationSummarySettings;

import java.util.Set;

public class ApplicationRowsSummaryViewModel {

    private final ApplicationSummarySettings settings;
    private final Set<ApplicationRowGroupSummaryViewModel> sections;

    public ApplicationRowsSummaryViewModel(ApplicationSummarySettings settings, Set<ApplicationRowGroupSummaryViewModel> sections) {
        this.settings = settings;
        this.sections = sections;
    }

    public ApplicationSummarySettings getSettings() {
        return settings;
    }

    public Set<ApplicationRowGroupSummaryViewModel> getSections() {
        return sections;
    }
}
