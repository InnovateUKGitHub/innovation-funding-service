package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.innovateuk.ifs.management.viewmodel.SubmittedApplicationsRowViewModel;
import org.innovateuk.ifs.management.viewmodel.SubmittedApplicationsViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Builds the Competition Management Submitted Applications view model.
 */
@Component
public class IneligibleApplicationsModelPopulator {

    @Autowired
    private ApplicationSummaryRestService applicationSummaryRestService;

    public SubmittedApplicationsViewModel populateModel(long competitionId, String origin, int page, String sorting, String filter) {
        CompetitionSummaryResource competitionSummary = applicationSummaryRestService
                .getCompetitionSummary(competitionId)
                .getSuccessObjectOrThrowException();

        ApplicationSummaryPageResource summaryPageResource = applicationSummaryRestService
                .getIneligibleApplications(competitionId, sorting, page, 20, filter)
                .getSuccessObjectOrThrowException();

        return new SubmittedApplicationsViewModel(
                competitionSummary.getCompetitionId(),
                competitionSummary.getCompetitionName(),
                competitionSummary.getAssessorDeadline(),
                competitionSummary.getApplicationsSubmitted(),
                sorting,
                filter,
                getApplications(summaryPageResource),
                new PaginationViewModel(summaryPageResource, origin)
        );
    }

    private List<SubmittedApplicationsRowViewModel> getApplications(ApplicationSummaryPageResource summaryPageResource) {
        return simpleMap(
                summaryPageResource.getContent(),
                applicationSummaryResource -> new SubmittedApplicationsRowViewModel(
                        applicationSummaryResource.getId(),
                        applicationSummaryResource.getName(),
                        applicationSummaryResource.getLead(),
                        applicationSummaryResource.getInnovationArea(),
                        applicationSummaryResource.getNumberOfPartners(),
                        applicationSummaryResource.getGrantRequested(),
                        applicationSummaryResource.getTotalProjectCost(),
                        applicationSummaryResource.getDuration()
                )
        );
    }
}
