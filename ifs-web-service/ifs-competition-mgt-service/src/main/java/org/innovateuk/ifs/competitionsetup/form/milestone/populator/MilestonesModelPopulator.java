package org.innovateuk.ifs.competitionsetup.form.milestone.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.form.common.populator.CompetitionSetupSectionModelPopulator;
import org.innovateuk.ifs.competitionsetup.form.common.viewmodel.CompetitionSetupViewModel;
import org.innovateuk.ifs.competitionsetup.form.milestone.viewmodel.MilestonesViewModel;
import org.innovateuk.ifs.competitionsetup.form.common.viewmodel.GeneralSetupViewModel;
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
