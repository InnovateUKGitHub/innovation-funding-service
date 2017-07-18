package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.resource.AssessorAssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessorCompetitionSummaryResource;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.service.AssessorCompetitionSummaryRestService;
import org.innovateuk.ifs.management.model.AssessorAssessmentProgressModelPopulator;
import org.innovateuk.ifs.management.viewmodel.AssessorAssessmentProgressViewModel;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.Matchers.hasItems;
import static org.innovateuk.ifs.assessment.builder.AssessorAssessmentResourceBuilder.newAssessorAssessmentResource;
import static org.innovateuk.ifs.assessment.builder.AssessorCompetitionSummaryResourceBuilder.newAssessorCompetitionSummaryResource;
import static org.innovateuk.ifs.assessment.builder.AssessorProfileResourceBuilder.newAssessorProfileResource;
import static org.innovateuk.ifs.assessment.builder.ProfileResourceBuilder.newProfileResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.ACCEPTED;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.SUBMITTED;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class CompetitionManagementAssessmentsAssessorProgressControllerTest extends BaseControllerMockMVCTest<CompetitionManagementAssessmentsAssessorProgressController> {

    @Mock
    private AssessorCompetitionSummaryRestService assessorCompetitionSummaryRestService;

    @InjectMocks
    @Spy
    private AssessorAssessmentProgressModelPopulator assessorAssessmentProgressModelPopulator;

    @Override
    protected CompetitionManagementAssessmentsAssessorProgressController supplyControllerUnderTest() {
        return new CompetitionManagementAssessmentsAssessorProgressController();
    }

    @Test
    public void assessorProgress() throws Exception {
        long competitionId = 1L;
        long assessorId = 2L;

        AssessorProfileResource assessor = newAssessorProfileResource()
                .withProfile(
                        newProfileResource()
                                .withBusinessType(ACADEMIC)
                                .withInnovationAreas(
                                        newInnovationAreaResource()
                                                .withName("Innovation 1", "Innovation 2")
                                                .build(2)
                                )
                                .build()
                )
                .withUser(
                        newUserResource()
                                .withFirstName("Paul")
                                .withLastName("Plum")
                                .build()
                )
                .build();

        List<AssessorAssessmentResource> assignedAssessments = newAssessorAssessmentResource()
                .withApplicationId(10L, 20L)
                .withApplicationName("Test App 1", "Test App 2")
                .withTotalAssessors(5, 7)
                .withLeadOrganisation("Lead Org 1", "Lead Org 2")
                .withState(SUBMITTED, ACCEPTED)
                .build(2);

        AssessorCompetitionSummaryResource assessorCompetitionSummaryResource = newAssessorCompetitionSummaryResource()
                .withCompetitionId(competitionId)
                .withCompetitionName("Test Competition")
                .withTotalApplications(20L)
                .withAssessor(assessor)
                .withAssignedAssessments(assignedAssessments)
                .build();

        when(assessorCompetitionSummaryRestService.getAssessorSummary(assessorId, competitionId))
                .thenReturn(restSuccess(assessorCompetitionSummaryResource));

        MvcResult result = mockMvc.perform(get("/assessment/competition/{competitionId}/assessors/{assessorId}", competitionId, assessorId))
                .andExpect(model().attributeExists("model"))
                .andExpect(model().attribute("originQuery", "?origin=ASSESSOR_PROGRESS&assessorId=2"))
                .andExpect(view().name("competition/assessor-progress"))
                .andReturn();

        verify(assessorCompetitionSummaryRestService).getAssessorSummary(assessorId, competitionId);

        AssessorAssessmentProgressViewModel model = (AssessorAssessmentProgressViewModel) result.getModelAndView().getModel().get("model");

        assertEquals("Paul Plum", model.getAssessorName());
        assertEquals(competitionId, model.getCompetitionId());
        assertEquals("Test Competition", model.getCompetitionName());
        assertEquals("Academic", model.getBusinessType());
        assertEquals(20L, model.getTotalApplications());
        assertThat(model.getInnovationAreas(), hasItems("Innovation 1", "Innovation 2"));

        assertEquals(2, assignedAssessments.size());

        assertEquals(assignedAssessments.get(0).getApplicationId(), model.getAssigned().get(0).getApplicationId());
        assertEquals(assignedAssessments.get(0).getApplicationName(), model.getAssigned().get(0).getApplicationName());
        assertEquals(assignedAssessments.get(0).getLeadOrganisation(), model.getAssigned().get(0).getLeadOrganisation());
        assertEquals(assignedAssessments.get(0).getTotalAssessors(), model.getAssigned().get(0).getTotalAssessors());
        assertEquals(assignedAssessments.get(0).getState(), model.getAssigned().get(0).getState());

        assertEquals(assignedAssessments.get(1).getApplicationId(), model.getAssigned().get(1).getApplicationId());
        assertEquals(assignedAssessments.get(1).getApplicationName(), model.getAssigned().get(1).getApplicationName());
        assertEquals(assignedAssessments.get(1).getLeadOrganisation(), model.getAssigned().get(1).getLeadOrganisation());
        assertEquals(assignedAssessments.get(1).getTotalAssessors(), model.getAssigned().get(1).getTotalAssessors());
        assertEquals(assignedAssessments.get(1).getState(), model.getAssigned().get(1).getState());
    }
}
