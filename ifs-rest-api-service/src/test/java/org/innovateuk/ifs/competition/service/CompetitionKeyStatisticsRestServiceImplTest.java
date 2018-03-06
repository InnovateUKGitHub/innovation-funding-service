package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.review.resource.ReviewInviteStatisticsResource;
import org.innovateuk.ifs.review.resource.ReviewKeyStatisticsResource;
import org.junit.Test;

import static java.lang.String.format;
import static org.innovateuk.ifs.competition.builder.CompetitionClosedKeyStatisticsResourceBuilder.newCompetitionClosedKeyStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionFundedKeyStatisticsResourceBuilder.newCompetitionFundedKeyStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionInAssessmentKeyStatisticsResourceBuilder.newCompetitionInAssessmentKeyStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionOpenKeyStatisticsResourceBuilder.newCompetitionOpenKeyStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionReadyToOpenKeyStatisticsResourceBuilder.newCompetitionReadyToOpenKeyStatisticsResource;
import static org.innovateuk.ifs.review.builder.ReviewInviteStatisticsResourceBuilder.newReviewInviteStatisticsResource;
import static org.innovateuk.ifs.review.builder.ReviewKeyStatisticsResourceBuilder.newReviewKeyStatisticsResource;
import static org.junit.Assert.assertSame;

public class CompetitionKeyStatisticsRestServiceImplTest extends BaseRestServiceUnitTest<CompetitionKeyStatisticsRestServiceImpl> {


    private String competitionKeyStatisticsRestURL = "/competitionStatistics";

    @Override
    protected CompetitionKeyStatisticsRestServiceImpl registerRestServiceUnderTest() {
        return new CompetitionKeyStatisticsRestServiceImpl();
    }

    @Test
    public void getReadyToOpenKeyStatisticsByCompetition() {
        CompetitionReadyToOpenKeyStatisticsResource expected = newCompetitionReadyToOpenKeyStatisticsResource().build();
        long competitionId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s/readyToOpen", competitionKeyStatisticsRestURL, competitionId), CompetitionReadyToOpenKeyStatisticsResource.class, expected);
        assertSame(expected, service.getReadyToOpenKeyStatisticsByCompetition(competitionId).getSuccess());

    }

    @Test
    public void getOpenKeyStatisticsByCompetition() {
        CompetitionOpenKeyStatisticsResource expected = newCompetitionOpenKeyStatisticsResource().build();
        long competitionId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s/open", competitionKeyStatisticsRestURL, competitionId), CompetitionOpenKeyStatisticsResource.class, expected);
        assertSame(expected, service.getOpenKeyStatisticsByCompetition(competitionId).getSuccess());
    }

    @Test
    public void getClosedKeyStatisticsByCompetition() {
        CompetitionClosedKeyStatisticsResource expected = newCompetitionClosedKeyStatisticsResource().build();
        long competitionId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s/closed", competitionKeyStatisticsRestURL, competitionId), CompetitionClosedKeyStatisticsResource.class, expected);
        assertSame(expected, service.getClosedKeyStatisticsByCompetition(competitionId).getSuccess());
    }

    @Test
    public void getInAssessmentKeyStatisticsByCompetition() {
        CompetitionInAssessmentKeyStatisticsResource expected = newCompetitionInAssessmentKeyStatisticsResource().build();
        long competitionId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s/inAssessment", competitionKeyStatisticsRestURL, competitionId), CompetitionInAssessmentKeyStatisticsResource.class, expected);
        assertSame(expected, service.getInAssessmentKeyStatisticsByCompetition(competitionId).getSuccess());
    }

    @Test
    public void getFundedKeyStatisticsByCompetition() {
        CompetitionFundedKeyStatisticsResource expected = newCompetitionFundedKeyStatisticsResource().build();
        long competitionId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s/funded", competitionKeyStatisticsRestURL, competitionId), CompetitionFundedKeyStatisticsResource.class, expected);
        assertSame(expected, service.getFundedKeyStatisticsByCompetition(competitionId).getSuccess());
    }

    @Test
    public void getAssessmentPanelKeyStatisticsByCompetition() {
        ReviewKeyStatisticsResource expected = newReviewKeyStatisticsResource().build();
        long competitionId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s/%s", competitionKeyStatisticsRestURL, competitionId, "panel"), ReviewKeyStatisticsResource.class, expected);
        assertSame(expected, service.getAssessmentPanelKeyStatisticsByCompetition(competitionId).getSuccess());
    }

    @Test
    public void getAssessmentPanelInviteStatisticsByCompetition() {
        ReviewInviteStatisticsResource expected = newReviewInviteStatisticsResource().build();
        long competitionId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s/%s", competitionKeyStatisticsRestURL, competitionId, "panelInvites"), ReviewInviteStatisticsResource.class, expected);
        assertSame(expected, service.getAssessmentPanelInviteStatisticsByCompetition(competitionId).getSuccess());
    }
}
