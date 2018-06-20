package org.innovateuk.ifs.management.competition.populator;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionKeyApplicationStatisticsRestService;
import org.innovateuk.ifs.assessment.service.CompetitionKeyAssessmentStatisticsRestService;
import org.innovateuk.ifs.management.competition.viewmodel.CompetitionInFlightStatsViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Competition Management Competition in flight stats dashboard.
 */
@Component
public class CompetitionInFlightStatsModelPopulator {

    @Autowired
    private CompetitionKeyApplicationStatisticsRestService competitionKeyApplicationStatisticsRestService;

    @Autowired
    private CompetitionKeyAssessmentStatisticsRestService competitionKeyAssessmentStatisticsRestService;

    @Autowired
    private CompetitionService competitionService;

    public CompetitionInFlightStatsViewModel populateStatsViewModel(Long competitionId) {
        return populateStatsViewModel(competitionService.getById(competitionId));
    }

    public CompetitionInFlightStatsViewModel populateStatsViewModel(CompetitionResource competitionResource) {
        CompetitionStatus status = competitionResource.getCompetitionStatus();
        switch (competitionResource.getCompetitionStatus()) {
            case READY_TO_OPEN:
                return new CompetitionInFlightStatsViewModel(
                        competitionKeyAssessmentStatisticsRestService.getReadyToOpenKeyStatisticsByCompetition(
                                competitionResource.getId()).getSuccess(),
                        status);
            case OPEN:
                return new CompetitionInFlightStatsViewModel(
                        competitionKeyApplicationStatisticsRestService.getOpenKeyStatisticsByCompetition(
                                competitionResource.getId()).getSuccess(),
                        competitionKeyAssessmentStatisticsRestService.getOpenKeyStatisticsByCompetition(
                                competitionResource.getId()).getSuccess(),
                        status);
            case CLOSED:
                return new CompetitionInFlightStatsViewModel(
                        competitionKeyApplicationStatisticsRestService.getClosedKeyStatisticsByCompetition(
                                competitionResource.getId()).getSuccess(),
                        competitionKeyAssessmentStatisticsRestService
                                .getClosedKeyStatisticsByCompetition(competitionResource.getId()).getSuccess(),
                        status);
            case IN_ASSESSMENT:
                return new CompetitionInFlightStatsViewModel(
                        competitionKeyAssessmentStatisticsRestService.getInAssessmentKeyStatisticsByCompetition(
                                competitionResource.getId()).getSuccess(),
                        status);
            case FUNDERS_PANEL:
                return new CompetitionInFlightStatsViewModel(
                        competitionKeyApplicationStatisticsRestService.getFundedKeyStatisticsByCompetition(
                                competitionResource.getId()).getSuccess(),
                        status);
            case ASSESSOR_FEEDBACK:
                return new CompetitionInFlightStatsViewModel(
                        competitionKeyApplicationStatisticsRestService.getFundedKeyStatisticsByCompetition(
                                competitionResource.getId()).getSuccess(), status);
        }
        return new CompetitionInFlightStatsViewModel();
    }
}
