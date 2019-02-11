package org.innovateuk.ifs.competitionsetup.completionstage.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.completionstage.viewmodel.CompletionStageViewModel;
import org.innovateuk.ifs.competitionsetup.core.populator.CompetitionSetupSectionModelPopulator;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.GeneralSetupViewModel;
import org.springframework.stereotype.Service;

/**
 * Service to populate the Completion Stage page in Competition Setup.
 */
@Service
public class CompletionStageViewModelPopulator implements CompetitionSetupSectionModelPopulator {

    @Override
    public CompetitionSetupSection sectionToPopulateModel() {
        return CompetitionSetupSection.COMPLETION_STAGE;
    }

    @Override
    public CompletionStageViewModel populateModel(GeneralSetupViewModel generalViewModel, CompetitionResource competitionResource) {
        return new CompletionStageViewModel(generalViewModel);
    }
}
