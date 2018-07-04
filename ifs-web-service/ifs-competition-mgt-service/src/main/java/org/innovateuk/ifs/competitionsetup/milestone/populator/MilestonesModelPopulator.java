package org.innovateuk.ifs.competitionsetup.milestone.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.core.populator.CompetitionSetupSectionModelPopulator;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.competitionsetup.milestone.viewmodel.MilestonesViewModel;
import org.springframework.stereotype.Service;

/**
 * Populates the model for the milestones competition setup section.
 */
@Service
public class MilestonesModelPopulator implements CompetitionSetupSectionModelPopulator {

    @Override
    public CompetitionSetupSection sectionToPopulateModel() {
        return CompetitionSetupSection.MILESTONES;
    }

    @Override
    public CompetitionSetupViewModel populateModel(GeneralSetupViewModel generalViewModel, CompetitionResource competitionResource) {

        return new MilestonesViewModel(generalViewModel);
    }
}
