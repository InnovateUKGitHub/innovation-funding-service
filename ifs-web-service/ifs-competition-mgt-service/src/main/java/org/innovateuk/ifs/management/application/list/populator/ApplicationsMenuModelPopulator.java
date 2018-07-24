package org.innovateuk.ifs.management.application.list.populator;

import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.management.application.list.viewmodel.ApplicationsMenuViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.innovateuk.ifs.user.resource.Role.INNOVATION_LEAD;

/**
 * Builds the model for the Competition Management Applications Menu view model.
 */
@Component
public class ApplicationsMenuModelPopulator {

    @Autowired
    private ApplicationSummaryRestService applicationSummaryRestService;

    public ApplicationsMenuViewModel populateModel(Long competitionId, UserResource user) {
        CompetitionSummaryResource summary = applicationSummaryRestService.getCompetitionSummary(competitionId).getSuccess();

        return new ApplicationsMenuViewModel(
                summary.getCompetitionId(),
                summary.getCompetitionName(),
                summary.getAssessorsInvited(),
                summary.getApplicationsInProgress(),
                summary.getApplicationsSubmitted(),
                summary.getIneligibleApplications(),
                user.hasRole(INNOVATION_LEAD)
        );
    }
}
