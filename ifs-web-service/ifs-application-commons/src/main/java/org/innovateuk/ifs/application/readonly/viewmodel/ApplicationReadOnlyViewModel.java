package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;

import java.math.BigDecimal;
import java.util.Set;

public class ApplicationReadOnlyViewModel {

    private final ApplicationReadOnlySettings settings;
    private final Set<ApplicationSectionReadOnlyViewModel> sections;
    private final BigDecimal applicationScore;

    public ApplicationReadOnlyViewModel(ApplicationReadOnlySettings settings, Set<ApplicationSectionReadOnlyViewModel> sections, BigDecimal applicationScore) {
        this.settings = settings;
        this.sections = sections;
        this.applicationScore = applicationScore;
    }

    public ApplicationReadOnlySettings getSettings() {
        return settings;
    }

    public Set<ApplicationSectionReadOnlyViewModel> getSections() {
        return sections;
    }

    public BigDecimal getApplicationScore() { return applicationScore; }
}
