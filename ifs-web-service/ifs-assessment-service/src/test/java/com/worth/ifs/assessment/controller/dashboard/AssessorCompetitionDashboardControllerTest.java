package com.worth.ifs.assessment.controller.dashboard;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.assessment.form.dashboard.AssessorCompetitionDashboardAssessmentForm;
import com.worth.ifs.assessment.model.AssessorCompetitionDashboardModelPopulator;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.service.AssessmentService;
import com.worth.ifs.assessment.viewmodel.AssessorCompetitionDashboardApplicationViewModel;
import com.worth.ifs.assessment.viewmodel.AssessorCompetitionDashboardViewModel;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.user.resource.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.List;

import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static com.worth.ifs.assessment.resource.AssessmentStates.*;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessorCompetitionDashboardControllerTest extends BaseControllerMockMVCTest<AssessorCompetitionDashboardController> {

    @Spy
    @InjectMocks
    private AssessorCompetitionDashboardModelPopulator assessorCompetitionDashboardModelPopulator;

    @Mock
    private AssessmentService assessmentService;

    @Override
    protected AssessorCompetitionDashboardController supplyControllerUnderTest() {
        return new AssessorCompetitionDashboardController();
    }

    @Test
    public void competitionDashboard() throws Exception {
        Long userId = 1L;

        UserResource leadTechnologist = buildTestLeadTechnologist();
        CompetitionResource competition = buildTestCompetition(leadTechnologist.getId());
        List<ApplicationResource> applications = buildTestApplications();

        List<AssessmentResource> assessments = newAssessmentResource()
                .withId(1L, 2L, 3L, 4L)
                .withApplication(applications.get(0).getId(), applications.get(1).getId(), applications.get(2).getId(), applications.get(3).getId())
                .withCompetition(competition.getId())
                .withActivityState(PENDING, ACCEPTED, READY_TO_SUBMIT, SUBMITTED)
                .build(4);

        RoleResource role = buildLeadApplicantRole();
        List<OrganisationResource> organisations = buildTestOrganisations();
        List<ProcessRoleResource> participants = newProcessRoleResource()
                .withRole(role)
                .withOrganisation(organisations.get(0).getId(), organisations.get(1).getId(), organisations.get(2).getId(), organisations.get(3).getId())
                .build(4);

        when(competitionService.getById(competition.getId())).thenReturn(competition);
        when(userService.findById(leadTechnologist.getId())).thenReturn(leadTechnologist);
        when(assessmentService.getByUserAndCompetition(userId, competition.getId())).thenReturn(assessments);
        applications.forEach(application -> when(applicationService.getById(application.getId())).thenReturn(application));
        when(processRoleService.findProcessRolesByApplicationId(applications.get(0).getId())).thenReturn(asList(participants.get(0)));
        when(processRoleService.findProcessRolesByApplicationId(applications.get(1).getId())).thenReturn(asList(participants.get(1)));
        when(processRoleService.findProcessRolesByApplicationId(applications.get(2).getId())).thenReturn(asList(participants.get(2)));
        when(processRoleService.findProcessRolesByApplicationId(applications.get(3).getId())).thenReturn(asList(participants.get(3)));
        organisations.forEach(organisation -> when(organisationRestService.getOrganisationById(organisation.getId())).thenReturn(restSuccess(organisation)));

        MvcResult result = mockMvc.perform(get("/assessor/dashboard/competition/{competitionId}", competition.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-competition-dashboard"))
                .andReturn();

        InOrder inOrder = inOrder(competitionService, userService, assessmentService);
        inOrder.verify(competitionService).getById(competition.getId());
        inOrder.verify(userService).findById(leadTechnologist.getId());
        inOrder.verify(assessmentService).getByUserAndCompetition(userId, competition.getId());
        inOrder.verifyNoMoreInteractions();

        assessments.forEach(assessment -> {
            InOrder inOrderByAssessment = inOrder(applicationService, processRoleService, organisationRestService);
            inOrderByAssessment.verify(applicationService).getById(assessment.getApplication());
            inOrderByAssessment.verify(processRoleService).findProcessRolesByApplicationId(assessment.getApplication());
            inOrderByAssessment.verify(organisationRestService).getOrganisationById(isA(Long.class));
        });

        List<AssessorCompetitionDashboardApplicationViewModel> expectedSubmitted = asList(
                new AssessorCompetitionDashboardApplicationViewModel(applications.get(3).getId(), assessments.get(3).getId(), applications.get(3).getName(), organisations.get(3).getName(), assessments.get(3).getAssessmentState())
        );

        List<AssessorCompetitionDashboardApplicationViewModel> expectedOutstanding = asList(
                new AssessorCompetitionDashboardApplicationViewModel(applications.get(0).getId(), assessments.get(0).getId(), applications.get(0).getName(), organisations.get(0).getName(), assessments.get(0).getAssessmentState()),
                new AssessorCompetitionDashboardApplicationViewModel(applications.get(1).getId(), assessments.get(1).getId(), applications.get(1).getName(), organisations.get(1).getName(), assessments.get(1).getAssessmentState()),
                new AssessorCompetitionDashboardApplicationViewModel(applications.get(2).getId(), assessments.get(2).getId(), applications.get(2).getName(), organisations.get(2).getName(), assessments.get(2).getAssessmentState())
        );

        AssessorCompetitionDashboardViewModel model = (AssessorCompetitionDashboardViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(competition.getName(), model.getCompetitionTitle());
        assertEquals("Juggling Craziness (CRD3359)", model.getCompetition());
        assertEquals("Competition Technologist", model.getLeadTechnologist());
        assertEquals(competition.getAssessorAcceptsDate(), model.getAcceptDeadline());
        assertEquals(competition.getAssessorDeadlineDate(), model.getSubmitDeadline());
        assertEquals(expectedSubmitted, model.getSubmitted());
        assertEquals(expectedOutstanding, model.getOutstanding());
        assertTrue(model.isSubmitVisible());
    }

    @Test
    public void competitionDashboard_submitNotVisible() throws Exception {
        Long userId = 1L;

        UserResource leadTechnologist = buildTestLeadTechnologist();
        CompetitionResource competition = buildTestCompetition(leadTechnologist.getId());
        List<ApplicationResource> applications = buildTestApplications();

        List<AssessmentResource> assessments = newAssessmentResource()
                .withId(1L, 2L, 3L, 4L)
                .withApplication(applications.get(0).getId(), applications.get(1).getId(), applications.get(2).getId(), applications.get(3).getId())
                .withCompetition(competition.getId())
                .withActivityState(PENDING, ACCEPTED, OPEN, SUBMITTED)
                .build(4);

        RoleResource role = buildLeadApplicantRole();
        List<OrganisationResource> organisations = buildTestOrganisations();
        List<ProcessRoleResource> participants = newProcessRoleResource()
                .withRole(role)
                .withOrganisation(organisations.get(0).getId(), organisations.get(1).getId(), organisations.get(2).getId(), organisations.get(3).getId())
                .build(4);

        when(competitionService.getById(competition.getId())).thenReturn(competition);
        when(userService.findById(leadTechnologist.getId())).thenReturn(leadTechnologist);
        when(assessmentService.getByUserAndCompetition(userId, competition.getId())).thenReturn(assessments);
        applications.forEach(application -> when(applicationService.getById(application.getId())).thenReturn(application));
        when(processRoleService.findProcessRolesByApplicationId(applications.get(0).getId())).thenReturn(asList(participants.get(0)));
        when(processRoleService.findProcessRolesByApplicationId(applications.get(1).getId())).thenReturn(asList(participants.get(1)));
        when(processRoleService.findProcessRolesByApplicationId(applications.get(2).getId())).thenReturn(asList(participants.get(2)));
        when(processRoleService.findProcessRolesByApplicationId(applications.get(3).getId())).thenReturn(asList(participants.get(3)));
        organisations.forEach(organisation -> when(organisationRestService.getOrganisationById(organisation.getId())).thenReturn(restSuccess(organisation)));

        MvcResult result = mockMvc.perform(get("/assessor/dashboard/competition/{competitionId}", competition.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-competition-dashboard"))
                .andReturn();

        InOrder inOrder = inOrder(competitionService, userService, assessmentService);
        inOrder.verify(competitionService).getById(competition.getId());
        inOrder.verify(userService).findById(leadTechnologist.getId());
        inOrder.verify(assessmentService).getByUserAndCompetition(userId, competition.getId());
        inOrder.verifyNoMoreInteractions();

        assessments.forEach(assessment -> {
            InOrder inOrderByAssessment = inOrder(applicationService, processRoleService, organisationRestService);
            inOrderByAssessment.verify(applicationService).getById(assessment.getApplication());
            inOrderByAssessment.verify(processRoleService).findProcessRolesByApplicationId(assessment.getApplication());
            inOrderByAssessment.verify(organisationRestService).getOrganisationById(isA(Long.class));
        });

        List<AssessorCompetitionDashboardApplicationViewModel> expectedSubmitted = asList(
                new AssessorCompetitionDashboardApplicationViewModel(applications.get(3).getId(), assessments.get(3).getId(), applications.get(3).getName(), organisations.get(3).getName(), assessments.get(3).getAssessmentState())
        );

        List<AssessorCompetitionDashboardApplicationViewModel> expectedOutstanding = asList(
                new AssessorCompetitionDashboardApplicationViewModel(applications.get(0).getId(), assessments.get(0).getId(), applications.get(0).getName(), organisations.get(0).getName(), assessments.get(0).getAssessmentState()),
                new AssessorCompetitionDashboardApplicationViewModel(applications.get(1).getId(), assessments.get(1).getId(), applications.get(1).getName(), organisations.get(1).getName(), assessments.get(1).getAssessmentState()),
                new AssessorCompetitionDashboardApplicationViewModel(applications.get(2).getId(), assessments.get(2).getId(), applications.get(2).getName(), organisations.get(2).getName(), assessments.get(2).getAssessmentState())
        );

        AssessorCompetitionDashboardViewModel model = (AssessorCompetitionDashboardViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(competition.getName(), model.getCompetitionTitle());
        assertEquals("Juggling Craziness (CRD3359)", model.getCompetition());
        assertEquals("Competition Technologist", model.getLeadTechnologist());
        assertEquals(competition.getAssessorAcceptsDate(), model.getAcceptDeadline());
        assertEquals(competition.getAssessorDeadlineDate(), model.getSubmitDeadline());
        assertEquals(expectedSubmitted, model.getSubmitted());
        assertEquals(expectedOutstanding, model.getOutstanding());
        assertFalse(model.isSubmitVisible());
    }

    @Test
    public void competitionDashboard_empty() throws Exception {
        Long userId = 1L;

        UserResource leadTechnologist = buildTestLeadTechnologist();
        CompetitionResource competition = buildTestCompetition(leadTechnologist.getId());

        when(competitionService.getById(competition.getId())).thenReturn(competition);
        when(userService.findById(leadTechnologist.getId())).thenReturn(leadTechnologist);
        when(assessmentService.getByUserAndCompetition(userId, competition.getId())).thenReturn(emptyList());

        MvcResult result = mockMvc.perform(get("/assessor/dashboard/competition/{competitionId}", competition.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-competition-dashboard"))
                .andReturn();

        InOrder inOrder = inOrder(competitionService, userService, assessmentService);
        inOrder.verify(competitionService).getById(competition.getId());
        inOrder.verify(userService).findById(leadTechnologist.getId());
        inOrder.verify(assessmentService).getByUserAndCompetition(userId, competition.getId());
        inOrder.verifyNoMoreInteractions();

        verifyZeroInteractions(applicationService);
        verifyZeroInteractions(processRoleService);
        verifyZeroInteractions(organisationRestService);

        AssessorCompetitionDashboardViewModel model = (AssessorCompetitionDashboardViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(competition.getName(), model.getCompetitionTitle());
        assertEquals("Juggling Craziness (CRD3359)", model.getCompetition());
        assertEquals("Competition Technologist", model.getLeadTechnologist());
        assertEquals(competition.getAssessorAcceptsDate(), model.getAcceptDeadline());
        assertEquals(competition.getAssessorDeadlineDate(), model.getSubmitDeadline());
        assertTrue(model.getSubmitted().isEmpty());
        assertTrue(model.getOutstanding().isEmpty());
        assertFalse(model.isSubmitVisible());
    }

    @Test
    public void submitAssessments() throws Exception {
        List<Long> assessmentIds = asList(1L, 2L);
        UserResource leadTechnologist = buildTestLeadTechnologist();
        CompetitionResource competition = buildTestCompetition(leadTechnologist.getId());

        when(competitionService.getById(competition.getId())).thenReturn(competition);
        when(userService.findById(leadTechnologist.getId())).thenReturn(leadTechnologist);
        when(assessmentService.submitAssessments(assessmentIds)).thenReturn(serviceSuccess());

        MvcResult result = mockMvc.perform(post("/assessor/dashboard/competition/{competitionId}", competition.getId())
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("assessmentIds[0]", "1")
                .param("assessmentIds[1]", "2"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-competition-dashboard"))
                .andReturn();

        AssessorCompetitionDashboardAssessmentForm form = (AssessorCompetitionDashboardAssessmentForm) result.getModelAndView().getModel().get("form");
        assertEquals(2, form.getAssessmentIds().size());
        assertEquals(1L, form.getAssessmentIds().get(0).longValue());
        assertEquals(2L, form.getAssessmentIds().get(1).longValue());

        verify(assessmentService, times(1)).submitAssessments(assessmentIds);
    }

    @Test
    public void submitAssessments_invalid() throws Exception {
        UserResource leadTechnologist = buildTestLeadTechnologist();
        CompetitionResource competition = buildTestCompetition(leadTechnologist.getId());

        when(competitionService.getById(competition.getId())).thenReturn(competition);
        when(userService.findById(leadTechnologist.getId())).thenReturn(leadTechnologist);
        when(assessmentService.submitAssessments(emptyList())).thenReturn(serviceFailure(new Error("TEST", null)));

        MvcResult result = mockMvc.perform(post("/assessor/dashboard/competition/{competitionId}", competition.getId())
                .contentType(APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("model"))
                .andExpect(model().attributeHasFieldErrors("form", "assessmentIds"))
                .andExpect(view().name("assessor-competition-dashboard"))
                .andReturn();

        AssessorCompetitionDashboardAssessmentForm form = (AssessorCompetitionDashboardAssessmentForm) result.getModelAndView().getModel().get("form");
        assertNull(form.getAssessmentIds());

        verify(assessmentService, never()).submitAssessments(emptyList());

        BindingResult bindingResult = form.getBindingResult();
        assertEquals(1, bindingResult.getErrorCount());
        assertEquals("Please select at least one assessment to submit", bindingResult.getFieldError("assessmentIds").getDefaultMessage());
    }

    @Test
    public void submitAssessments_serviceFailure() throws Exception {
        List<Long> assessmentIds = asList(1L, 2L);
        UserResource leadTechnologist = buildTestLeadTechnologist();
        CompetitionResource competition = buildTestCompetition(leadTechnologist.getId());

        when(competitionService.getById(competition.getId())).thenReturn(competition);
        when(userService.findById(leadTechnologist.getId())).thenReturn(leadTechnologist);
        when(assessmentService.submitAssessments(assessmentIds)).thenReturn(serviceFailure(new Error("Test Error", null)));

        MvcResult result = mockMvc.perform(post("/assessor/dashboard/competition/{competitionId}", competition.getId())
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("assessmentIds[0]", "1")
                .param("assessmentIds[1]", "2"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-competition-dashboard"))
                .andReturn();

        AssessorCompetitionDashboardAssessmentForm form = (AssessorCompetitionDashboardAssessmentForm) result.getModelAndView().getModel().get("form");
        assertEquals(assessmentIds, form.getAssessmentIds());

        verify(assessmentService, times(1)).submitAssessments(assessmentIds);

        BindingResult bindingResult = form.getBindingResult();
        assertEquals(1, bindingResult.getGlobalErrorCount());
        assertEquals(1, bindingResult.getErrorCount());
        assertEquals(0, bindingResult.getFieldErrorCount());
        assertEquals("Test Error", bindingResult.getGlobalError().getCode());
    }

    private UserResource buildTestLeadTechnologist() {
        return newUserResource()
                .withFirstName("Competition")
                .withLastName("Technologist")
                .build();
    }

    private CompetitionResource buildTestCompetition(Long leadTechnologistId) {
        LocalDateTime assessorAcceptsDate = LocalDateTime.now().minusDays(2);
        LocalDateTime assessorDeadlineDate = LocalDateTime.now().plusDays(4);

        return newCompetitionResource()
                .withName("Juggling Craziness")
                .withDescription("Juggling Craziness (CRD3359)")
                .withLeadTechnologist(leadTechnologistId)
                .withAssessorAcceptsDate(assessorAcceptsDate)
                .withAssessorDeadlineDate(assessorDeadlineDate)
                .build();
    }

    private List<ApplicationResource> buildTestApplications() {
        return newApplicationResource()
                .withId(11L, 12L, 13L, 14L)
                .withName("Juggling is fun", "Juggling is very fun", "Juggling is not fun", "Juggling is word that sounds funny to say")
                .build(4);
    }

    private RoleResource buildLeadApplicantRole() {
        return newRoleResource().withType(UserRoleType.LEADAPPLICANT).build();
    }

    private List<OrganisationResource> buildTestOrganisations() {
        return newOrganisationResource()
                .withId(1L, 2L, 3L, 4L)
                .withName("The Best Juggling Company", "Juggle Ltd", "Jugglez Ltd", "Mo Juggling Mo Problems Ltd")
                .build(4);
    }
}