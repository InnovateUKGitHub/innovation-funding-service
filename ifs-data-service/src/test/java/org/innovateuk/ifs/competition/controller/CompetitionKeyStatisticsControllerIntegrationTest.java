package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.CompetitionClosedKeyStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionInAssessmentKeyStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionOpenKeyStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionReadyToOpenKeyStatisticsResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionClosedKeyStatisticsResourceBuilder.newCompetitionClosedKeyStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionInAssessmentKeyStatisticsResourceBuilder.newCompetitionInAssessmentKeyStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionOpenKeyStatisticsResourceBuilder.newCompetitionOpenKeyStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionReadyToOpenKeyStatisticsResourceBuilder.newCompetitionReadyToOpenKeyStatisticsResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class CompetitionKeyStatisticsControllerIntegrationTest extends BaseControllerIntegrationTest<CompetitionKeyStatisticsController> {

    @Autowired
    @Override
    protected void setControllerUnderTest(CompetitionKeyStatisticsController controller) {
        this.controller = controller;
    }

    @Autowired
    CompetitionRepository competitionRepository;

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

}
