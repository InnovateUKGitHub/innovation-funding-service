package org.innovateuk.ifs.management.application.view.populator;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.IneligibleOutcomeResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.application.view.viewmodel.ApplicationOverviewIneligibilityViewModel;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

import static org.innovateuk.ifs.application.resource.ApplicationState.INELIGIBLE;
import static org.innovateuk.ifs.application.resource.ApplicationState.INELIGIBLE_INFORMED;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.*;

/**
 * Build the model for the Competition Management Application Overview Application Ineligibility details.
 */
@Component
public class ApplicationOverviewIneligibilityModelPopulator {

    public ApplicationOverviewIneligibilityViewModel populateModel(final ApplicationResource applicationResource) {
        boolean readOnly = isCompetitionBeyondAssessment(applicationResource);
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

    private boolean isCompetitionBeyondAssessment(final ApplicationResource applicationResource) {
        return EnumSet.of(FUNDERS_PANEL, ASSESSOR_FEEDBACK, PROJECT_SETUP).contains(applicationResource.getCompetitionStatus());
    }
}
