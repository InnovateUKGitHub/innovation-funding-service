package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.management.form.IneligibleApplicationsForm;
import org.innovateuk.ifs.management.viewmodel.IneligibleApplicationsRowViewModel;
import org.innovateuk.ifs.management.viewmodel.IneligibleApplicationsViewModel;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
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

    public IneligibleApplicationsViewModel populateModel(long competitionId,
                                                         String origin,
                                                         int page,
                                                         String sorting,
                                                         IneligibleApplicationsForm filterForm,
                                                         UserResource user) {
        CompetitionSummaryResource competitionSummary = applicationSummaryRestService
                .getCompetitionSummary(competitionId)
                .getSuccessObjectOrThrowException();

        ApplicationSummaryPageResource summaryPageResource = applicationSummaryRestService
                .getIneligibleApplications(competitionId,
                        sorting,
                        page,
                        20,
                        Optional.of(filterForm.getFilterSearch()),
                        filterForm.getFilterInform())
                .getSuccessObjectOrThrowException();

        return new IneligibleApplicationsViewModel(
                competitionSummary.getCompetitionId(),
                competitionSummary.getCompetitionName(),
                sorting,
                filterForm.getFilterSearch(),
                getApplications(summaryPageResource),
                new PaginationViewModel(summaryPageResource, origin),
                user.hasRole(UserRoleType.INNOVATION_LEAD) || user.hasRole(UserRoleType.SUPPORT)
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
