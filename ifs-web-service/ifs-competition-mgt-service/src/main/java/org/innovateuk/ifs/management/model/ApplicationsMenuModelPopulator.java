package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.management.viewmodel.ApplicationsMenuViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Competition Management Applications Menu view model.
 */
@Component
public class ApplicationsMenuModelPopulator {

    @Autowired
    private ApplicationSummaryRestService applicationSummaryRestService;

    public ApplicationsMenuViewModel populateModel(Long competitionId) {
        CompetitionSummaryResource summary = applicationSummaryRestService.getCompetitionSummary(competitionId).getSuccessObjectOrThrowException();

        return new ApplicationsMenuViewModel(
                summary.getCompetitionId(),
                summary.getCompetitionName(),
                summary.getAssessorsInvited(),
                summary.getApplicationsInProgress(),
                summary.getApplicationsSubmitted(),
                summary.getIneligibleApplications()
        );
    }
}
