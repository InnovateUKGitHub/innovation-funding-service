package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.assessment.resource.CompetitionClosedKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionInAssessmentKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionOpenKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionReadyToOpenKeyAssessmentStatisticsResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class CompetitionKeyAssessmentStatisticsControllerIntegrationTest extends
        BaseControllerIntegrationTest<CompetitionKeyAssessmentStatisticsController> {

    @Autowired
    @Override
    protected void setControllerUnderTest(CompetitionKeyAssessmentStatisticsController controller) {
        this.controller = controller;
    }

    @Before
    public void setup() {
        loginCompAdmin();
    }

    @Test
    public void getReadyToOpenKeyStatistics() {
        CompetitionReadyToOpenKeyAssessmentStatisticsResource keyStatisticsResource = controller
                .getReadyToOpenKeyStatistics(1L).getSuccess();

        assertEquals(0, keyStatisticsResource.getAssessorsAccepted());
        assertEquals(0, keyStatisticsResource.getAssessorsInvited());
    }

    @Test
    public void getOpenKeyStatistics() {
        CompetitionOpenKeyAssessmentStatisticsResource keyStatisticsResource = controller.getOpenKeyStatistics(1L)
                .getSuccess();

        assertEquals(0, keyStatisticsResource.getAssessorsAccepted());
        assertEquals(0, keyStatisticsResource.getAssessorsInvited());
    }

    @Test
    public void getClosedKeyStatistics() {
        CompetitionClosedKeyAssessmentStatisticsResource keyStatisticsResource = controller.getClosedKeyStatistics(1L)
                .getSuccess();

        assertEquals(0, keyStatisticsResource.getAssessorsAccepted());
        assertEquals(0, keyStatisticsResource.getAssessorsInvited());
        assertEquals(0, keyStatisticsResource.getAssessorsWithoutApplications());
    }

    @Test
    public void getInAssessmentKeyStatistics() {
        CompetitionInAssessmentKeyAssessmentStatisticsResource keyStatisticsResource = controller
                .getInAssessmentKeyStatistics(1L).getSuccess();

        assertEquals(4, keyStatisticsResource.getAssessmentsStarted());
        assertEquals(9, keyStatisticsResource.getAssignmentCount());
        assertEquals(2, keyStatisticsResource.getAssessmentsSubmitted());
        assertEquals(1, keyStatisticsResource.getAssignmentsAccepted());
        assertEquals(1, keyStatisticsResource.getAssignmentsWaiting());
    }
}