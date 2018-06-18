package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.resource.CompetitionInAssessmentKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.management.assessment.populator.ManageAssessmentsModelPopulator;
import org.innovateuk.ifs.management.assessment.viewmodel.ManageAssessmentsViewModel;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.assessment.builder.CompetitionInAssessmentKeyAssessmentStatisticsResourceBuilder.newCompetitionInAssessmentKeyAssessmentStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AssessmentsControllerTest extends BaseControllerMockMVCTest<AssessmentController> {

    @Mock
    private ManageAssessmentsModelPopulator manageAssessmentsModelPopulatorMock;

    @Override
    protected AssessmentController supplyControllerUnderTest() {
        return new AssessmentController();
    }

    @Test
    public void manageAssessments() throws Exception {
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
        CompetitionInAssessmentKeyAssessmentStatisticsResource statisticsResource = newCompetitionInAssessmentKeyAssessmentStatisticsResource()
                .withAssignmentCount(expectedAssignmentCount)
                .withAssignmentsWaiting(expectedAssignmentsWaiting)
                .withAssignmentsAccepted(expectedAssignmentsAccepted)
                .withAssessmentsStarted(expectedAssessmentsStarted)
                .withAssessmentsSubmitted(expectedAssessmentsSubmitted)
                .build();

        ManageAssessmentsViewModel expectedModel = new ManageAssessmentsViewModel(competitionResource, statisticsResource);

        when(manageAssessmentsModelPopulatorMock.populateModel(competitionResource.getId())).thenReturn(expectedModel);

        mockMvc.perform(get("/assessment/competition/{competitionId}", competitionResource.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedModel))
                .andExpect(view().name("competition/manage-assessments"))
                .andReturn();
    }
}
