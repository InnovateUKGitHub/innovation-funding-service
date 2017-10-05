package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.assessment.panel.resource.AssessmentPanelInviteStatisticsResource;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentPanelKeyStatisticsResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.*;

/**
 * Interface for retrieving key statistics about competitions
 */
public interface CompetitionKeyStatisticsRestService {
    RestResult<CompetitionReadyToOpenKeyStatisticsResource> getReadyToOpenKeyStatisticsByCompetition(long competitionId);
    RestResult<CompetitionOpenKeyStatisticsResource> getOpenKeyStatisticsByCompetition(long competitionId);
    RestResult<CompetitionClosedKeyStatisticsResource> getClosedKeyStatisticsByCompetition(long competitionId);
    RestResult<CompetitionInAssessmentKeyStatisticsResource> getInAssessmentKeyStatisticsByCompetition(long competitionId);
    RestResult<CompetitionFundedKeyStatisticsResource> getFundedKeyStatisticsByCompetition(long competitionId);
    RestResult<AssessmentPanelKeyStatisticsResource> getAssessmentPanelKeyStatisticsByCompetition(long competitionId);
    RestResult<AssessmentPanelInviteStatisticsResource> getAssessmentPanelInviteStatisticsByCompetition(long competitionId);
}
