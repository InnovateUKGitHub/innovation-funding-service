package org.innovateuk.ifs.management.competition.inflight.populator;

import org.innovateuk.ifs.assessment.service.CompetitionKeyAssessmentStatisticsRestService;
import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionKeyApplicationStatisticsRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.competition.inflight.viewmodel.CompetitionInFlightStatsViewModel;
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
    private CompetitionRestService competitionRestService;

    public CompetitionInFlightStatsViewModel populateStatsViewModel(Long competitionId) {
        CompetitionResource competititon = competitionRestService.getCompetitionById(competitionId).getSuccess();
        return populateStatsViewModel(competititon);
    }

    public CompetitionInFlightStatsViewModel populateStatsViewModel(CompetitionResource competitionResource) {
        CompetitionStatus status = competitionResource.getCompetitionStatus();
        CompetitionCompletionStage completionStage = competitionResource.getCompletionStage();
        switch (competitionResource.getCompetitionStatus()) {
            case READY_TO_OPEN:
                return new CompetitionInFlightStatsViewModel(
                        competitionKeyAssessmentStatisticsRestService.getReadyToOpenKeyStatisticsByCompetition(
                                competitionResource.getId()).getSuccess(),
                        status,
                        completionStage);
            case OPEN:
                if (competitionResource.isHasAssessmentStage()) {
                    return new CompetitionInFlightStatsViewModel(
                            competitionKeyApplicationStatisticsRestService.getOpenKeyStatisticsByCompetition(
                                    competitionResource.getId()).getSuccess(),
                            competitionKeyAssessmentStatisticsRestService.getOpenKeyStatisticsByCompetition(
                                    competitionResource.getId()).getSuccess(),
                            status,
                            completionStage);

                } else {
                    return new CompetitionInFlightStatsViewModel(
                            competitionKeyApplicationStatisticsRestService.getOpenKeyStatisticsByCompetition(
                                    competitionResource.getId()).getSuccess(),
                            competitionKeyAssessmentStatisticsRestService.getOpenKeyStatisticsByCompetition(
                                    competitionResource.getId()).getSuccess(),
                            status,
                            competitionKeyApplicationStatisticsRestService.getFundedKeyStatisticsByCompetition(
                                    competitionResource.getId()).getSuccess(),
                            completionStage);
                }
            case CLOSED:
                return new CompetitionInFlightStatsViewModel(
                        competitionKeyApplicationStatisticsRestService.getClosedKeyStatisticsByCompetition(
                                competitionResource.getId()).getSuccess(),
                        competitionKeyAssessmentStatisticsRestService
                                .getClosedKeyStatisticsByCompetition(competitionResource.getId()).getSuccess(),
                        status,
                        completionStage);
            case IN_ASSESSMENT:
                return new CompetitionInFlightStatsViewModel(
                        competitionKeyAssessmentStatisticsRestService.getInAssessmentKeyStatisticsByCompetition(
                                competitionResource.getId()).getSuccess(),
                        status,
                        completionStage);
            case FUNDERS_PANEL:
                return new CompetitionInFlightStatsViewModel(
                        competitionKeyApplicationStatisticsRestService.getFundedKeyStatisticsByCompetition(
                                competitionResource.getId()).getSuccess(),
                        status,
                        completionStage);
            case ASSESSOR_FEEDBACK:
                return new CompetitionInFlightStatsViewModel(
                        competitionKeyApplicationStatisticsRestService.getFundedKeyStatisticsByCompetition(
                                competitionResource.getId()).getSuccess(), status,
                        completionStage);
        }
        return new CompetitionInFlightStatsViewModel();
    }
}
