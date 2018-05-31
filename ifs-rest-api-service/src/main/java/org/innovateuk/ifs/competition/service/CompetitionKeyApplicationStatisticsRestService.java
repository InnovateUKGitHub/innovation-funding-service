package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionClosedKeyApplicationStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionFundedKeyApplicationStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionOpenKeyApplicationStatisticsResource;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentKeyStatisticsResource;
import org.innovateuk.ifs.interview.resource.InterviewInviteStatisticsResource;
import org.innovateuk.ifs.review.resource.ReviewInviteStatisticsResource;
import org.innovateuk.ifs.review.resource.ReviewKeyStatisticsResource;

/**
 * Interface for retrieving key statistics about competitions
 */
public interface CompetitionKeyApplicationStatisticsRestService {

    RestResult<CompetitionOpenKeyApplicationStatisticsResource> getOpenKeyStatisticsByCompetition(long competitionId);

    RestResult<CompetitionClosedKeyApplicationStatisticsResource> getClosedKeyStatisticsByCompetition(long competitionId);

    RestResult<CompetitionFundedKeyApplicationStatisticsResource> getFundedKeyStatisticsByCompetition(long competitionId);

    RestResult<ReviewKeyStatisticsResource> getReviewKeyStatisticsByCompetition(long competitionId);

    RestResult<ReviewInviteStatisticsResource> getReviewInviteStatisticsByCompetition(long competitionId);

    RestResult<InterviewAssignmentKeyStatisticsResource> getInterviewKeyStatisticsByCompetition(long competitionId);

    RestResult<InterviewInviteStatisticsResource> getInterviewInviteStatisticsByCompetition(long competitionId);

}