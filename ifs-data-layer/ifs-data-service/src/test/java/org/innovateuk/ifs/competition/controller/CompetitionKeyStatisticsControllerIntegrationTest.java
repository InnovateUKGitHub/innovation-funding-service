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
        assertEquals(0, keyStatisticsResource.getAssessorsAccepted());
        assertEquals(0, keyStatisticsResource.getAssessorsInvited());
    }

    @Test
    public void getOpenKeyStatistics() throws Exception {
        CompetitionOpenKeyStatisticsResource keyStatisticsResource = controller.getOpenKeyStatistics(1L).getSuccessObject();
        assertEquals(0, keyStatisticsResource.getAssessorsAccepted());
        assertEquals(0, keyStatisticsResource.getAssessorsInvited());
        assertEquals(2, keyStatisticsResource.getApplicationsStarted());
        assertEquals(0, keyStatisticsResource.getApplicationsPastHalf());
        assertEquals(5, keyStatisticsResource.getApplicationsSubmitted());
        assertEquals(0, keyStatisticsResource.getApplicationsPerAssessor());
    }

    @Test
    public void getClosedKeyStatistics() throws Exception {
        CompetitionClosedKeyStatisticsResource keyStatisticsResource = controller.getClosedKeyStatistics(1L).getSuccessObject();
        assertEquals(0, keyStatisticsResource.getAssessorsAccepted());
        assertEquals(0, keyStatisticsResource.getAssessorsInvited());
        assertEquals(0, keyStatisticsResource.getApplicationsRequiringAssessors());
        assertEquals(0, keyStatisticsResource.getAssessorsWithoutApplications());
        assertEquals(9, keyStatisticsResource.getAssignmentCount());
        assertEquals(0, keyStatisticsResource.getApplicationsPerAssessor());
    }

    @Test
    public void getInAssessmentKeyStatistics() throws Exception {

        CompetitionInAssessmentKeyStatisticsResource keyStatisticsResource = controller.getInAssessmentKeyStatistics(1L).getSuccessObject();
        assertEquals(4, keyStatisticsResource.getAssessmentsStarted());
        assertEquals(9, keyStatisticsResource.getAssignmentCount());
        assertEquals(2, keyStatisticsResource.getAssessmentsSubmitted());
        assertEquals(1, keyStatisticsResource.getAssignmentsAccepted());
        assertEquals(1, keyStatisticsResource.getAssignmentsWaiting());
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
