package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.review.resource.ReviewInviteStatisticsResource;
import org.innovateuk.ifs.review.resource.ReviewKeyStatisticsResource;

/**
 * Interface for retrieving key statistics about competitions
 */
public interface CompetitionKeyStatisticsRestService {
    RestResult<CompetitionReadyToOpenKeyStatisticsResource> getReadyToOpenKeyStatisticsByCompetition(long competitionId);
    RestResult<CompetitionOpenKeyStatisticsResource> getOpenKeyStatisticsByCompetition(long competitionId);
    RestResult<CompetitionClosedKeyStatisticsResource> getClosedKeyStatisticsByCompetition(long competitionId);
    RestResult<CompetitionInAssessmentKeyStatisticsResource> getInAssessmentKeyStatisticsByCompetition(long competitionId);
    RestResult<CompetitionFundedKeyStatisticsResource> getFundedKeyStatisticsByCompetition(long competitionId);
    RestResult<ReviewKeyStatisticsResource> getAssessmentPanelKeyStatisticsByCompetition(long competitionId);
    RestResult<ReviewInviteStatisticsResource> getAssessmentPanelInviteStatisticsByCompetition(long competitionId);
}
