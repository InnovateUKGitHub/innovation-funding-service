package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.management.viewmodel.AllApplicationsRowViewModel;
import org.innovateuk.ifs.management.viewmodel.AllApplicationsViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Builds the Competition Management All Applications view model.
 */
@Component
public class AllApplicationsPageModelPopulator {

    @Autowired
    private ApplicationSummaryRestService applicationSummaryRestService;

    public AllApplicationsViewModel populateModel(long competitionId) {
        CompetitionSummaryResource competitionSummaryResource = applicationSummaryRestService
                .getCompetitionSummary(competitionId)
                .getSuccessObjectOrThrowException();

        return new AllApplicationsViewModel(
                competitionSummaryResource.getCompetitionId(),
                competitionSummaryResource.getCompetitionName(),
                competitionSummaryResource.getTotalNumberOfApplications(),
                competitionSummaryResource.getApplicationsStarted(),
                competitionSummaryResource.getApplicationsInProgress(),
                competitionSummaryResource.getApplicationsSubmitted(),
                getApplications(competitionId)
        );
    }

    private List<AllApplicationsRowViewModel> getApplications(long competitionId) {
        // TODO: Implement sorting - INFUND-8054
        // TODO: Implement filtering - INFUND-8010
        // TODO: Pagination required - INFUND-8067

        ApplicationSummaryPageResource applicationSummaryPageResource = applicationSummaryRestService
                .getAllApplications(competitionId, "", 0, Integer.MAX_VALUE)
                .getSuccessObjectOrThrowException();

        return simpleMap(
                applicationSummaryPageResource.getContent(),
                applicationSummaryResource -> new AllApplicationsRowViewModel(
                        applicationSummaryResource.getId(),
                        applicationSummaryResource.getName(),
                        applicationSummaryResource.getLead(),
                        applicationSummaryResource.getInnovationArea(),
                        applicationSummaryResource.getStatus(),
                        applicationSummaryResource.getCompletedPercentage()
                )
        );
    }
}
