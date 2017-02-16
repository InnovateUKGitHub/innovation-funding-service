package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.management.viewmodel.AllApplicationsRowViewModel;
import org.innovateuk.ifs.management.viewmodel.AllApplicationsViewModel;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Builds the Competition Management All Applications view model.
 */
@Component
public class AllApplicationsPageModelPopulator {

    @Autowired
    private ApplicationSummaryRestService applicationSummaryRestService;

    public AllApplicationsViewModel populateModel(long competitionId, String origin, int page, String sorting, String filter) {
        CompetitionSummaryResource competitionSummaryResource = applicationSummaryRestService
                .getCompetitionSummary(competitionId)
                .getSuccessObjectOrThrowException();

        ApplicationSummaryPageResource applicationSummaryPageResource = applicationSummaryRestService
                .getAllApplications(competitionId, sorting, page, 3, filter)
                .getSuccessObjectOrThrowException();

        return new AllApplicationsViewModel(
                competitionSummaryResource.getCompetitionId(),
                competitionSummaryResource.getCompetitionName(),
                competitionSummaryResource.getTotalNumberOfApplications(),
                competitionSummaryResource.getApplicationsStarted(),
                competitionSummaryResource.getApplicationsInProgress(),
                competitionSummaryResource.getApplicationsSubmitted(),
                sorting,
                filter,
                getApplications(applicationSummaryPageResource),
                new PaginationViewModel(applicationSummaryPageResource, origin)
        );
    }

    private List<AllApplicationsRowViewModel> getApplications(ApplicationSummaryPageResource applicationSummaryPageResource) {
        // TODO: Implement sorting - INFUND-8054
        // TODO: Implement filtering - INFUND-8010

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
