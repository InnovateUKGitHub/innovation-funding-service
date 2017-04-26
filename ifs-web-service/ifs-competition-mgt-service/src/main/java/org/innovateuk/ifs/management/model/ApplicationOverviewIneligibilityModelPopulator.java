package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.management.viewmodel.ApplicationOverviewIneligibilityViewModel;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.EnumSet;

import static org.innovateuk.ifs.application.resource.ApplicationState.INELIGIBLE;
import static org.innovateuk.ifs.application.resource.ApplicationState.INELIGIBLE_INFORMED;
import static org.innovateuk.ifs.management.viewmodel.ApplicationOverviewIneligibilityViewModel.createViewModelEligible;

/**
 * Build the model for the Competition Management Application Overview Application Ineligibility details.
 */
@Component
public class ApplicationOverviewIneligibilityModelPopulator {

    public ApplicationOverviewIneligibilityViewModel populateModel(final ApplicationResource applicationResource) {
        if (isApplicationIneligible(applicationResource)) {
            String removedBy = "Removed by";
            ZonedDateTime removedOn = null;
            return new ApplicationOverviewIneligibilityViewModel(removedBy, removedOn, applicationResource.getIneligibleReason());
        }

        return createViewModelEligible();
    }

    private boolean isApplicationIneligible(final ApplicationResource applicationResource) {
        return EnumSet.of(INELIGIBLE, INELIGIBLE_INFORMED).contains(applicationResource.getApplicationState());
    }

}
