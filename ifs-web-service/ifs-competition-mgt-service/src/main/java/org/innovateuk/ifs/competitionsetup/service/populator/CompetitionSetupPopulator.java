package org.innovateuk.ifs.competitionsetup.service.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competitionsetup.viewmodel.CompetitionStateSetupViewModel;
import org.innovateuk.ifs.competitionsetup.viewmodel.fragments.GeneralSetupViewModel;
import org.springframework.stereotype.Service;

/**
 * General populator for non section specific
 */
@Service
public class CompetitionSetupPopulator {
    public GeneralSetupViewModel populateGeneralModelAttributes(CompetitionResource competitionResource, CompetitionSetupSection section) {
        boolean editable = (!competitionResource.getSectionSetupStatus().containsKey(section)
                || !competitionResource.getSectionSetupStatus().get(section))
                && !section.preventEdit(competitionResource);

        GeneralSetupViewModel viewModel = new GeneralSetupViewModel(editable, competitionResource, section, CompetitionSetupSection.values(), competitionResource.isInitialDetailsComplete());

        if (section.hasDisplayableSetupFragment()) {
            viewModel.setCurrentSectionFragment("section-" + section.getPath());
        }

        viewModel.setState(populateCompetitionStateModelAttributes(competitionResource, section));

        return viewModel;
    }

    private CompetitionStateSetupViewModel populateCompetitionStateModelAttributes(CompetitionResource competitionResource, CompetitionSetupSection section) {
        return new CompetitionStateSetupViewModel(section.preventEdit(competitionResource),
                competitionResource.isSetupAndLive(),
                competitionResource.getSetupComplete());
    }
}
