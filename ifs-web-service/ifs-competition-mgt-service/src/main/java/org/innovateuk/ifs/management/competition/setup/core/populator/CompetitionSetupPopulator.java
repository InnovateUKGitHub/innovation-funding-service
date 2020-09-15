package org.innovateuk.ifs.management.competition.setup.core.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionStateSetupViewModel;
import org.innovateuk.ifs.management.competition.setup.core.viewmodel.GeneralSetupViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.util.SecurityRuleUtil;
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

    public GeneralSetupViewModel populateGeneralModelAttributes(CompetitionResource competitionResource, UserResource userResource, CompetitionSetupSection section) {

        Map<CompetitionSetupSection, Optional<Boolean>> statuses = competitionSetupRestService.getSectionStatuses(competitionResource.getId())
                .getSuccess();

        Map<CompetitionSetupSection, Boolean> statusesAndValues = simpleMapKeyAndValue(statuses, key -> key, value -> value.orElse(false));

        boolean editable = isSectionEditable(statusesAndValues, section, competitionResource);

        boolean isInitialComplete = competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionResource.getId());

        boolean isIfsAdmin = SecurityRuleUtil.isIFSAdmin(userResource);

        GeneralSetupViewModel viewModel = new GeneralSetupViewModel(editable, competitionResource, section, CompetitionSetupSection.values(),
                isInitialComplete, isIfsAdmin);

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
