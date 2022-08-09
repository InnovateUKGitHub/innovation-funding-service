package org.innovateuk.ifs.management.decision.populator;


import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.management.decision.form.FundingNotificationFilterForm;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.application.view.viewmodel.ManageApplicationDecisionViewModel;
import org.innovateuk.ifs.management.competition.inflight.populator.CompetitionInFlightStatsModelPopulator;
import org.innovateuk.ifs.management.competition.inflight.viewmodel.CompetitionInFlightStatsViewModel;
import org.innovateuk.ifs.management.cookie.CompetitionManagementCookieController;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Populator for the manage funding applications view model
 */
@Component
public class ManageApplicationDecisionsModelPopulator {

    private static int DEFAULT_PAGE_SIZE = 20;

    private CompetitionInFlightStatsModelPopulator competitionInFlightStatsModelPopulator;
    private CompetitionRestService competitionRestService;
    private ApplicationSummaryRestService applicationSummaryRestService;

    public ManageApplicationDecisionsModelPopulator(CompetitionInFlightStatsModelPopulator competitionInFlightStatsModelPopulator,
                                                    CompetitionRestService competitionRestService,
                                                    ApplicationSummaryRestService applicationSummaryRestService) {
        this.competitionInFlightStatsModelPopulator = competitionInFlightStatsModelPopulator;
        this.competitionRestService = competitionRestService;
        this.applicationSummaryRestService = applicationSummaryRestService;
    }

    public ManageApplicationDecisionViewModel populate(FundingNotificationFilterForm queryForm,
                                                       long competitionId,
                                                       long totalSubmittableApplications) {
        ApplicationSummaryPageResource results = applicationSummaryRestService.getWithDecisionApplications(
                competitionId, queryForm.getSortField(), queryForm.getPage(),
                DEFAULT_PAGE_SIZE, Optional.of(queryForm.getStringFilter()),
                queryForm.getSendFilter(), queryForm.getFundingFilter(), Optional.of(queryForm.isEoi())).getSuccess();

        CompetitionResource competitionResource = competitionRestService.getCompetitionById(competitionId).getSuccess();
        CompetitionInFlightStatsViewModel keyStatistics = queryForm.isEoi() ?
                competitionInFlightStatsModelPopulator.populateEoiStatsViewModel(competitionResource) :
                competitionInFlightStatsModelPopulator.populateStatsViewModel(competitionResource);
        boolean selectAllDisabled = totalSubmittableApplications > CompetitionManagementCookieController.SELECTION_LIMIT;
        Pagination pagination = new Pagination(results);

        return new ManageApplicationDecisionViewModel(
                results,
                keyStatistics,
                pagination,
                queryForm.getSortField(),
                competitionId,
                competitionResource.getName(),
                selectAllDisabled,
                queryForm.isEoi());
    }
}
