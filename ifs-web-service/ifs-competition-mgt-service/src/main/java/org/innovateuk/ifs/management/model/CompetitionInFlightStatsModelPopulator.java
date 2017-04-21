package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionKeyStatisticsRestService;
import org.innovateuk.ifs.management.viewmodel.CompetitionInFlightStatsViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Competition Management Competition in flight stats dashboard.
 */
@Component
public class CompetitionInFlightStatsModelPopulator {

    @Autowired
    private CompetitionKeyStatisticsRestService competitionKeyStatisticsRestService;

    @Autowired
    private CompetitionService competitionService;

    public CompetitionInFlightStatsViewModel populateStatsViewModel(Long competitionId) {
        return populateStatsViewModel(competitionService.getById(competitionId));
    }

    public CompetitionInFlightStatsViewModel populateStatsViewModel(CompetitionResource competitionResource) {
        CompetitionStatus status = competitionResource.getCompetitionStatus();
        switch (competitionResource.getCompetitionStatus()) {
            case READY_TO_OPEN:
                return new CompetitionInFlightStatsViewModel(competitionKeyStatisticsRestService.getReadyToOpenKeyStatisticsByCompetition(competitionResource.getId()).getSuccessObjectOrThrowException(), status);
            case OPEN:
                return new CompetitionInFlightStatsViewModel(competitionKeyStatisticsRestService.getOpenKeyStatisticsByCompetition(competitionResource.getId()).getSuccessObjectOrThrowException(), status);
            case CLOSED:
                return new CompetitionInFlightStatsViewModel(competitionKeyStatisticsRestService.getClosedKeyStatisticsByCompetition(competitionResource.getId()).getSuccessObjectOrThrowException(), status);
            case IN_ASSESSMENT:
                return new CompetitionInFlightStatsViewModel(competitionKeyStatisticsRestService.getInAssessmentKeyStatisticsByCompetition(competitionResource.getId()).getSuccessObjectOrThrowException(), status);
            case FUNDERS_PANEL:
                return new CompetitionInFlightStatsViewModel(competitionKeyStatisticsRestService.getFundedKeyStatisticsByCompetition(competitionResource.getId()).getSuccessObjectOrThrowException(), status);
            case ASSESSOR_FEEDBACK:
                return new CompetitionInFlightStatsViewModel(competitionKeyStatisticsRestService.getFundedKeyStatisticsByCompetition(competitionResource.getId()).getSuccessObjectOrThrowException(), status);
        }
        return new CompetitionInFlightStatsViewModel();
    }
}
