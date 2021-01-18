package org.innovateuk.ifs.management.competition.setup.core.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;

import static java.lang.String.format;

public abstract class CompetitionSetupViewModel {
    protected GeneralSetupViewModel generalSetupViewModel;

    public GeneralSetupViewModel getGeneral() {
        return generalSetupViewModel;
    }

    public String getNextSection(CompetitionResource competition, CompetitionSetupSection section) {
        return format("redirect:/competition/setup/%d/section/%s", competition.getId(), section.getPostMarkCompletePath());
    }
}
