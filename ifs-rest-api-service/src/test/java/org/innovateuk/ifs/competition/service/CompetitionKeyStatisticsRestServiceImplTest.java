package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.competition.resource.CompetitionClosedKeyStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionInAssessmentKeyStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionOpenKeyStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionReadyToOpenKeyStatisticsResource;
import org.junit.Test;

import static java.lang.String.format;
import static org.innovateuk.ifs.competition.builder.CompetitionClosedKeyStatisticsResourceBuilder.newCompetitionClosedKeyStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionInAssessmentKeyStatisticsResourceBuilder.newCompetitionInAssessmentKeyStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionOpenKeyStatisticsResourceBuilder.newCompetitionOpenKeyStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionReadyToOpenKeyStatisticsResourceBuilder.newCompetitionReadyToOpenKeyStatisticsResource;
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
        assertSame(expected, service.getReadyToOpenKeyStatisticsByCompetition(competitionId).getSuccessObject());

    }

    @Test
    public void getOpenKeyStatisticsByCompetition() {
        CompetitionOpenKeyStatisticsResource expected = newCompetitionOpenKeyStatisticsResource().build();
        long competitionId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s/open", competitionKeyStatisticsRestURL, competitionId), CompetitionOpenKeyStatisticsResource.class, expected);
        assertSame(expected, service.getOpenKeyStatisticsByCompetition(competitionId).getSuccessObject());
    }

    @Test
    public void getClosedKeyStatisticsByCompetition() {
        CompetitionClosedKeyStatisticsResource expected = newCompetitionClosedKeyStatisticsResource().build();
        long competitionId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s/closed", competitionKeyStatisticsRestURL, competitionId), CompetitionClosedKeyStatisticsResource.class, expected);
        assertSame(expected, service.getClosedKeyStatisticsByCompetition(competitionId).getSuccessObject());
    }

    @Test
    public void getInAssessmentKeyStatisticsByCompetition() {
        CompetitionInAssessmentKeyStatisticsResource expected = newCompetitionInAssessmentKeyStatisticsResource().build();
        long competitionId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s/inAssessment", competitionKeyStatisticsRestURL, competitionId), CompetitionInAssessmentKeyStatisticsResource.class, expected);
        assertSame(expected, service.getInAssessmentKeyStatisticsByCompetition(competitionId).getSuccessObject());
    }
}
