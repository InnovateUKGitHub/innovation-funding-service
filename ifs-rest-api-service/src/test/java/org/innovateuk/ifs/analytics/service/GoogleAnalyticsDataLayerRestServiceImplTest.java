package org.innovateuk.ifs.analytics.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.junit.Test;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;

public class GoogleAnalyticsDataLayerRestServiceImplTest extends BaseRestServiceUnitTest<GoogleAnalyticsDataLayerRestServiceImpl> {

    private static final String restUrl = "/analytics";

    @Override
    protected GoogleAnalyticsDataLayerRestServiceImpl registerRestServiceUnderTest() {
        return new GoogleAnalyticsDataLayerRestServiceImpl();
    }

    @Test
    public void getCompetitionNameForApplication() {
        long applicationId = 5L;
        String expected = "competition name";

        setupGetWithRestResultExpectations(
                format("%s/%s/%d/competition-name", restUrl, "application", applicationId),
                String.class,
                expected
        );

        String actual = service.getCompetitionNameForApplication(applicationId).getSuccessObject();

        assertEquals(expected, actual);
    }

    @Test
    public void getCompetitionName() {
        long competitionId = 7L;
        String expected = "competition name";

        setupGetWithRestResultAnonymousExpectations(
                format("%s/%s/%d/competition-name", restUrl, "competition", competitionId),
                String.class,
                expected
        );

        String actual = service.getCompetitionName(competitionId).getSuccessObject();

        assertEquals(expected, actual);
    }

    @Test
    public void getCompetitionNameForProject() {
        long projectId = 11L;
        String expected = "competition name";

        setupGetWithRestResultExpectations(
                format("%s/%s/%d/competition-name", restUrl, "project", projectId),
                String.class,
                expected
        );

        String actual = service.getCompetitionNameForProject(projectId).getSuccessObject();

        assertEquals(expected, actual);
    }

    @Test
    public void getCompetitionNameForAssessment() {
        long assessmentId = 13L;
        String expected = "competition name";

        setupGetWithRestResultExpectations(
                format("%s/%s/%d/competition-name", restUrl, "assessment", assessmentId),
                String.class,
                expected
        );

        String actual = service.getCompetitionNameForAssessment(assessmentId).getSuccessObject();

        assertEquals(expected, actual);
    }
}
