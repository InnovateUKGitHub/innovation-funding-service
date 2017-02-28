package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.transactional.CompetitionKeyStatisticsService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;


public class CompetitionKeyStatisticsControllerIntegrationTest extends BaseControllerIntegrationTest<CompetitionKeyStatisticsController> {

    @Autowired
    @Override
    protected void setControllerUnderTest(CompetitionKeyStatisticsController controller) {
        this.controller = controller;
    }

    @Mock
    protected CompetitionKeyStatisticsService competitionKeyStatisticsService;


    @Before
    public void setup() {
        loginCompAdmin();
    }

    @Test
    public void getReadyToOpenKeyStatistics() throws Exception {
        CompetitionReadyToOpenKeyStatisticsResource keyStatisticsResource = controller.getReadyToOpenKeyStatistics(1L).getSuccessObject();
        assertEquals(0L, keyStatisticsResource.getAssessorsAccepted());
        assertEquals(0L, keyStatisticsResource.getAssessorsInvited());
    }

    @Test
    public void getOpenKeyStatistics() throws Exception {
        CompetitionOpenKeyStatisticsResource keyStatisticsResource = controller.getOpenKeyStatistics(1L).getSuccessObject();
        assertEquals(0L, keyStatisticsResource.getAssessorsAccepted());
        assertEquals(0L, keyStatisticsResource.getAssessorsInvited());
        assertEquals(1L, keyStatisticsResource.getApplicationsStarted());
        assertEquals(0L, keyStatisticsResource.getApplicationsPastHalf());
        assertEquals(5L, keyStatisticsResource.getApplicationsSubmitted());
        assertEquals(0L, keyStatisticsResource.getApplicationsPerAssessor());
    }

    @Test
    public void getClosedKeyStatistics() throws Exception {
        CompetitionClosedKeyStatisticsResource keyStatisticsResource = controller.getClosedKeyStatistics(1L).getSuccessObject();
        assertEquals(0L, keyStatisticsResource.getAssessorsAccepted());
        assertEquals(0L, keyStatisticsResource.getAssessorsInvited());
        assertEquals(0L, keyStatisticsResource.getApplicationsRequiringAssessors());
        assertEquals(0L, keyStatisticsResource.getAssessorsWithoutApplications());
        assertEquals(9L, keyStatisticsResource.getAssignmentCount());
        assertEquals(0L, keyStatisticsResource.getApplicationsPerAssessor());
    }

    @Test
    public void getInAssessmentKeyStatistics() throws Exception {

        CompetitionInAssessmentKeyStatisticsResource keyStatisticsResource = controller.getInAssessmentKeyStatistics(1L).getSuccessObject();
        assertEquals(4L, keyStatisticsResource.getAssessmentsStarted());
        assertEquals(9L, keyStatisticsResource.getAssignmentCount());
        assertEquals(2L, keyStatisticsResource.getAssessmentsSubmitted());
        assertEquals(1L, keyStatisticsResource.getAssignmentsAccepted());
        assertEquals(1L, keyStatisticsResource.getAssignmentsWaiting());
    }

    @Test
    public void getFundedKeyStatistics() throws Exception {

        CompetitionFundedKeyStatisticsResource keyStatisticsResource = controller.getFundedKeyStatistics(1L).getSuccessObject();

        assertEquals(5, keyStatisticsResource.getApplicationsSubmitted());
        assertEquals(0, keyStatisticsResource.getApplicationsFunded());
        assertEquals(0, keyStatisticsResource.getApplicationsNotFunded());
        assertEquals(0, keyStatisticsResource.getApplicationsOnHold());
        assertEquals(0, keyStatisticsResource.getApplicationsNotifiedOfDecision());
        assertEquals(0, keyStatisticsResource.getApplicationsAwaitingDecision());
    }
}
