package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.management.viewmodel.SubmittedApplicationsRowViewModel;
import org.innovateuk.ifs.management.viewmodel.SubmittedApplicationsViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Builds the Competition Management Submitted Applications view model.
 */
@Component
public class SubmittedApplicationsModelPopulator {

    @Autowired
    private ApplicationSummaryRestService applicationSummaryRestService;

    public SubmittedApplicationsViewModel populateModel(long competitionId) {
        CompetitionSummaryResource competitionSummary = applicationSummaryRestService
                .getCompetitionSummary(competitionId)
                .getSuccessObjectOrThrowException();


        return new SubmittedApplicationsViewModel(
                competitionSummary.getCompetitionId(),
                competitionSummary.getCompetitionName(),
                competitionSummary.getAssessorDeadline(),
                competitionSummary.getApplicationsSubmitted(),
                getApplications(competitionId)
        );
    }

    private List<SubmittedApplicationsRowViewModel> getApplications(long competitionId) {
        // TODO: Implement sorting - INFUND-7952
        // TODO: Pagination needs to be implemented properly

        ApplicationSummaryPageResource summaryPageResource = applicationSummaryRestService
                .getSubmittedApplications(competitionId, "", 0, Integer.MAX_VALUE)
                .getSuccessObjectOrThrowException();

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
