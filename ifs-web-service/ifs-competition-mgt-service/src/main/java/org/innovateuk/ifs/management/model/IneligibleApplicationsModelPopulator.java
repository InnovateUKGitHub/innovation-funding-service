package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.management.form.IneligibleApplicationsForm;
import org.innovateuk.ifs.management.viewmodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Builds the Competition Management Submitted Applications view model.
 */
@Component
public class IneligibleApplicationsModelPopulator {

    @Autowired
    private ApplicationSummaryRestService applicationSummaryRestService;

    public IneligibleApplicationsViewModel populateModel(long competitionId, String origin, int page, String sorting, IneligibleApplicationsForm filterForm) {
        CompetitionSummaryResource competitionSummary = applicationSummaryRestService
                .getCompetitionSummary(competitionId)
                .getSuccessObjectOrThrowException();

        ApplicationSummaryPageResource summaryPageResource = applicationSummaryRestService
                .getIneligibleApplications(competitionId, sorting, page, 20, filterForm.getFilterSearch(),filterForm.getFilterInform())
                .getSuccessObjectOrThrowException();

        return new IneligibleApplicationsViewModel(
                competitionSummary.getCompetitionId(),
                competitionSummary.getCompetitionName(),
                sorting,
                filterForm.getFilterSearch(),
                getApplications(summaryPageResource),
                new PaginationViewModel(summaryPageResource, origin)
        );
    }

    private List<IneligibleApplicationsRowViewModel> getApplications(ApplicationSummaryPageResource summaryPageResource) {
        return simpleMap(
                summaryPageResource.getContent(),
                applicationSummaryResource -> new IneligibleApplicationsRowViewModel(
                        applicationSummaryResource.getId(),
                        applicationSummaryResource.getName(),
                        applicationSummaryResource.getLead(),
                        applicationSummaryResource.getLeadApplicant(),
                        applicationSummaryResource.isIneligibleInformed()
                )
        );
    }
}
