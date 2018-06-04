package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.assessment.resource.CompetitionClosedKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionInAssessmentKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionOpenKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionReadyToOpenKeyAssessmentStatisticsResource;
import org.junit.Test;

import static java.lang.String.format;
import static org.innovateuk.ifs.assessment.builder.CompetitionClosedKeyAssessmentStatisticsResourceBuilder.newCompetitionClosedKeyAssessmentStatisticsResource;
import static org.innovateuk.ifs.assessment.builder.CompetitionInAssessmentKeyAssessmentStatisticsResourceBuilder.newCompetitionInAssessmentKeyAssessmentStatisticsResource;
import static org.innovateuk.ifs.assessment.builder.CompetitionOpenKeyAssessmentStatisticsResourceBuilder.newCompetitionOpenKeyAssessmentStatisticsResource;
import static org.innovateuk.ifs.assessment.builder.CompetitionReadyToOpenKeyAssessmentStatisticsResourceBuilder.newCompetitionReadyToOpenKeyAssessmentStatisticsResource;
import static org.junit.Assert.assertSame;

public class CompetitionKeyAssessmentStatisticsRestServiceImplTest extends
        BaseRestServiceUnitTest<CompetitionKeyAssessmentStatisticsRestServiceImpl> {

    private static final String COMPETITION_ASSESSMENT_KEY_STATISTICS_REST_URL = "/competition-assessment-statistics";

    @Override
    protected CompetitionKeyAssessmentStatisticsRestServiceImpl registerRestServiceUnderTest() {
        return new CompetitionKeyAssessmentStatisticsRestServiceImpl();
    }

    @Test
    public void getReadyToOpenKeyStatisticsByCompetition() {
        CompetitionReadyToOpenKeyAssessmentStatisticsResource expected =
                newCompetitionReadyToOpenKeyAssessmentStatisticsResource().build();
        long competitionId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s/ready-to-open",
                COMPETITION_ASSESSMENT_KEY_STATISTICS_REST_URL, competitionId),
                CompetitionReadyToOpenKeyAssessmentStatisticsResource.class, expected);
        assertSame(expected, service.getReadyToOpenKeyStatisticsByCompetition(competitionId).getSuccess());
    }

    @Test
    public void getOpenKeyStatisticsByCompetition() {
        CompetitionOpenKeyAssessmentStatisticsResource expected = newCompetitionOpenKeyAssessmentStatisticsResource()
                .build();
        long competitionId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s/open", COMPETITION_ASSESSMENT_KEY_STATISTICS_REST_URL,
                competitionId), CompetitionOpenKeyAssessmentStatisticsResource.class, expected);
        assertSame(expected, service.getOpenKeyStatisticsByCompetition(competitionId).getSuccess());
    }

    @Test
    public void getClosedKeyStatisticsByCompetition() {
        CompetitionClosedKeyAssessmentStatisticsResource expected =
                newCompetitionClosedKeyAssessmentStatisticsResource().build();
        long competitionId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s/closed", COMPETITION_ASSESSMENT_KEY_STATISTICS_REST_URL,
                competitionId), CompetitionClosedKeyAssessmentStatisticsResource.class, expected);
        assertSame(expected, service.getClosedKeyStatisticsByCompetition(competitionId).getSuccess());
    }

    @Test
    public void getInAssessmentKeyStatisticsByCompetition() {
        CompetitionInAssessmentKeyAssessmentStatisticsResource expected =
                newCompetitionInAssessmentKeyAssessmentStatisticsResource().build();
        long competitionId = 1L;

        setupGetWithRestResultExpectations(format("%s/%s/in-assessment",
                COMPETITION_ASSESSMENT_KEY_STATISTICS_REST_URL, competitionId),
                CompetitionInAssessmentKeyAssessmentStatisticsResource.class, expected);
        assertSame(expected, service.getInAssessmentKeyStatisticsByCompetition(competitionId).getSuccess());
    }
}