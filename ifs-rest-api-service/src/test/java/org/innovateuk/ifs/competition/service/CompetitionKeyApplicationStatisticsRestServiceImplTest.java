package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.competition.resource.CompetitionClosedKeyApplicationStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionFundedKeyApplicationStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionOpenKeyApplicationStatisticsResource;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentKeyStatisticsResource;
import org.innovateuk.ifs.interview.resource.InterviewInviteStatisticsResource;
import org.innovateuk.ifs.interview.resource.InterviewStatisticsResource;
import org.innovateuk.ifs.review.resource.ReviewInviteStatisticsResource;
import org.innovateuk.ifs.review.resource.ReviewKeyStatisticsResource;
import org.junit.Test;

import static java.lang.String.format;
import static org.innovateuk.ifs.competition.builder.CompetitionClosedKeyApplicationStatisticsResourceBuilder.newCompetitionClosedKeyApplicationStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionFundedKeyApplicationStatisticsResourceBuilder.newCompetitionFundedKeyApplicationStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionOpenKeyApplicationStatisticsResourceBuilder.newCompetitionOpenKeyApplicationStatisticsResource;
import static org.innovateuk.ifs.interview.builder.InterviewAssignmentKeyStatisticsResourceBuilder.newInterviewAssignmentKeyStatisticsResource;
import static org.innovateuk.ifs.interview.builder.InterviewInviteStatisticsResourceBuilder.newInterviewInviteStatisticsResource;

import static org.innovateuk.ifs.interview.builder.InterviewStatisticsResourceBuilder.newInterviewStatisticsResource;
import static org.innovateuk.ifs.review.builder.ReviewInviteStatisticsResourceBuilder.newReviewInviteStatisticsResource;
import static org.innovateuk.ifs.review.builder.ReviewKeyStatisticsResourceBuilder.newReviewKeyStatisticsResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class CompetitionKeyApplicationStatisticsRestServiceImplTest extends
        BaseRestServiceUnitTest<CompetitionKeyApplicationStatisticsRestServiceImpl> {

    private static final String COMPETITION_APPLICATION_KEY_STATISTICS_REST_URL = "/competition-application-statistics";

    @Override
    protected CompetitionKeyApplicationStatisticsRestServiceImpl registerRestServiceUnderTest() {
        return new CompetitionKeyApplicationStatisticsRestServiceImpl();
    }


    @Test
    public void getOpenKeyStatisticsByCompetition() {
        CompetitionOpenKeyApplicationStatisticsResource expected = newCompetitionOpenKeyApplicationStatisticsResource()
                .build();
        long competitionId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s/open", COMPETITION_APPLICATION_KEY_STATISTICS_REST_URL,
                competitionId), CompetitionOpenKeyApplicationStatisticsResource.class, expected);
        assertSame(expected, service.getOpenKeyStatisticsByCompetition(competitionId).getSuccess());
    }

    @Test
    public void getClosedKeyStatisticsByCompetition() {
        CompetitionClosedKeyApplicationStatisticsResource expected =
                newCompetitionClosedKeyApplicationStatisticsResource().build();
        long competitionId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s/closed", COMPETITION_APPLICATION_KEY_STATISTICS_REST_URL,
                competitionId), CompetitionClosedKeyApplicationStatisticsResource.class, expected);
        assertSame(expected, service.getClosedKeyStatisticsByCompetition(competitionId).getSuccess());
    }


    @Test
    public void getFundedKeyStatisticsByCompetition() {
        CompetitionFundedKeyApplicationStatisticsResource expected =
                newCompetitionFundedKeyApplicationStatisticsResource().build();
        long competitionId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s/funded", COMPETITION_APPLICATION_KEY_STATISTICS_REST_URL,
                competitionId)
                , CompetitionFundedKeyApplicationStatisticsResource.class, expected);
        assertSame(expected, service.getFundedKeyStatisticsByCompetition(competitionId).getSuccess());
    }

    @Test
    public void getReviewKeyStatisticsByCompetition() {
        ReviewKeyStatisticsResource expected = newReviewKeyStatisticsResource().build();
        long competitionId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s/%s", COMPETITION_APPLICATION_KEY_STATISTICS_REST_URL,
                competitionId, "review"), ReviewKeyStatisticsResource.class, expected);
        assertSame(expected, service.getReviewKeyStatisticsByCompetition(competitionId).getSuccess());
    }

    @Test
    public void getReviewInviteStatisticsByCompetition() {
        ReviewInviteStatisticsResource expected = newReviewInviteStatisticsResource().build();
        long competitionId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s/%s", COMPETITION_APPLICATION_KEY_STATISTICS_REST_URL,
                competitionId, "review-invites"), ReviewInviteStatisticsResource.class, expected);
        assertSame(expected, service.getReviewInviteStatisticsByCompetition(competitionId).getSuccess());
    }

    @Test
    public void getInterviewAssignmentStatisticsByCompetition() {
        long competitionId = 1L;
        InterviewAssignmentKeyStatisticsResource expected = newInterviewAssignmentKeyStatisticsResource().build();

        setupGetWithRestResultExpectations(format("%s/%s/%s", COMPETITION_APPLICATION_KEY_STATISTICS_REST_URL,
                competitionId, "interview-assignment"), InterviewAssignmentKeyStatisticsResource.class, expected);

        InterviewAssignmentKeyStatisticsResource actual = service.getInterviewAssignmentStatisticsByCompetition
                (competitionId).getSuccess();
        assertEquals(expected, actual);
    }

    @Test
    public void getInterviewInviteStatisticsByCompetition() {
        InterviewInviteStatisticsResource expected = newInterviewInviteStatisticsResource().build();
        long competitionId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s/%s", COMPETITION_APPLICATION_KEY_STATISTICS_REST_URL,
                competitionId, "interview-invites"), InterviewInviteStatisticsResource.class, expected);
        assertSame(expected, service.getInterviewInviteStatisticsByCompetition(competitionId).getSuccess());
    }

    @Test
    public void getInterviewStatisticsByCompetition() {
        InterviewStatisticsResource expected = newInterviewStatisticsResource().build();
        long competitionId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s/%s", COMPETITION_APPLICATION_KEY_STATISTICS_REST_URL,
                competitionId, "interview"), InterviewStatisticsResource.class, expected);
        assertSame(expected, service.getInterviewStatisticsByCompetition(competitionId).getSuccess());
    }
}