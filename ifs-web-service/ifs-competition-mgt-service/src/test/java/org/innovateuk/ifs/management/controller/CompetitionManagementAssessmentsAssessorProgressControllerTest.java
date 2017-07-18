package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationCountSummaryRestService;
import org.innovateuk.ifs.assessment.resource.AssessorCompetitionSummaryResource;
import org.innovateuk.ifs.assessment.resource.AssessorProfileResource;
import org.innovateuk.ifs.assessment.service.AssessorCompetitionSummaryRestService;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.model.AssessorAssessmentProgressModelPopulator;
import org.innovateuk.ifs.management.model.ManageApplicationsModelPopulator;
import org.innovateuk.ifs.management.viewmodel.AssessorAssessmentProgressViewModel;
import org.innovateuk.ifs.management.viewmodel.ManageApplicationsViewModel;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
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

import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.application.builder.ApplicationCountSummaryResourceBuilder.newApplicationCountSummaryResource;
import static org.innovateuk.ifs.assessment.builder.AssessorCompetitionSummaryResourceBuilder.newAssessorCompetitionSummaryResource;
import static org.innovateuk.ifs.assessment.builder.AssessorProfileResourceBuilder.newAssessorProfileResource;
import static org.innovateuk.ifs.assessment.builder.ProfileResourceBuilder.newProfileResource;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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

    @Mock
    private ApplicationCountSummaryRestService applicationCountSummaryRestService;

    @InjectMocks
    @Spy
    private AssessorAssessmentProgressModelPopulator assessorAssessmentProgressModelPopulator;

    @Override
    protected CompetitionManagementAssessmentsAssessorProgressController supplyControllerUnderTest() {
        return new CompetitionManagementAssessmentsAssessorProgressController();
    }

    @Test
    public void assessorProgress_mine() throws Exception {
        long assessorId = 1L;

        AddressResource expectedAddress = getExpectedAddress();
        List<InnovationAreaResource> expectedInnovationAreas = getInnovationAreas();
        AssessorProfileResource expectedProfile = getAssessorProfile(expectedAddress, expectedInnovationAreas);

        CompetitionResource competitionResource = newCompetitionResource()
                .withName("name")
                .withCompetitionStatus(IN_ASSESSMENT)
                .build();

        AssessorCompetitionSummaryResource summaryResource = newAssessorCompetitionSummaryResource()
                .withAssessor(expectedProfile)
                .withCompetitionId(competitionResource.getId())
                .withCompetitionName("name")
                .build();

        List<ApplicationCountSummaryResource> summaryResources = newApplicationCountSummaryResource()
                .withName("one", "two")
                .withLeadOrganisation("Lead Org 1", "Lead Org 2")
                .withAccepted(2L, 3L)
                .withAssessors(3L, 4L)
                .withSubmitted(1L, 2L).build(2);

        ApplicationCountSummaryPageResource expectedPageResource = new ApplicationCountSummaryPageResource(41, 3, summaryResources, 1, 20);

        when(assessorCompetitionSummaryRestService.getAssessorSummary(assessorId, competitionResource.getId())).thenReturn(restSuccess(summaryResource));
        when(competitionRestService.getCompetitionById(competitionResource.getId())).thenReturn(restSuccess(competitionResource));
        //when(applicationCountSummaryRestService.getApplicationCountSummariesByCompetitionId(competitionResource.getId(), 1,20,"filter")).thenReturn(restSuccess(expectedPageResource));

        when(applicationCountSummaryRestService.getApplicationCountSummariesByCompetitionIdAndInnovationArea(competitionResource.getId(), 1, 20, empty(), "")).thenReturn(restSuccess(expectedPageResource));

        AssessorAssessmentProgressViewModel model = (AssessorAssessmentProgressViewModel) mockMvc.perform(get("/assessment/competition/{competitionId}/assessors/{assessorId}?page=1", competitionResource.getId(), assessorId))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/assessor-progress"))
                .andExpect(model().attributeExists("model"))
                .andReturn().getModelAndView().getModel().get("model");

        assertEquals((long) competitionResource.getId(), model.getCompetitionId());
        assertEquals(competitionResource.getName(), model.getCompetitionName());
        assertTrue(model.getApplicationsView().getInAssessment());
        assertEquals(2, model.getApplicationsView().getApplications().size());
        assertEquals(2L, model.getApplicationsView().getApplications().get(0).getAccepted());
        assertEquals(3L, model.getApplicationsView().getApplications().get(0).getAssessors());
        assertEquals(1L, model.getApplicationsView().getApplications().get(0).getSubmitted());
        assertEquals("Lead Org 1", model.getApplicationsView().getApplications().get(0).getLeadOrganisation());

        assertEquals(3L, model.getApplicationsView().getApplications().get(1).getAccepted());
        assertEquals(4L, model.getApplicationsView().getApplications().get(1).getAssessors());
        assertEquals(2L, model.getApplicationsView().getApplications().get(1).getSubmitted());
        assertEquals("Lead Org 2", model.getApplicationsView().getApplications().get(1).getLeadOrganisation());

        PaginationViewModel actualPagination = model.getApplicationsView().getPagination();
        assertEquals(1, actualPagination.getCurrentPage());
        assertEquals(20,actualPagination.getPageSize());
        assertEquals(3, actualPagination.getTotalPages());
        assertEquals("1 to 20", actualPagination.getPageNames().get(0).getTitle());
        assertEquals("21 to 40", actualPagination.getPageNames().get(1).getTitle());
        assertEquals("41 to 41", actualPagination.getPageNames().get(2).getTitle());
    }

    private AddressResource getExpectedAddress() {
        return newAddressResource()
                .withAddressLine1("1 Testing Lane")
                .withTown("Testville")
                .withCounty("South Testshire")
                .withPostcode("TES TEST")
                .build();
    }

    private List<InnovationAreaResource> getInnovationAreas() {
        return newInnovationAreaResource()
                .withSector(1L, 2L, 1L)
                .withSectorName("sector 1", "sector 2", "sector 1")
                .withName("innovation area 1", "innovation area 2", "innovation area 3")
                .build(3);
    }

    private AssessorProfileResource getAssessorProfile(AddressResource expectedAddress, List<InnovationAreaResource> expectedInnovationAreas) {
        return newAssessorProfileResource()
                .withUser(
                        newUserResource()
                                .withFirstName("Test")
                                .withLastName("Tester")
                                .withEmail("test@test.com")
                                .withPhoneNumber("012345")
                                .build()
                )
                .withProfile(
                        newProfileResource()
                                .withSkillsAreas("A Skill")
                                .withBusinessType(ACADEMIC)
                                .withInnovationAreas(expectedInnovationAreas)
                                .withAddress(expectedAddress)
                                .build()
                )
                .build();
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
