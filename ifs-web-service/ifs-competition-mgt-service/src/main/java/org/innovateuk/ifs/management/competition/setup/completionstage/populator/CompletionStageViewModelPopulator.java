package org.innovateuk.ifs.management.competition.setup.completionstage.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.completionstage.util.CompletionStageUtils;
import org.innovateuk.ifs.management.competition.setup.completionstage.viewmodel.CompletionStageViewModel;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupSectionModelPopulator;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service to populate the Completion Stage page in Competition Setup.
 */
@Service
public class CompletionStageViewModelPopulator implements CompetitionSetupSectionModelPopulator {

    @Autowired
    private CompletionStageUtils completionStageUtils;

    @Override
    public CompetitionSetupSection sectionToPopulateModel() {
        return CompetitionSetupSection.COMPLETION_STAGE;
    }

    @Override
    public CompletionStageViewModel populateModel(GeneralSetupViewModel generalViewModel, CompetitionResource competitionResource) {
        return new CompletionStageViewModel(generalViewModel, completionStageUtils);
    }
}
