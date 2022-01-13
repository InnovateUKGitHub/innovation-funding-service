package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.CompetitionClosedKeyApplicationStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionFundedKeyApplicationStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionOpenKeyApplicationStatisticsResource;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentKeyStatisticsResource;
import org.innovateuk.ifs.interview.resource.InterviewInviteStatisticsResource;
import org.innovateuk.ifs.interview.resource.InterviewStatisticsResource;
import org.innovateuk.ifs.review.resource.ReviewInviteStatisticsResource;
import org.innovateuk.ifs.review.resource.ReviewKeyStatisticsResource;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

/**
 * Interface for retrieving Competition Key Statistics Resources}
 */
@Service
public class CompetitionKeyApplicationStatisticsRestServiceImpl extends BaseRestService implements
        CompetitionKeyApplicationStatisticsRestService {

    private static final String COMPETITION_APPLICATION_KEY_STATISTICS_REST_URL = "/competition-application-statistics";

    @Override
    public RestResult<CompetitionOpenKeyApplicationStatisticsResource> getOpenKeyStatisticsByCompetition(
            long competitionId) {
        return getWithRestResult(format("%s/%s/%s", COMPETITION_APPLICATION_KEY_STATISTICS_REST_URL, competitionId,
                "open"), CompetitionOpenKeyApplicationStatisticsResource.class);
    }

    @Override
    public RestResult<CompetitionClosedKeyApplicationStatisticsResource> getClosedKeyStatisticsByCompetition(
            long competitionId) {
        return getWithRestResult(format("%s/%s/%s", COMPETITION_APPLICATION_KEY_STATISTICS_REST_URL, competitionId,
                "closed"), CompetitionClosedKeyApplicationStatisticsResource.class);
    }

    @Override
    public RestResult<CompetitionFundedKeyApplicationStatisticsResource> getFundedKeyStatisticsByCompetition(
            long competitionId) {
        return getWithRestResult(format("%s/%s/%s", COMPETITION_APPLICATION_KEY_STATISTICS_REST_URL, competitionId,
                "funded"), CompetitionFundedKeyApplicationStatisticsResource.class);
    }

    @Override
    public RestResult<ReviewKeyStatisticsResource> getReviewKeyStatisticsByCompetition(long competitionId) {
        return getWithRestResult(format("%s/%s/%s", COMPETITION_APPLICATION_KEY_STATISTICS_REST_URL, competitionId,
                "review"), ReviewKeyStatisticsResource.class);
    }

    @Override
    public RestResult<ReviewInviteStatisticsResource> getReviewInviteStatisticsByCompetition(long competitionId) {
        return getWithRestResult(format("%s/%s/%s", COMPETITION_APPLICATION_KEY_STATISTICS_REST_URL, competitionId,
                "review-invites"), ReviewInviteStatisticsResource.class);
    }

    @Override
    public RestResult<InterviewAssignmentKeyStatisticsResource> getInterviewAssignmentStatisticsByCompetition(
            long competitionId) {
        return getWithRestResult(format("%s/%s/%s", COMPETITION_APPLICATION_KEY_STATISTICS_REST_URL, competitionId,
                "interview-assignment"), InterviewAssignmentKeyStatisticsResource.class);
    }

    @Override
    public RestResult<InterviewInviteStatisticsResource> getInterviewInviteStatisticsByCompetition(long competitionId) {
        return getWithRestResult(format("%s/%s/%s", COMPETITION_APPLICATION_KEY_STATISTICS_REST_URL, competitionId,
                "interview-invites"), InterviewInviteStatisticsResource.class);
    }

    @Override
    public RestResult<InterviewStatisticsResource> getInterviewStatisticsByCompetition(long competitionId) {
        return getWithRestResult(format("%s/%s/%s", COMPETITION_APPLICATION_KEY_STATISTICS_REST_URL, competitionId,
                "interview"), InterviewStatisticsResource.class);
    }
}