package org.innovateuk.ifs.assessment.dashboard.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.assessment.dashboard.populator.AssessorCompetitionForInterviewDashboardModelPopulator;
import org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorCompetitionForInterviewDashboardApplicationViewModel;
import org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorCompetitionForInterviewDashboardViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.interview.resource.InterviewResource;
import org.innovateuk.ifs.interview.service.InterviewAllocationRestService;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static junit.framework.TestCase.assertEquals;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.interview.builder.InterviewResourceBuilder.newInterviewResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessorCompetitionForInterviewDashboardControllerTest extends BaseControllerMockMVCTest<AssessorCompetitionForInterviewDashboardController> {

    @Spy
    @InjectMocks
    private AssessorCompetitionForInterviewDashboardModelPopulator assessorCompetitionForInterviewDashboardModelPopulator;

    @Mock
    private CompetitionService competitionService;

    @Mock
    private ProcessRoleService processRoleService;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private InterviewAllocationRestService interviewAllocationRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Override
    protected AssessorCompetitionForInterviewDashboardController supplyControllerUnderTest() {
        return new AssessorCompetitionForInterviewDashboardController();
    }

    @Test
    public void competitionForInterviewDashboard() throws Exception {
        long userId = 1L;

        CompetitionResource competition = buildTestCompetition();
        List<ApplicationResource> applications = buildTestApplications();

        List<InterviewResource> assessmentInterviews = newInterviewResource()
                .withId(1L, 2L, 3L, 4L)
                .withApplication(applications.get(0).getId(), applications.get(1).getId(), applications.get(2).getId(), applications.get(3).getId())
                .withCompetition(competition.getId())
                .build(4);

        Role role = Role.LEADAPPLICANT;
        List<OrganisationResource> organisations = buildTestOrganisations();
        List<ProcessRoleResource> participants = newProcessRoleResource()
                .withRole(role)
                .withOrganisation(organisations.get(0).getId(), organisations.get(1).getId(), organisations.get(2).getId(), organisations.get(3).getId())
                .build(4);

        when(competitionService.getById(competition.getId())).thenReturn(competition);
        when(interviewAllocationRestService.getAllocatedApplicationsByAssessorId(competition.getId(), userId)).thenReturn(restSuccess(assessmentInterviews));
        applications.forEach(application -> when(applicationService.getById(application.getId())).thenReturn(application));
        when(processRoleService.findProcessRolesByApplicationId(applications.get(0).getId())).thenReturn(asList(participants.get(0)));
        when(processRoleService.findProcessRolesByApplicationId(applications.get(1).getId())).thenReturn(asList(participants.get(1)));
        when(processRoleService.findProcessRolesByApplicationId(applications.get(2).getId())).thenReturn(asList(participants.get(2)));
        when(processRoleService.findProcessRolesByApplicationId(applications.get(3).getId())).thenReturn(asList(participants.get(3)));

        organisations.forEach(organisation -> when(organisationRestService.getOrganisationById(organisation.getId())).thenReturn(restSuccess(organisation)));

        MvcResult result = mockMvc.perform(get("/assessor/dashboard/competition/{competitionId}/interview", competition.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-interview-applications"))
                .andReturn();

        InOrder inOrder = inOrder(competitionService, interviewAllocationRestService, applicationService, processRoleService, organisationRestService);
        inOrder.verify(competitionService).getById(competition.getId());
        inOrder.verify(interviewAllocationRestService).getAllocatedApplicationsByAssessorId(competition.getId(), userId);

        assessmentInterviews.forEach(assessmentInterview -> {
            inOrder.verify(applicationService).getById(assessmentInterview.getApplication());
            inOrder.verify(processRoleService).findProcessRolesByApplicationId(assessmentInterview.getApplication());
            inOrder.verify(organisationRestService).getOrganisationById(isA(Long.class));
        });

        inOrder.verifyNoMoreInteractions();

        List<AssessorCompetitionForInterviewDashboardApplicationViewModel> expectedReviews = asList(
                new AssessorCompetitionForInterviewDashboardApplicationViewModel(applications.get(0).getId(), applications.get(0).getName(), organisations.get(0).getName()),
                new AssessorCompetitionForInterviewDashboardApplicationViewModel(applications.get(1).getId(), applications.get(1).getName(), organisations.get(1).getName()),
                new AssessorCompetitionForInterviewDashboardApplicationViewModel(applications.get(2).getId(), applications.get(2).getName(), organisations.get(2).getName()),
                new AssessorCompetitionForInterviewDashboardApplicationViewModel(applications.get(3).getId(), applications.get(3).getName(), organisations.get(3).getName())
        );

        AssessorCompetitionForInterviewDashboardViewModel model = (AssessorCompetitionForInterviewDashboardViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(competition.getName(), model.getCompetitionTitle());
        assertEquals("Innovation Lead", model.getLeadTechnologist());
        assertEquals(expectedReviews, model.getApplications());
    }

    @Test
    public void competitionDashboard_empty() throws Exception {
        long userId = 1L;

        CompetitionResource competition = buildTestCompetition();

        when(competitionService.getById(competition.getId())).thenReturn(competition);
        when(interviewAllocationRestService.getAllocatedApplicationsByAssessorId(competition.getId(), userId)).thenReturn(restSuccess(emptyList()));

        MvcResult result = mockMvc.perform(get("/assessor/dashboard/competition/{competitionId}/interview", competition.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-interview-applications"))
                .andReturn();

        InOrder inOrder = inOrder(competitionService, interviewAllocationRestService);
        inOrder.verify(competitionService).getById(competition.getId());
        inOrder.verify(interviewAllocationRestService).getAllocatedApplicationsByAssessorId(competition.getId(), userId);
        inOrder.verifyNoMoreInteractions();

        verifyZeroInteractions(applicationService);
        verifyZeroInteractions(processRoleService);
        verifyZeroInteractions(organisationRestService);

        AssessorCompetitionForInterviewDashboardViewModel model = (AssessorCompetitionForInterviewDashboardViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(competition.getName(), model.getCompetitionTitle());
        assertEquals("Innovation Lead", model.getLeadTechnologist());
        assertTrue(model.getApplications().isEmpty());
    }

    private CompetitionResource buildTestCompetition() {
        ZonedDateTime assessorAcceptsDate = ZonedDateTime.now().minusDays(2);
        ZonedDateTime assessorDeadlineDate = ZonedDateTime.now().plusDays(4);

        return newCompetitionResource()
                .withName("Juggling Craziness")
                .withLeadTechnologist(2L)
                .withLeadTechnologistName("Innovation Lead")
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

    private List<OrganisationResource> buildTestOrganisations() {
        return newOrganisationResource()
                .withId(1L, 2L, 3L, 4L)
                .withName("The Best Juggling Company", "Juggle Ltd", "Jugglez Ltd", "Mo Juggling Mo Problems Ltd")
                .build(4);
    }
}
