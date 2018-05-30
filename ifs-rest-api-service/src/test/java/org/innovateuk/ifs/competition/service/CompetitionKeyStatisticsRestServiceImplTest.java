package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentKeyStatisticsResource;
import org.innovateuk.ifs.interview.resource.InterviewInviteStatisticsResource;
import org.innovateuk.ifs.interview.resource.InterviewStatisticsResource;
import org.innovateuk.ifs.review.resource.ReviewInviteStatisticsResource;
import org.innovateuk.ifs.review.resource.ReviewKeyStatisticsResource;
import org.junit.Test;

import static java.lang.String.format;
import static org.innovateuk.ifs.competition.builder.CompetitionClosedKeyStatisticsResourceBuilder.newCompetitionClosedKeyStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionFundedKeyStatisticsResourceBuilder.newCompetitionFundedKeyStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionInAssessmentKeyStatisticsResourceBuilder.newCompetitionInAssessmentKeyStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionOpenKeyStatisticsResourceBuilder.newCompetitionOpenKeyStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionReadyToOpenKeyStatisticsResourceBuilder.newCompetitionReadyToOpenKeyStatisticsResource;
import static org.innovateuk.ifs.interview.builder.InterviewAssignmentKeyStatisticsResourceBuilder.newInterviewAssignmentKeyStatisticsResource;
import static org.innovateuk.ifs.interview.builder.InterviewInviteStatisticsResourceBuilder.newInterviewInviteStatisticsResource;
import static org.innovateuk.ifs.interview.builder.InterviewStatisticsResourceBuilder.newInterviewStatisticsResource;
import static org.innovateuk.ifs.review.builder.ReviewInviteStatisticsResourceBuilder.newReviewInviteStatisticsResource;
import static org.innovateuk.ifs.review.builder.ReviewKeyStatisticsResourceBuilder.newReviewKeyStatisticsResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class CompetitionKeyStatisticsRestServiceImplTest extends BaseRestServiceUnitTest<CompetitionKeyStatisticsRestServiceImpl> {

    private static final String COMPETITION_KEY_STATISTICS_REST_URL = "/competition-statistics";

    @Override
    protected CompetitionKeyStatisticsRestServiceImpl registerRestServiceUnderTest() {
        return new CompetitionKeyStatisticsRestServiceImpl();
    }

    @Test
    public void getReadyToOpenKeyStatisticsByCompetition() {
        CompetitionReadyToOpenKeyStatisticsResource expected = newCompetitionReadyToOpenKeyStatisticsResource().build();
        long competitionId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s/ready-to-open", COMPETITION_KEY_STATISTICS_REST_URL, competitionId), CompetitionReadyToOpenKeyStatisticsResource.class, expected);
        assertSame(expected, service.getReadyToOpenKeyStatisticsByCompetition(competitionId).getSuccess());
    }

    @Test
    public void getOpenKeyStatisticsByCompetition() {
        CompetitionOpenKeyStatisticsResource expected = newCompetitionOpenKeyStatisticsResource().build();
        long competitionId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s/open", COMPETITION_KEY_STATISTICS_REST_URL, competitionId), CompetitionOpenKeyStatisticsResource.class, expected);
        assertSame(expected, service.getOpenKeyStatisticsByCompetition(competitionId).getSuccess());
    }

    @Test
    public void getClosedKeyStatisticsByCompetition() {
        CompetitionClosedKeyStatisticsResource expected = newCompetitionClosedKeyStatisticsResource().build();
        long competitionId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s/closed", COMPETITION_KEY_STATISTICS_REST_URL, competitionId), CompetitionClosedKeyStatisticsResource.class, expected);
        assertSame(expected, service.getClosedKeyStatisticsByCompetition(competitionId).getSuccess());
    }

    @Test
    public void getInAssessmentKeyStatisticsByCompetition() {
        CompetitionInAssessmentKeyStatisticsResource expected = newCompetitionInAssessmentKeyStatisticsResource().build();
        long competitionId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s/in-assessment", COMPETITION_KEY_STATISTICS_REST_URL, competitionId), CompetitionInAssessmentKeyStatisticsResource.class, expected);
        assertSame(expected, service.getInAssessmentKeyStatisticsByCompetition(competitionId).getSuccess());
    }

    @Test
    public void getFundedKeyStatisticsByCompetition() {
        CompetitionFundedKeyStatisticsResource expected = newCompetitionFundedKeyStatisticsResource().build();
        long competitionId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s/funded", COMPETITION_KEY_STATISTICS_REST_URL, competitionId), CompetitionFundedKeyStatisticsResource.class, expected);
        assertSame(expected, service.getFundedKeyStatisticsByCompetition(competitionId).getSuccess());
    }

    @Test
    public void getReviewKeyStatisticsByCompetition() {
        ReviewKeyStatisticsResource expected = newReviewKeyStatisticsResource().build();
        long competitionId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s/%s", COMPETITION_KEY_STATISTICS_REST_URL, competitionId, "review"), ReviewKeyStatisticsResource.class, expected);
        assertSame(expected, service.getReviewKeyStatisticsByCompetition(competitionId).getSuccess());
    }

    @Test
    public void getReviewInviteStatisticsByCompetition() {
        ReviewInviteStatisticsResource expected = newReviewInviteStatisticsResource().build();
        long competitionId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s/%s", COMPETITION_KEY_STATISTICS_REST_URL, competitionId, "review-invites"), ReviewInviteStatisticsResource.class, expected);
        assertSame(expected, service.getReviewInviteStatisticsByCompetition(competitionId).getSuccess());
    }

    @Test
    public void getInterviewAssignmentStatisticsByCompetition() {
        long competitionId = 1L;
        InterviewAssignmentKeyStatisticsResource expected = newInterviewAssignmentKeyStatisticsResource().build();

        setupGetWithRestResultExpectations(format("%s/%s/%s", COMPETITION_KEY_STATISTICS_REST_URL, competitionId, "interview-assignment"), InterviewAssignmentKeyStatisticsResource.class, expected);

        InterviewAssignmentKeyStatisticsResource actual = service.getInterviewAssignmentStatisticsByCompetition(competitionId).getSuccess();
        assertEquals(expected, actual);
    }

    @Test
    public void getInterviewInviteStatisticsByCompetition() {
        InterviewInviteStatisticsResource expected = newInterviewInviteStatisticsResource().build();
        long competitionId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s/%s", COMPETITION_KEY_STATISTICS_REST_URL, competitionId, "interview-invites"), InterviewInviteStatisticsResource.class, expected);
        assertSame(expected, service.getInterviewInviteStatisticsByCompetition(competitionId).getSuccess());
    }

    @Test
    public void getInterviewStatisticsByCompetition() {
        InterviewStatisticsResource expected = newInterviewStatisticsResource().build();
        long competitionId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s/%s", COMPETITION_KEY_STATISTICS_REST_URL, competitionId, "interview"), InterviewStatisticsResource.class, expected);
        assertSame(expected, service.getInterviewStatisticsByCompetition(competitionId).getSuccess());
    }
}