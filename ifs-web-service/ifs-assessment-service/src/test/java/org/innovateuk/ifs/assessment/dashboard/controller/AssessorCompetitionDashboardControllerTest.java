package org.innovateuk.ifs.assessment.dashboard.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.assessment.dashboard.controller.AssessorCompetitionDashboardController;
import org.innovateuk.ifs.assessment.dashboard.form.AssessorCompetitionDashboardAssessmentForm;
import org.innovateuk.ifs.assessment.dashboard.populator.AssessorCompetitionDashboardModelPopulator;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessmentTotalScoreResource;
import org.innovateuk.ifs.assessment.service.AssessmentService;
import org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorCompetitionDashboardApplicationViewModel;
import org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorCompetitionDashboardViewModel;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
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

import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static junit.framework.TestCase.assertEquals;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentFundingDecisionOutcomeResourceBuilder.newAssessmentFundingDecisionOutcomeResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentTotalScoreResourceBuilder.newAssessmentTotalScoreResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.*;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
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

        CompetitionResource competition = buildTestCompetition();
        List<ApplicationResource> applications = buildTestApplications();

        List<AssessmentResource> assessments = newAssessmentResource()
                .withId(1L, 2L, 3L, 4L)
                .withApplication(applications.get(0).getId(), applications.get(1).getId(), applications.get(2).getId(), applications.get(3).getId())
                .withCompetition(competition.getId())
                .withActivityState(PENDING, ACCEPTED, READY_TO_SUBMIT, SUBMITTED)
                .withFundingDecision(
                        null,
                        null,
                        newAssessmentFundingDecisionOutcomeResource().withFundingConfirmation(false).build(),
                        newAssessmentFundingDecisionOutcomeResource().withFundingConfirmation(true).build()
                )
                .build(4);

        List<AssessmentTotalScoreResource> totalScores = newAssessmentTotalScoreResource()
                .withTotalScoreGiven(50, 55)
                .withTotalScorePossible(100, 100)
                .build(2);

        RoleResource role = buildLeadApplicantRole();
        List<OrganisationResource> organisations = buildTestOrganisations();
        List<ProcessRoleResource> participants = newProcessRoleResource()
                .withRole(role)
                .withOrganisation(organisations.get(0).getId(), organisations.get(1).getId(), organisations.get(2).getId(), organisations.get(3).getId())
                .build(4);

        when(competitionService.getById(competition.getId())).thenReturn(competition);
        when(assessmentService.getByUserAndCompetition(userId, competition.getId())).thenReturn(assessments);
        applications.forEach(application -> when(applicationService.getById(application.getId())).thenReturn(application));
        when(processRoleService.findProcessRolesByApplicationId(applications.get(0).getId())).thenReturn(asList(participants.get(0)));
        when(processRoleService.findProcessRolesByApplicationId(applications.get(1).getId())).thenReturn(asList(participants.get(1)));
        when(processRoleService.findProcessRolesByApplicationId(applications.get(2).getId())).thenReturn(asList(participants.get(2)));
        when(processRoleService.findProcessRolesByApplicationId(applications.get(3).getId())).thenReturn(asList(participants.get(3)));
        when(assessmentService.getTotalScore(assessments.get(2).getId())).thenReturn(totalScores.get(0));
        when(assessmentService.getTotalScore(assessments.get(3).getId())).thenReturn(totalScores.get(1));

        organisations.forEach(organisation -> when(organisationRestService.getOrganisationById(organisation.getId())).thenReturn(restSuccess(organisation)));

        MvcResult result = mockMvc.perform(get("/assessor/dashboard/competition/{competitionId}", competition.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-competition-dashboard"))
                .andReturn();

        InOrder inOrder = inOrder(competitionService, assessmentService, applicationService, processRoleService, organisationRestService);
        inOrder.verify(competitionService).getById(competition.getId());
        inOrder.verify(assessmentService).getByUserAndCompetition(userId, competition.getId());

        assessments.forEach(assessment -> {
            inOrder.verify(applicationService).getById(assessment.getApplication());
            inOrder.verify(processRoleService).findProcessRolesByApplicationId(assessment.getApplication());
            inOrder.verify(organisationRestService).getOrganisationById(isA(Long.class));
            if (assessment.getAssessmentState() == SUBMITTED || assessment.getAssessmentState() == READY_TO_SUBMIT) {
                inOrder.verify(assessmentService).getTotalScore(assessment.getId());
            } else {
                inOrder.verify(assessmentService, never()).getTotalScore(assessment.getId());
            }
        });

        inOrder.verifyNoMoreInteractions();

        List<AssessorCompetitionDashboardApplicationViewModel> expectedSubmitted = singletonList(
                new AssessorCompetitionDashboardApplicationViewModel(applications.get(3).getId(), assessments.get(3).getId(), applications.get(3).getName(), organisations.get(3).getName(), assessments.get(3).getAssessmentState(), totalScores.get(1).getTotalScorePercentage(), true)
        );

        List<AssessorCompetitionDashboardApplicationViewModel> expectedOutstanding = asList(
                new AssessorCompetitionDashboardApplicationViewModel(applications.get(0).getId(), assessments.get(0).getId(), applications.get(0).getName(), organisations.get(0).getName(), assessments.get(0).getAssessmentState(), 0, null),
                new AssessorCompetitionDashboardApplicationViewModel(applications.get(1).getId(), assessments.get(1).getId(), applications.get(1).getName(), organisations.get(1).getName(), assessments.get(1).getAssessmentState(), 0, null),
                new AssessorCompetitionDashboardApplicationViewModel(applications.get(2).getId(), assessments.get(2).getId(), applications.get(2).getName(), organisations.get(2).getName(), assessments.get(2).getAssessmentState(), totalScores.get(0).getTotalScorePercentage(), false)
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

        CompetitionResource competition = buildTestCompetition();
        List<ApplicationResource> applications = buildTestApplications();

        List<AssessmentResource> assessments = newAssessmentResource()
                .withId(1L, 2L, 3L, 4L)
                .withApplication(applications.get(0).getId(), applications.get(1).getId(), applications.get(2).getId(), applications.get(3).getId())
                .withCompetition(competition.getId())
                .withActivityState(PENDING, ACCEPTED, OPEN, SUBMITTED)
                .withFundingDecision(
                        null,
                        null,
                        newAssessmentFundingDecisionOutcomeResource().withFundingConfirmation(false).build(),
                        newAssessmentFundingDecisionOutcomeResource().withFundingConfirmation(true).build()
                )
                .build(4);

        AssessmentTotalScoreResource totalScore = newAssessmentTotalScoreResource()
                .withTotalScoreGiven(55)
                .withTotalScorePossible(100)
                .build();

        RoleResource role = buildLeadApplicantRole();
        List<OrganisationResource> organisations = buildTestOrganisations();
        List<ProcessRoleResource> participants = newProcessRoleResource()
                .withRole(role)
                .withOrganisation(organisations.get(0).getId(), organisations.get(1).getId(), organisations.get(2).getId(), organisations.get(3).getId())
                .build(4);

        when(competitionService.getById(competition.getId())).thenReturn(competition);
        when(assessmentService.getByUserAndCompetition(userId, competition.getId())).thenReturn(assessments);
        applications.forEach(application -> when(applicationService.getById(application.getId())).thenReturn(application));
        when(processRoleService.findProcessRolesByApplicationId(applications.get(0).getId())).thenReturn(asList(participants.get(0)));
        when(processRoleService.findProcessRolesByApplicationId(applications.get(1).getId())).thenReturn(asList(participants.get(1)));
        when(processRoleService.findProcessRolesByApplicationId(applications.get(2).getId())).thenReturn(asList(participants.get(2)));
        when(processRoleService.findProcessRolesByApplicationId(applications.get(3).getId())).thenReturn(asList(participants.get(3)));
        when(assessmentService.getTotalScore(assessments.get(3).getId())).thenReturn(totalScore);

        organisations.forEach(organisation -> when(organisationRestService.getOrganisationById(organisation.getId())).thenReturn(restSuccess(organisation)));

        MvcResult result = mockMvc.perform(get("/assessor/dashboard/competition/{competitionId}", competition.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-competition-dashboard"))
                .andReturn();

        InOrder inOrder = inOrder(competitionService, assessmentService, applicationService, processRoleService, organisationRestService);
        inOrder.verify(competitionService).getById(competition.getId());
        inOrder.verify(assessmentService).getByUserAndCompetition(userId, competition.getId());

        assessments.forEach(assessment -> {
            inOrder.verify(applicationService).getById(assessment.getApplication());
            inOrder.verify(processRoleService).findProcessRolesByApplicationId(assessment.getApplication());
            inOrder.verify(organisationRestService).getOrganisationById(isA(Long.class));
            if (assessment.getAssessmentState() == SUBMITTED || assessment.getAssessmentState() == READY_TO_SUBMIT) {
                inOrder.verify(assessmentService).getTotalScore(assessment.getId());
            } else {
                inOrder.verify(assessmentService, never()).getTotalScore(assessment.getId());
            }
        });

        inOrder.verifyNoMoreInteractions();

        List<AssessorCompetitionDashboardApplicationViewModel> expectedSubmitted = singletonList(
                new AssessorCompetitionDashboardApplicationViewModel(applications.get(3).getId(), assessments.get(3).getId(), applications.get(3).getName(), organisations.get(3).getName(), assessments.get(3).getAssessmentState(), totalScore.getTotalScorePercentage(), true)
        );

        List<AssessorCompetitionDashboardApplicationViewModel> expectedOutstanding = asList(
                new AssessorCompetitionDashboardApplicationViewModel(applications.get(0).getId(), assessments.get(0).getId(), applications.get(0).getName(), organisations.get(0).getName(), assessments.get(0).getAssessmentState(), 0, null),
                new AssessorCompetitionDashboardApplicationViewModel(applications.get(1).getId(), assessments.get(1).getId(), applications.get(1).getName(), organisations.get(1).getName(), assessments.get(1).getAssessmentState(), 0, null),
                new AssessorCompetitionDashboardApplicationViewModel(applications.get(2).getId(), assessments.get(2).getId(), applications.get(2).getName(), organisations.get(2).getName(), assessments.get(2).getAssessmentState(), 0, false)
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

        CompetitionResource competition = buildTestCompetition();

        when(competitionService.getById(competition.getId())).thenReturn(competition);
        when(assessmentService.getByUserAndCompetition(userId, competition.getId())).thenReturn(emptyList());

        MvcResult result = mockMvc.perform(get("/assessor/dashboard/competition/{competitionId}", competition.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-competition-dashboard"))
                .andReturn();

        InOrder inOrder = inOrder(competitionService, assessmentService);
        inOrder.verify(competitionService).getById(competition.getId());
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
        CompetitionResource competition = buildTestCompetition();

        when(competitionService.getById(competition.getId())).thenReturn(competition);
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
        CompetitionResource competition = buildTestCompetition();

        when(competitionService.getById(competition.getId())).thenReturn(competition);
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
        assertEquals("Please select at least one assessment to submit.", bindingResult.getFieldError("assessmentIds").getDefaultMessage());
    }

    @Test
    public void submitAssessments_serviceFailure() throws Exception {
        List<Long> assessmentIds = asList(1L, 2L);
        CompetitionResource competition = buildTestCompetition();

        when(competitionService.getById(competition.getId())).thenReturn(competition);
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

    private CompetitionResource buildTestCompetition() {
        ZonedDateTime assessorAcceptsDate = ZonedDateTime.now().minusDays(2);
        ZonedDateTime assessorDeadlineDate = ZonedDateTime.now().plusDays(4);

        return newCompetitionResource()
                .withName("Juggling Craziness")
                .withDescription("Juggling Craziness (CRD3359)")
                .withLeadTechnologist(2L)
                .withLeadTechnologistName("Competition Technologist")
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
