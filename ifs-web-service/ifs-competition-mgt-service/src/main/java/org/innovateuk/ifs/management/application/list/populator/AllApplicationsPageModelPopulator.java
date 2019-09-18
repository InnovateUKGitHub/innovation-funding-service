package org.innovateuk.ifs.management.application.list.populator;

import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.management.application.list.viewmodel.AllApplicationsRowViewModel;
import org.innovateuk.ifs.management.application.list.viewmodel.AllApplicationsViewModel;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.user.resource.Role.INNOVATION_LEAD;
import static org.innovateuk.ifs.user.resource.Role.STAKEHOLDER;
import static org.innovateuk.ifs.user.resource.Role.SUPPORT;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

/**
 * Builds the Competition Management All Applications view model.
 */
@Component
public class AllApplicationsPageModelPopulator {

    @Autowired
    private ApplicationSummaryRestService applicationSummaryRestService;

    public AllApplicationsViewModel populateModel(long competitionId, int page, String sorting, Optional<String> filter, UserResource user) {
        CompetitionSummaryResource competitionSummaryResource = applicationSummaryRestService
                .getCompetitionSummary(competitionId)
                .getSuccess();

        ApplicationSummaryPageResource applicationSummaryPageResource = applicationSummaryRestService
                .getAllApplications(competitionId, sorting, page, 20, filter)
                .getSuccess();

        return new AllApplicationsViewModel(
                competitionSummaryResource.getCompetitionId(),
                competitionSummaryResource.getCompetitionName(),
                competitionSummaryResource.getTotalNumberOfApplications(),
                competitionSummaryResource.getApplicationsStarted(),
                competitionSummaryResource.getApplicationsInProgress(),
                competitionSummaryResource.getApplicationsSubmitted(),
                sorting,
                filter.orElse(""),
                getApplications(applicationSummaryPageResource),
                new Pagination(applicationSummaryPageResource),
                user.hasRole(SUPPORT) || user.hasRole(INNOVATION_LEAD) || user.hasRole(STAKEHOLDER)? "Dashboard" : "Applications",
                user.hasRole(SUPPORT) || user.hasRole(INNOVATION_LEAD) || user.hasRole(STAKEHOLDER)? "/dashboard/live" : "/competition/" + competitionId + "/applications",
                user.hasRole(SUPPORT)
        );
    }

    private List<AllApplicationsRowViewModel> getApplications(ApplicationSummaryPageResource applicationSummaryPageResource) {

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
