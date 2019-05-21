package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;

import java.util.Set;

public class ApplicationReadOnlyViewModel {

    private final ApplicationReadOnlySettings settings;
    private final Set<ApplicationSectionReadOnlyViewModel> sections;

    public ApplicationReadOnlyViewModel(ApplicationReadOnlySettings settings, Set<ApplicationSectionReadOnlyViewModel> sections) {
        this.settings = settings;
        this.sections = sections;
    }

    public ApplicationReadOnlySettings getSettings() {
        return settings;
    }

    public Set<ApplicationSectionReadOnlyViewModel> getSections() {
        return sections;
    }
}
