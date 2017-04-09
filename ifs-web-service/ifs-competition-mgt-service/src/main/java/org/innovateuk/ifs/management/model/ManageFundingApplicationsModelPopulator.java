package org.innovateuk.ifs.management.model;


import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.form.ManageFundingApplicationsQueryForm;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.viewmodel.CompetitionInFlightStatsViewModel;
import org.innovateuk.ifs.management.viewmodel.ManageFundingApplicationViewModel;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Populator for the manage funding applications view model
 */
@Component
public class ManageFundingApplicationsModelPopulator {

    private static int DEFAULT_PAGE_SIZE = 20;

    @Autowired
    private CompetitionInFlightStatsModelPopulator competitionInFlightStatsModelPopulator;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private ApplicationSummaryRestService applicationSummaryRestService;

    public ManageFundingApplicationViewModel populate(ManageFundingApplicationsQueryForm queryForm, long competitionId, String queryString) {
        ApplicationSummaryPageResource results = applicationSummaryRestService.getWithFundingDecisionApplications(competitionId,
                queryForm.getSortField(), queryForm.getPage(),
                DEFAULT_PAGE_SIZE, queryForm.getStringFilter(),
                queryForm.getSendFilter(), queryForm.getFundingFilter()).getSuccessObjectOrThrowException();
        CompetitionResource competitionResource = competitionService.getById(competitionId);
        CompetitionInFlightStatsViewModel keyStatistics = competitionInFlightStatsModelPopulator.populateStatsViewModel(competitionResource);
        PaginationViewModel paginationViewModel = new PaginationViewModel(results, queryString);
        return new ManageFundingApplicationViewModel(
                results,
                keyStatistics,
                paginationViewModel,
                queryForm.getSortField(),
                competitionId,
                competitionResource.getName());
    }
}
