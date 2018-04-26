package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentKeyStatisticsResource;
import org.innovateuk.ifs.interview.resource.InterviewInviteStatisticsResource;
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
    RestResult<ReviewKeyStatisticsResource> getReviewKeyStatisticsByCompetition(long competitionId);
    RestResult<ReviewInviteStatisticsResource> getReviewInviteStatisticsByCompetition(long competitionId);
    RestResult<InterviewAssignmentKeyStatisticsResource> getInterviewKeyStatisticsByCompetition(long competitionId);
    RestResult<InterviewInviteStatisticsResource> getInterviewInviteStatisticsByCompetition(long competitionId);
}