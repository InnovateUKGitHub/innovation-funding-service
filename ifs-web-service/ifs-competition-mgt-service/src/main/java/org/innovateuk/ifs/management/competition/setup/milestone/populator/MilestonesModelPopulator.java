package org.innovateuk.ifs.management.competition.setup.milestone.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.management.competition.setup.completionstage.util.CompletionStageUtils;
import org.innovateuk.ifs.management.competition.setup.core.populator.CompetitionSetupSectionModelPopulator;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.milestone.viewmodel.MilestonesViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Populates the model for the milestones competition setup section.
 */
@Service
public class MilestonesModelPopulator implements CompetitionSetupSectionModelPopulator {

    @Autowired
    private CompletionStageUtils completionStageUtils;

    @Override
    public CompetitionSetupSection sectionToPopulateModel() {
        return CompetitionSetupSection.MILESTONES;
    }

    @Override
    public CompetitionSetupViewModel populateModel(GeneralSetupViewModel generalViewModel, CompetitionResource competitionResource) {

        return new MilestonesViewModel(generalViewModel, completionStageUtils);
    }
}
