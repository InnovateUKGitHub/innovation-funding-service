package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.assessment.resource.dashboard.AssessorCompetitionDashboardResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static java.lang.String.format;
import static org.innovateuk.ifs.assessment.builder.AssessorCompetitionDashboardResourceBuilder.newAssessorCompetitionDashboardResource;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class AssessorCompetitionDashboardRestServiceImplTest extends BaseRestServiceUnitTest<AssessorCompetitionDashboardRestServiceImpl> {

    @Override
    protected AssessorCompetitionDashboardRestServiceImpl registerRestServiceUnderTest() {
        return new AssessorCompetitionDashboardRestServiceImpl();
    }

    @Test
    public void getAssessorCompetitionDashboard() {
        long userId = 1L;
        long competitionId = 2L;
        String baseUrl = "/assessment/user/%d/competition/%d";

        AssessorCompetitionDashboardResource expected = newAssessorCompetitionDashboardResource()
                .withCompetitionId(competitionId)
                .build();

        setupGetWithRestResultExpectations(format(baseUrl + "/dashboard", userId, competitionId), AssessorCompetitionDashboardResource.class, expected);

        AssessorCompetitionDashboardResource actual = service.getAssessorCompetitionDashboard(competitionId, userId).getSuccess();
        assertEquals(expected, actual);
    }
}