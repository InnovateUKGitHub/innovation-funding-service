package org.innovateuk.ifs.competitionsetup.core.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.competitionsetup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.CompetitionStateSetupViewModel;
import org.innovateuk.ifs.competitionsetup.core.viewmodel.GeneralSetupViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMapKeyAndValue;

/**
 * General populator for non section specific
 */
@Service
public class CompetitionSetupPopulator {
    @Autowired
    private CompetitionSetupService competitionSetupService;

    @Autowired
    private CompetitionSetupRestService competitionSetupRestService;

    public GeneralSetupViewModel populateGeneralModelAttributes(CompetitionResource competitionResource, CompetitionSetupSection section) {

        Map<CompetitionSetupSection, Optional<Boolean>> statuses = competitionSetupRestService.getSectionStatuses(competitionResource.getId())
                .getSuccess();

        Map<CompetitionSetupSection, Boolean> statusesAndValues = simpleMapKeyAndValue(statuses, key -> key, value -> value.orElse(false));

        boolean editable = isSectionEditable(statusesAndValues, section, competitionResource);

        GeneralSetupViewModel viewModel = new GeneralSetupViewModel(editable, competitionResource, section, CompetitionSetupSection.values(),
                competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionResource.getId()));

        if (section.hasDisplayableSetupFragment()) {
            viewModel.setCurrentSectionFragment("section-" + section.getPath());
        }

        viewModel.setState(populateCompetitionStateModelAttributes(competitionResource, section));

        return viewModel;
    }

    private boolean isSectionEditable(Map<CompetitionSetupSection, Boolean> statuses, CompetitionSetupSection section, CompetitionResource competitionResource) {
        return !statuses.getOrDefault(section, false) && !section.preventEdit(competitionResource);
    }

    private CompetitionStateSetupViewModel populateCompetitionStateModelAttributes(CompetitionResource competitionResource, CompetitionSetupSection section) {
        return new CompetitionStateSetupViewModel(section.preventEdit(competitionResource),
                competitionResource.isSetupAndLive(),
                competitionResource.getSetupComplete(),
                competitionResource.getCompetitionStatus());
    }
}
