package org.innovateuk.ifs.management.funding.populator;


import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.form.FundingNotificationFilterForm;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.application.viewmodel.ManageFundingApplicationViewModel;
import org.innovateuk.ifs.management.competition.populator.CompetitionInFlightStatsModelPopulator;
import org.innovateuk.ifs.management.competition.viewmodel.CompetitionInFlightStatsViewModel;
import org.innovateuk.ifs.management.cookie.CompetitionManagementCookieController;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Populator for the manage funding applications view model
 */
@Component
public class ManageFundingApplicationsModelPopulator {

    private static int DEFAULT_PAGE_SIZE = 20;

    private CompetitionInFlightStatsModelPopulator competitionInFlightStatsModelPopulator;
    private CompetitionService competitionService;
    private ApplicationSummaryRestService applicationSummaryRestService;

    public ManageFundingApplicationsModelPopulator(CompetitionInFlightStatsModelPopulator competitionInFlightStatsModelPopulator,
                                                   CompetitionService competitionService,
                                                   ApplicationSummaryRestService applicationSummaryRestService) {
        this.competitionInFlightStatsModelPopulator = competitionInFlightStatsModelPopulator;
        this.competitionService = competitionService;
        this.applicationSummaryRestService = applicationSummaryRestService;
    }

    public ManageFundingApplicationViewModel populate(FundingNotificationFilterForm queryForm,
                                                      long competitionId,
                                                      String queryString,
                                                      long totalSubmittableApplications) {
        ApplicationSummaryPageResource results = applicationSummaryRestService.getWithFundingDecisionApplications(
                competitionId, queryForm.getSortField(), queryForm.getPage(),
                DEFAULT_PAGE_SIZE, Optional.of(queryForm.getStringFilter()),
                queryForm.getSendFilter(), queryForm.getFundingFilter()).getSuccess();

        CompetitionResource competitionResource = competitionService.getById(competitionId);
        CompetitionInFlightStatsViewModel keyStatistics = competitionInFlightStatsModelPopulator.populateStatsViewModel(competitionResource);
        boolean selectAllDisabled = totalSubmittableApplications > CompetitionManagementCookieController.SELECTION_LIMIT;
        Pagination pagination = new Pagination(results, queryString);

        return new ManageFundingApplicationViewModel(
                results,
                keyStatistics,
                pagination,
                queryForm.getSortField(),
                competitionId,
                competitionResource.getName(),
                selectAllDisabled);
    }
}
