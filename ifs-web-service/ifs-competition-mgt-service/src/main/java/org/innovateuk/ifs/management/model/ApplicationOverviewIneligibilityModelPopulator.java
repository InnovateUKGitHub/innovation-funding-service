package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.IneligibleOutcomeResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.viewmodel.ApplicationOverviewIneligibilityViewModel;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

import static org.innovateuk.ifs.application.resource.ApplicationState.INELIGIBLE;
import static org.innovateuk.ifs.application.resource.ApplicationState.INELIGIBLE_INFORMED;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.ASSESSOR_FEEDBACK;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.FUNDERS_PANEL;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.PROJECT_SETUP;

/**
 * Build the model for the Competition Management Application Overview Application Ineligibility details.
 */
@Component
public class ApplicationOverviewIneligibilityModelPopulator {

    public ApplicationOverviewIneligibilityViewModel populateModel(final ApplicationResource applicationResource, CompetitionResource competitionResource) {
        boolean readOnly = isCompetitionBeyondAssessment(competitionResource);
        if (isApplicationIneligible(applicationResource)) {
            IneligibleOutcomeResource ineligibleOutcome = applicationResource.getIneligibleOutcome();
            return new ApplicationOverviewIneligibilityViewModel(
                    readOnly,
                    ineligibleOutcome.getRemovedBy(),
                    ineligibleOutcome.getRemovedOn(),
                    ineligibleOutcome.getReason());
        }

        return new ApplicationOverviewIneligibilityViewModel(readOnly);
    }

    private boolean isApplicationIneligible(final ApplicationResource applicationResource) {
        return EnumSet.of(INELIGIBLE, INELIGIBLE_INFORMED).contains(applicationResource.getApplicationState());
    }

    private boolean isCompetitionBeyondAssessment(final CompetitionResource competitionResource) {
        return EnumSet.of(FUNDERS_PANEL, ASSESSOR_FEEDBACK, PROJECT_SETUP).contains(competitionResource.getCompetitionStatus());
    }
}
