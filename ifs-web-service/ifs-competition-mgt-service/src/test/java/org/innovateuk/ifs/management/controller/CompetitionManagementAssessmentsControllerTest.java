package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionInAssessmentKeyStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.management.model.ManageAssessmentsModelPopulator;
import org.innovateuk.ifs.management.viewmodel.ManageAssessmentsViewModel;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.competition.builder.CompetitionInAssessmentKeyStatisticsResourceBuilder.newCompetitionInAssessmentKeyStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CompetitionManagementAssessmentsControllerTest extends BaseControllerMockMVCTest<CompetitionManagementAssessmentsController> {

    @Mock
    private ManageAssessmentsModelPopulator manageAssessmentsModelPopulatorMock;

    @Override
    protected CompetitionManagementAssessmentsController supplyControllerUnderTest() {
        return new CompetitionManagementAssessmentsController();
    }

    @Test
    public void manageAssessments() throws Exception {
        final int expectedCompetitionId = 13;
        final String expectedCompetitionName = "Test Competition";
        final CompetitionStatus expectedCompetitionStatus = CompetitionStatus.IN_ASSESSMENT;
        final int expectedAssignmentCount = 2;
        final int expectedAssignmentsWaiting = 3;
        final int expectedAssignmentsAccepted = 5;
        final int expectedAssessmentsStarted = 7;
        final int expectedAssessmentsSubmitted = 11;

        CompetitionResource competitionResource = newCompetitionResource()
                .withName(expectedCompetitionName)
                .withCompetitionStatus(expectedCompetitionStatus)
                .build();
        CompetitionInAssessmentKeyStatisticsResource statisticsResource = newCompetitionInAssessmentKeyStatisticsResource()
                .withAssignmentCount(expectedAssignmentCount)
                .withAssignmentsWaiting(expectedAssignmentsWaiting)
                .withAssignmentsAccepted(expectedAssignmentsAccepted)
                .withAssessmentsStarted(expectedAssessmentsStarted)
                .withAssessmentsSubmitted(expectedAssessmentsSubmitted)
                .build();

        ManageAssessmentsViewModel expectedModel = new ManageAssessmentsViewModel(competitionResource, statisticsResource);

        when(manageAssessmentsModelPopulatorMock.populateModel(expectedCompetitionId)).thenReturn(expectedModel);

        mockMvc.perform(get("/assessment/competition/{competitionId}", competitionResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedModel))
                .andExpect(view().name("competition/manage-assessments"))
                .andReturn();
    }
}
