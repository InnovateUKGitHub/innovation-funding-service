package org.innovateuk.ifs.management.application.list.populator;

import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.management.application.view.form.IneligibleApplicationsForm;
import org.innovateuk.ifs.management.application.list.viewmodel.IneligibleApplicationsRowViewModel;
import org.innovateuk.ifs.management.application.list.viewmodel.IneligibleApplicationsViewModel;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.user.resource.Role.INNOVATION_LEAD;
import static org.innovateuk.ifs.user.resource.Role.SUPPORT;
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
                .getSuccess();

        ApplicationSummaryPageResource summaryPageResource = applicationSummaryRestService
                .getIneligibleApplications(competitionId,
                        sorting,
                        page,
                        20,
                        Optional.of(filterForm.getFilterSearch()),
                        filterForm.getFilterInform())
                .getSuccess();

        return new IneligibleApplicationsViewModel(
                competitionSummary.getCompetitionId(),
                competitionSummary.getCompetitionName(),
                sorting,
                filterForm.getFilterSearch(),
                getApplications(summaryPageResource),
                new Pagination(summaryPageResource, origin),
                user.hasRole(INNOVATION_LEAD) || user.hasRole(SUPPORT)
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
