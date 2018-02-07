package org.innovateuk.ifs.assessment.dashboard.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.assessment.dashboard.populator.AssessorCompetitionForPanelDashboardModelPopulator;
import org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorCompetitionForPanelDashboardApplicationViewModel;
import org.innovateuk.ifs.assessment.dashboard.viewmodel.AssessorCompetitionForPanelDashboardViewModel;
import org.innovateuk.ifs.assessment.review.resource.AssessmentReviewResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.RoleResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
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
import static org.innovateuk.ifs.assessment.builder.AssessmentReviewResourceBuilder.newAssessmentReviewResource;
import static org.innovateuk.ifs.assessment.review.resource.AssessmentReviewState.*;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessorCompetitionForPanelDashboardControllerTest extends BaseControllerMockMVCTest<AssessorCompetitionForPanelDashboardController> {

    @Spy
    @InjectMocks
    private AssessorCompetitionForPanelDashboardModelPopulator assessorCompetitionForPanelDashboardModelPopulator;

    @Override
    protected AssessorCompetitionForPanelDashboardController supplyControllerUnderTest() {
        return new AssessorCompetitionForPanelDashboardController();
    }

    @Test
    public void competitionForPanelDashboard() throws Exception {
        Long userId = 1L;

        CompetitionResource competition = buildTestCompetition();
        List<ApplicationResource> applications = buildTestApplications();

        List<AssessmentReviewResource> assessmentReviews = newAssessmentReviewResource()
                .withId(1L, 2L, 3L, 4L)
                .withApplication(applications.get(0).getId(), applications.get(1).getId(), applications.get(2).getId(), applications.get(3).getId())
                .withCompetition(competition.getId())
                .withActivityState(PENDING, ACCEPTED, REJECTED, CONFLICT_OF_INTEREST)
                .build(4);

        RoleResource role = buildLeadApplicantRole();
        List<OrganisationResource> organisations = buildTestOrganisations();
        List<ProcessRoleResource> participants = newProcessRoleResource()
                .withRole(role)
                .withOrganisation(organisations.get(0).getId(), organisations.get(1).getId(), organisations.get(2).getId(), organisations.get(3).getId())
                .build(4);

        when(competitionService.getById(competition.getId())).thenReturn(competition);
        when(assessmentPanelRestService.getAssessmentReviews(userId, competition.getId())).thenReturn(restSuccess(assessmentReviews));
        applications.forEach(application -> when(applicationService.getById(application.getId())).thenReturn(application));
        when(processRoleService.findProcessRolesByApplicationId(applications.get(0).getId())).thenReturn(asList(participants.get(0)));
        when(processRoleService.findProcessRolesByApplicationId(applications.get(1).getId())).thenReturn(asList(participants.get(1)));
        when(processRoleService.findProcessRolesByApplicationId(applications.get(2).getId())).thenReturn(asList(participants.get(2)));
        when(processRoleService.findProcessRolesByApplicationId(applications.get(3).getId())).thenReturn(asList(participants.get(3)));

        organisations.forEach(organisation -> when(organisationRestService.getOrganisationById(organisation.getId())).thenReturn(restSuccess(organisation)));

        MvcResult result = mockMvc.perform(get("/assessor/dashboard/competition/{competitionId}/panel", competition.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-competition-for-panel-dashboard"))
                .andReturn();

        InOrder inOrder = inOrder(competitionService, assessmentPanelRestService, applicationService, processRoleService, organisationRestService);
        inOrder.verify(competitionService).getById(competition.getId());
        inOrder.verify(assessmentPanelRestService).getAssessmentReviews(userId, competition.getId());

        assessmentReviews.forEach(assessmentReview -> {
            inOrder.verify(applicationService).getById(assessmentReview.getApplication());
            inOrder.verify(processRoleService).findProcessRolesByApplicationId(assessmentReview.getApplication());
            inOrder.verify(organisationRestService).getOrganisationById(isA(Long.class));
        });

        inOrder.verifyNoMoreInteractions();

        List<AssessorCompetitionForPanelDashboardApplicationViewModel> expectedReviews = asList(
                new AssessorCompetitionForPanelDashboardApplicationViewModel(applications.get(0).getId(), assessmentReviews.get(0).getId(), applications.get(0).getName(), organisations.get(0).getName(), assessmentReviews.get(0).getAssessmentReviewState()),
                new AssessorCompetitionForPanelDashboardApplicationViewModel(applications.get(1).getId(), assessmentReviews.get(1).getId(), applications.get(1).getName(), organisations.get(1).getName(), assessmentReviews.get(1).getAssessmentReviewState()),
                new AssessorCompetitionForPanelDashboardApplicationViewModel(applications.get(2).getId(), assessmentReviews.get(2).getId(), applications.get(2).getName(), organisations.get(2).getName(), assessmentReviews.get(2).getAssessmentReviewState()),
                new AssessorCompetitionForPanelDashboardApplicationViewModel(applications.get(3).getId(), assessmentReviews.get(3).getId(), applications.get(3).getName(), organisations.get(3).getName(), assessmentReviews.get(3).getAssessmentReviewState())
        );

        AssessorCompetitionForPanelDashboardViewModel model = (AssessorCompetitionForPanelDashboardViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(competition.getName(), model.getCompetitionTitle());
        assertEquals("Innovation Lead", model.getLeadTechnologist());
        assertEquals(competition.getFundersPanelDate(), model.getPanelDate());
        assertEquals(expectedReviews, model.getApplications());
    }

    @Test
    public void competitionDashboard_empty() throws Exception {
        Long userId = 1L;

        CompetitionResource competition = buildTestCompetition();

        when(competitionService.getById(competition.getId())).thenReturn(competition);
        when(assessmentPanelRestService.getAssessmentReviews(userId, competition.getId())).thenReturn(restSuccess(emptyList()));

        MvcResult result = mockMvc.perform(get("/assessor/dashboard/competition/{competitionId}/panel", competition.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-competition-for-panel-dashboard"))
                .andReturn();

        InOrder inOrder = inOrder(competitionService, assessmentPanelRestService);
        inOrder.verify(competitionService).getById(competition.getId());
        inOrder.verify(assessmentPanelRestService).getAssessmentReviews(userId, competition.getId());
        inOrder.verifyNoMoreInteractions();

        verifyZeroInteractions(applicationService);
        verifyZeroInteractions(processRoleService);
        verifyZeroInteractions(organisationRestService);

        AssessorCompetitionForPanelDashboardViewModel model = (AssessorCompetitionForPanelDashboardViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(competition.getName(), model.getCompetitionTitle());
        assertEquals("Innovation Lead", model.getLeadTechnologist());
        assertEquals(competition.getFundersPanelDate(), model.getPanelDate());
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
