package org.innovateuk.ifs.management.assessment.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.JsonTestUtil;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource.Sort;
import org.innovateuk.ifs.application.service.ApplicationCountSummaryRestService;
import org.innovateuk.ifs.assessment.resource.*;
import org.innovateuk.ifs.assessment.service.AssessmentPeriodService;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.assessment.service.AssessorCompetitionSummaryRestService;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.assessment.form.ApplicationSelectionForm;
import org.innovateuk.ifs.management.assessment.populator.AssessorAssessmentProgressModelPopulator;
import org.innovateuk.ifs.management.assessment.viewmodel.AssessorAssessmentProgressRemoveViewModel;
import org.innovateuk.ifs.management.assessment.viewmodel.AssessorAssessmentProgressUnsubmitViewModel;
import org.innovateuk.ifs.management.assessment.viewmodel.AssessorAssessmentProgressViewModel;
import org.innovateuk.ifs.pagination.PaginationViewModel;
import org.innovateuk.ifs.util.CompressedCookieService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasItems;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.application.builder.ApplicationCountSummaryResourceBuilder.newApplicationCountSummaryResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentCreateResourceBuilder.newAssessmentCreateResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.assessment.builder.AssessorAssessmentResourceBuilder.newAssessorAssessmentResource;
import static org.innovateuk.ifs.assessment.builder.AssessorCompetitionSummaryResourceBuilder.newAssessorCompetitionSummaryResource;
import static org.innovateuk.ifs.assessment.builder.AssessorProfileResourceBuilder.newAssessorProfileResource;
import static org.innovateuk.ifs.assessment.builder.ProfileResourceBuilder.newProfileResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue.CONFLICT_OF_INTEREST;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.*;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.innovateuk.ifs.util.CookieTestUtil.setupCompressedCookieService;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AssessmentAssessorProgressControllerTest extends BaseControllerMockMVCTest<AssessmentAssessorProgressController> {

    @Mock
    private AssessorCompetitionSummaryRestService assessorCompetitionSummaryRestService;

    @Mock
    private ApplicationCountSummaryRestService applicationCountSummaryRestService;

    @InjectMocks
    @Spy
    private AssessorAssessmentProgressModelPopulator assessorAssessmentProgressModelPopulator;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private AssessmentRestService assessmentRestService;

    @Mock
    private CompressedCookieService compressedCookieService;

    @Mock
    private AssessmentPeriodService assessmentPeriodService;

    @Before
    public void setUpCookies() {
        setupCompressedCookieService(compressedCookieService);
    }
    @Override
    protected AssessmentAssessorProgressController supplyControllerUnderTest() {
        return new AssessmentAssessorProgressController();
    }

    @Test
    public void assessorProgress() throws Exception {
        long competitionId = 1L;
        long assessorId = 2L;
        long assessmentPeriodId = 3L;
        long applicationId = 18L;

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
                .withApplicationId(10L, 20L, 30L)
                .withApplicationName("Test App 1", "Test App 2", "Test App 3")
                .withTotalAssessors(5, 7, 6)
                .withLeadOrganisation("Lead Org 1", "Lead Org 2", "Lead Org 3")
                .withState(SUBMITTED, ACCEPTED, REJECTED)
                .withRejectionReason(null, null, CONFLICT_OF_INTEREST)
                .withRejectionComment(null, null, "rejection comment")
                .withAssessmentPeriodId(assessmentPeriodId, assessmentPeriodId, assessmentPeriodId)
                .build(3);

        AssessorCompetitionSummaryResource assessorCompetitionSummaryResource = newAssessorCompetitionSummaryResource()
                .withCompetitionId(competitionId)
                .withCompetitionName("Test Competition")
                .withCompetitionStatus(IN_ASSESSMENT)
                .withTotalApplications(20L)
                .withAssessor(assessor)
                .withAssignedAssessments(assignedAssessments)
                .build();

        List<ApplicationCountSummaryResource> summaryResources = newApplicationCountSummaryResource()
                .withName("one", "two")
                .withLeadOrganisation("Lead Org 1", "Lead Org 2")
                .withAccepted(2L, 3L)
                .withAssessors(3L, 4L)
                .withSubmitted(1L, 2L).build(2);

        ApplicationCountSummaryPageResource expectedPageResource = new ApplicationCountSummaryPageResource(41, 3, summaryResources, 1, 20);

        CompetitionResource competitionResource = newCompetitionResource()
                .withId(competitionId)
                .withName("name")
                .withCompetitionStatus(IN_ASSESSMENT)
                .build();

        when(assessorCompetitionSummaryRestService.getAssessorSummary(assessorId, competitionId))
                .thenReturn(restSuccess(assessorCompetitionSummaryResource));
        when(competitionRestService.getCompetitionById(competitionResource.getId())).thenReturn(restSuccess(competitionResource));
        when(applicationCountSummaryRestService.getApplicationCountSummariesByCompetitionIdAndAssessorId(competitionId, assessorId, assessmentPeriodId, 0, Sort.APPLICATION_NUMBER, "")).thenReturn(restSuccess(expectedPageResource));
        when(applicationCountSummaryRestService.getApplicationIdsByCompetitionIdAndAssessorId(competitionResource.getId(), assessorId, assessmentPeriodId, "")).thenReturn(restSuccess(asList(1L, 21L)));
        when(assessmentPeriodService.assessmentPeriodName(assessmentPeriodId, competitionResource.getId())).thenReturn("name");

        MvcResult result = mockMvc.perform(get("/assessment/competition/{competitionId}/assessors/{assessorId}/period/{assessmentPeriodId}", competitionId, assessorId, assessmentPeriodId))
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("competition/assessor-progress"))
                .andReturn();

        verify(assessorCompetitionSummaryRestService).getAssessorSummary(assessorId, competitionId);

        AssessorAssessmentProgressViewModel model = (AssessorAssessmentProgressViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(assessmentPeriodId, model.getAssessmentPeriodId());
        assertEquals("name", model.getAssessmentPeriodName());
        assertEquals("Paul Plum", model.getAssessorName());
        assertEquals(competitionId, model.getCompetitionId());
        assertEquals("Test Competition", model.getCompetitionName());
        assertEquals(IN_ASSESSMENT, model.getCompetitionStatus());
        assertEquals("Academic", model.getBusinessType());
        assertEquals(20L, model.getTotalApplications());
        assertThat(model.getInnovationAreas(), hasItems("Innovation 1", "Innovation 2"));

        assertEquals(2, model.getAssigned().size());
        assertEquals(1, model.getRejected().size());

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

        assertEquals(assignedAssessments.get(2).getApplicationId(), model.getRejected().get(0).getApplicationId());
        assertEquals(assignedAssessments.get(2).getApplicationName(), model.getRejected().get(0).getApplicationName());
        assertEquals(assignedAssessments.get(2).getLeadOrganisation(), model.getRejected().get(0).getLeadOrganisation());
        assertEquals(assignedAssessments.get(2).getTotalAssessors(), model.getRejected().get(0).getTotalAssessors());
        assertEquals(assignedAssessments.get(2).getRejectReason(), model.getRejected().get(0).getRejectReason());
        assertEquals(assignedAssessments.get(2).getRejectComment(), model.getRejected().get(0).getRejectComment());


        AssessmentCreateResource expectedAssessmentCreateResource = newAssessmentCreateResource()
                .withApplicationId(applicationId)
                .withAssessorId(assessorId)
                .build();

        AssessmentResource expectedAssessmentResource = newAssessmentResource().build();
        ApplicationSelectionForm form = new ApplicationSelectionForm();
        form.setSelectedApplications(singletonList(applicationId));
        when(compressedCookieService.getCookieValue(any(), eq("applicationSelectionForm_comp_1"))).thenReturn(JsonTestUtil.toJson(form));
        when(assessmentRestService.createAssessments(singletonList(expectedAssessmentCreateResource))).thenReturn(restSuccess(singletonList(expectedAssessmentResource)));

        mockMvc.perform(post("/assessment/competition/{competitionId}/assessors/{assessorId}/period/{assessmentPeriodId}", competitionId, assessorId, assessmentPeriodId))
                .andExpect(status().is3xxRedirection())
                .andReturn();
    }

    @Test
    public void assessorProgress_applications() throws Exception {
        long assessorId = 1L;
        long assessmentPeriodId = 3L;

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
        when(applicationCountSummaryRestService.getApplicationCountSummariesByCompetitionIdAndAssessorId(competitionResource.getId(), assessorId, assessmentPeriodId, 1, Sort.APPLICATION_NUMBER, "")).thenReturn(restSuccess(expectedPageResource));
        when(applicationCountSummaryRestService.getApplicationIdsByCompetitionIdAndAssessorId(competitionResource.getId(), assessorId, assessmentPeriodId, "")).thenReturn(restSuccess(asList(1L, 21L)));
        when(assessmentPeriodService.assessmentPeriodName(assessmentPeriodId, competitionResource.getId())).thenReturn("name");
        AssessorAssessmentProgressViewModel model = (AssessorAssessmentProgressViewModel) mockMvc.perform(get("/assessment/competition/{competitionId}/assessors/{assessorId}/period/{assessmentPeriodId}?page=2", competitionResource.getId(), assessorId, assessmentPeriodId))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/assessor-progress"))
                .andExpect(model().attributeExists("model"))
                .andReturn().getModelAndView().getModel().get("model");

        assertEquals((long) competitionResource.getId(), model.getCompetitionId());
        assertEquals(competitionResource.getName(), model.getCompetitionName());
        assertTrue(model.getApplicationsView().getInAssessment());
        assertEquals(assessmentPeriodId, model.getAssessmentPeriodId());
        assertEquals("name", model.getAssessmentPeriodName());
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
        assertEquals(2, actualPagination.getCurrentPage());
        assertEquals(20,actualPagination.getSize());
        assertEquals(3, actualPagination.getTotalPages());
    }

    @Test
    public void withdrawAssessment() throws Exception {
        long competitionId = 1L;
        long assessorId = 2L;
        long assessmentId = 3L;
        long assessmentPeriodId = 2L;

        when(assessmentRestService.withdrawAssessment(assessmentId)).thenReturn(restSuccess());

        mockMvc.perform(
                post("/assessment/competition/{competitionId}/assessors/{assessorId}/withdraw/{assessmentId}/period/{assessmentPeriodId}", competitionId, assessorId, assessmentId, assessmentPeriodId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/assessment/competition/%s/assessors/%s/period/%s", competitionId, assessorId, assessmentPeriodId)));

        InOrder inOrder = inOrder(assessmentRestService);
        inOrder.verify(assessmentRestService).withdrawAssessment(assessmentId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void withdrawAssessmentConfirm() throws Exception {
        long competitionId = 1L;
        long assessorId = 2L;
        long assessmentId = 3L;
        long assessmentPeriodId = 2L;

        AssessorAssessmentProgressRemoveViewModel expectedModel = new AssessorAssessmentProgressRemoveViewModel(
                competitionId,
                assessorId,
                assessmentId
        );

        mockMvc.perform(
                get("/assessment/competition/{competitionId}/assessors/{assessorId}/withdraw/{assessmentId}/period/{assessmentPeriodId}/confirm", competitionId, assessorId, assessmentId, assessmentPeriodId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedModel))
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("competition/assessor-progress-remove-confirm"));
    }

    @Test
    public void unsubmitAssessment() throws Exception {
        long competitionId = 1L;
        long assessorId = 2L;
        long assessmentId = 3L;

        when(assessmentRestService.unsubmitAssessment(assessmentId)).thenReturn(restSuccess());

        mockMvc.perform(
                post("/assessment/competition/{competitionId}/assessors/{assessorId}/unsubmit/{assessmentId}", competitionId, assessorId, assessmentId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/assessment/competition/%s/assessors/%s", competitionId, assessorId)));

        InOrder inOrder = inOrder(assessmentRestService);
        inOrder.verify(assessmentRestService).unsubmitAssessment(assessmentId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void unsubmitAssessmentConfirm() throws Exception {
        long competitionId = 1L;
        long assessorId = 2L;
        long assessmentId = 3L;

        AssessorAssessmentProgressUnsubmitViewModel expectedModel = new AssessorAssessmentProgressUnsubmitViewModel(
                competitionId,
                assessorId,
                assessmentId
        );

        mockMvc.perform(
                get("/assessment/competition/{competitionId}/assessors/{assessorId}/unsubmit/{assessmentId}/confirm", competitionId, assessorId, assessmentId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedModel))
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("competition/assessor-progress-unsubmit-assessment-confirm"));
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
}
