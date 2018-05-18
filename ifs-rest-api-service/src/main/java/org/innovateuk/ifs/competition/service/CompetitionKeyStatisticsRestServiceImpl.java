package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.*;
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
public class CompetitionKeyStatisticsRestServiceImpl extends BaseRestService implements CompetitionKeyStatisticsRestService {

    private static final String COMPETITION_KEY_STATISTICS_REST_URL = "/competition-statistics";

    @Override
    public RestResult<CompetitionReadyToOpenKeyStatisticsResource> getReadyToOpenKeyStatisticsByCompetition(long competitionId) {
        return getWithRestResult(format("%s/%s/%s", COMPETITION_KEY_STATISTICS_REST_URL, competitionId, "ready-to-open"), CompetitionReadyToOpenKeyStatisticsResource.class);

    }

    @Override
    public RestResult<CompetitionOpenKeyStatisticsResource> getOpenKeyStatisticsByCompetition(long competitionId) {
        return getWithRestResult(format("%s/%s/%s", COMPETITION_KEY_STATISTICS_REST_URL, competitionId, "open"), CompetitionOpenKeyStatisticsResource.class);

    }

    @Override
    public RestResult<CompetitionClosedKeyStatisticsResource> getClosedKeyStatisticsByCompetition(long competitionId) {
        return getWithRestResult(format("%s/%s/%s", COMPETITION_KEY_STATISTICS_REST_URL, competitionId, "closed"), CompetitionClosedKeyStatisticsResource.class);

    }

    @Override
    public RestResult<CompetitionInAssessmentKeyStatisticsResource> getInAssessmentKeyStatisticsByCompetition(long competitionId) {
        return getWithRestResult(format("%s/%s/%s", COMPETITION_KEY_STATISTICS_REST_URL, competitionId, "in-assessment"), CompetitionInAssessmentKeyStatisticsResource.class);
    }

    @Override
    public RestResult<CompetitionFundedKeyStatisticsResource> getFundedKeyStatisticsByCompetition(long competitionId) {
        return getWithRestResult(format("%s/%s/%s", COMPETITION_KEY_STATISTICS_REST_URL, competitionId,"funded"), CompetitionFundedKeyStatisticsResource.class);
    }

    @Override
    public RestResult<ReviewKeyStatisticsResource> getReviewKeyStatisticsByCompetition(long competitionId) {
        return getWithRestResult(format("%s/%s/%s", COMPETITION_KEY_STATISTICS_REST_URL, competitionId, "review"), ReviewKeyStatisticsResource.class);
    }

    @Override
    public RestResult<ReviewInviteStatisticsResource> getReviewInviteStatisticsByCompetition(long competitionId) {
        return getWithRestResult(format("%s/%s/%s", COMPETITION_KEY_STATISTICS_REST_URL, competitionId, "review-invites"), ReviewInviteStatisticsResource.class);
    }

    @Override
    public RestResult<InterviewAssignmentKeyStatisticsResource> getInterviewAssignmentStatisticsByCompetition(long competitionId) {
        return getWithRestResult(format("%s/%s/%s", COMPETITION_KEY_STATISTICS_REST_URL, competitionId, "interview-assignment"), InterviewAssignmentKeyStatisticsResource.class);
    }

    @Override
    public RestResult<InterviewInviteStatisticsResource> getInterviewInviteStatisticsByCompetition(long competitionId) {
        return getWithRestResult(format("%s/%s/%s", COMPETITION_KEY_STATISTICS_REST_URL, competitionId, "interview-invites"), InterviewInviteStatisticsResource.class);
    }

    @Override
    public RestResult<InterviewStatisticsResource> getInterviewStatisticsByCompetition(long competitionId) {
        return getWithRestResult(format("%s/%s/%s", COMPETITION_KEY_STATISTICS_REST_URL, competitionId, "interview"), InterviewStatisticsResource.class);
    }
}