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
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${ifs.assessment.stage.competition.enabled}")
    private boolean isAssessmentStageEnabled;

    @Value("${ifs.expression.of.interest.enabled}")
    private boolean isExpressionOfInterestEnabled;

    @Value("${ifs.project.impact.enabled}")
    private boolean isProjectImpactEnabled;

    public GeneralSetupViewModel populateGeneralModelAttributes(CompetitionResource competitionResource, UserResource userResource, CompetitionSetupSection section) {

        Map<CompetitionSetupSection, Optional<Boolean>> statuses = competitionSetupRestService.getSectionStatuses(competitionResource.getId())
                .getSuccess();
        boolean firstTimeInForm = !statuses.get(section).isPresent();

        Map<CompetitionSetupSection, Boolean> statusesAndValues = simpleMapKeyAndValue(statuses, key -> key, value -> value.orElse(false));

        boolean editable = isSectionEditable(statusesAndValues, section, competitionResource, userResource);
        boolean isInitialComplete = competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionResource.getId());

        boolean isIfsAdmin = SecurityRuleUtil.hasIFSAdminAuthority(userResource);

        GeneralSetupViewModel viewModel = new GeneralSetupViewModel(editable, firstTimeInForm, competitionResource, section, CompetitionSetupSection.values(),
                isInitialComplete, isIfsAdmin, isAssessmentStageEnabled, isExpressionOfInterestEnabled, isProjectImpactEnabled);

        if (section.hasDisplayableSetupFragment()) {
            viewModel.setCurrentSectionFragment("section-" + section.getPath());
        }

        viewModel.setState(populateCompetitionStateModelAttributes(competitionResource, section, userResource));

        return viewModel;
    }

    private boolean isSectionEditable(Map<CompetitionSetupSection, Boolean> statuses, CompetitionSetupSection section, CompetitionResource competitionResource, UserResource loggedInUser) {
        return !statuses.getOrDefault(section, false) && !section.preventEdit(competitionResource, loggedInUser);
    }

    private CompetitionStateSetupViewModel populateCompetitionStateModelAttributes(CompetitionResource competitionResource, CompetitionSetupSection section, UserResource loggedInUser) {
        return new CompetitionStateSetupViewModel(
                section.preventEdit(competitionResource, loggedInUser),
                competitionResource.isSetupAndLive(),
                competitionResource.getSetupComplete(),
                competitionResource.getCompetitionStatus());
    }
}
