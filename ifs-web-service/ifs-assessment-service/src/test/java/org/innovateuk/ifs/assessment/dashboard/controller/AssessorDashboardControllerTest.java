package org.innovateuk.ifs.assessment.dashboard.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.dashboard.populator.AssessorDashboardModelPopulator;
import org.innovateuk.ifs.assessment.dashboard.viewmodel.*;
import org.innovateuk.ifs.assessment.profile.viewmodel.AssessorProfileStatusViewModel;
import org.innovateuk.ifs.assessment.service.CompetitionParticipantRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.invite.resource.AssessmentReviewPanelInviteResource;
import org.innovateuk.ifs.invite.resource.AssessmentReviewPanelParticipantResource;
import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantResource;
import org.innovateuk.ifs.user.resource.UserProfileStatusResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MvcResult;

import java.time.*;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.ZoneId.systemDefault;
import static java.time.ZonedDateTime.now;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.assessment.builder.AssessmentReviewPanelInviteResourceBuilder.newAssessmentReviewPanelInviteResource;
import static org.innovateuk.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.*;
import static org.innovateuk.ifs.invite.builder.AssessmentReviewPanelParticipantResourceBuilder.newAssessmentReviewPanelParticipantResource;
import static org.innovateuk.ifs.invite.builder.CompetitionParticipantResourceBuilder.newCompetitionParticipantResource;
import static org.innovateuk.ifs.invite.resource.CompetitionParticipantRoleResource.ASSESSOR;
import static org.innovateuk.ifs.invite.resource.CompetitionParticipantRoleResource.PANEL_ASSESSOR;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.ACCEPTED;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.PENDING;
import static org.innovateuk.ifs.user.builder.UserProfileStatusResourceBuilder.newUserProfileStatusResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessorDashboardControllerTest extends BaseControllerMockMVCTest<AssessorDashboardController> {

    @Spy
    @InjectMocks
    private AssessorDashboardModelPopulator assessorDashboardModelPopulator;

    @Mock
    private CompetitionParticipantRestService competitionParticipantRestService;

    @Override
    protected AssessorDashboardController supplyControllerUnderTest() {
        return new AssessorDashboardController();
    }

    @Before
    public void setUp() {
        super.setUp();
        UserResource user = newUserResource().withId(3L).withFirstName("test").withLastName("name").build();
        setLoggedInUser(user);
    }

    @Test
    public void dashboard() throws Exception {
        final ZonedDateTime now = now();
        final LocalDateTime time = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 23,59,59);
        Instant instant = time.toInstant(ZoneOffset.UTC);

        Clock clock = Clock.fixed(instant, systemDefault());

        CompetitionResource competitionResource = newCompetitionResource()
                .withFundersPanelDate(now.plusDays(30))
                .build();

        CompetitionParticipantResource participant = newCompetitionParticipantResource()
                .withCompetitionParticipantRole(ASSESSOR)
                .withStatus(ACCEPTED)
                .withUser(3L)
                .withCompetition(competitionResource.getId())
                .withCompetitionName("Juggling Craziness")
                .withCompetitionStatus(IN_ASSESSMENT)
                .withAssessorAcceptsDate(now.minusDays(2))
                .withAssessorDeadlineDate(now.plusDays(4))
                .withSubmittedAssessments(1L)
                .withTotalAssessments(3L)
                .withPendingAssessments(1L)
                .build();
        ReflectionTestUtils.setField(participant, "clock", clock, Clock.class);
        UserProfileStatusResource profileStatusResource = newUserProfileStatusResource()
                .withSkillsComplete(true)
                .withAffliliationsComplete(false)
                .withAgreementComplete(false)
                .build();

        AssessmentReviewPanelInviteResource invite = newAssessmentReviewPanelInviteResource()
                .withInviteHash("")
                .withCompetitionId(competitionResource.getId())
                .withCompetitionName("Juggling Craziness")
                .build();

        AssessmentReviewPanelParticipantResource assessmentReviewPanelParticipantResource = newAssessmentReviewPanelParticipantResource()
                .withCompetition(competitionResource.getId())
                .withCompetitionName("Juggling Craziness")
                .withCompetitionParticipantRole(PANEL_ASSESSOR)
                .withStatus(PENDING)
                .withUser(3L)
                .withInvite(invite)
                .build();


        when(competitionParticipantRestService.getParticipants(3L, ASSESSOR)).thenReturn(restSuccess(singletonList(participant)));
        when(profileRestService.getUserProfileStatus(3L)).thenReturn(restSuccess(profileStatusResource));
        when(assessmentPanelInviteRestService.getAllInvitesByUser(3L)).thenReturn(restSuccess(singletonList(assessmentReviewPanelParticipantResource)));
        when(competitionService.getById(competitionResource.getId())).thenReturn(competitionResource);

        MvcResult result = mockMvc.perform(get("/assessor/dashboard"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-dashboard"))
                .andReturn();

        AssessorDashboardViewModel model = (AssessorDashboardViewModel) result.getModelAndView().getModel().get("model");

        List<AssessorDashboardActiveCompetitionViewModel> expectedActiveCompetitions = singletonList(
                new AssessorDashboardActiveCompetitionViewModel(competitionResource.getId(), "Juggling Craziness", 1, 3, 1,
                        now.plusDays(4).toLocalDate(),
                        3,
                        50
                )
        );
        AssessorProfileStatusViewModel expectedAssessorProfileStatusViewModel = new AssessorProfileStatusViewModel(profileStatusResource);

        AssessorDashboardAssessmentPanelInviteViewModel expectedAssessmentPanelInviteViewModel =
                new AssessorDashboardAssessmentPanelInviteViewModel("Juggling Craziness", competitionResource.getId(), "");

        assertTrue(model.getPendingInvites().isEmpty());
        assertEquals(expectedActiveCompetitions, model.getActiveCompetitions());
        assertEquals(expectedAssessorProfileStatusViewModel, model.getProfileStatus());
        assertTrue(model.getUpcomingCompetitions().isEmpty());
        assertFalse(model.getAssessmentPanelInvites().isEmpty());
        assertEquals(model.getAssessmentPanelInvites().get(0).getCompetitionId(), singletonList(expectedAssessmentPanelInviteViewModel).get(0).getCompetitionId());
        assertEquals(model.getAssessmentPanelInvites().get(0).getCompetitionName(), singletonList(expectedAssessmentPanelInviteViewModel).get(0).getCompetitionName());
        assertEquals(model.getAssessmentPanelInvites().get(0).getInviteHash(), singletonList(expectedAssessmentPanelInviteViewModel).get(0).getInviteHash());
        assertEquals(model.getAssessmentPanelInvites(), singletonList(expectedAssessmentPanelInviteViewModel));
    }

    @Test
    public void dashboard_activeStartsToday() throws Exception {
        ZonedDateTime now = now();
        Clock clock = Clock.fixed(now.toInstant(), systemDefault());
        CompetitionParticipantResource participant = newCompetitionParticipantResource()
                .withCompetitionParticipantRole(ASSESSOR)
                .withStatus(ACCEPTED)
                .withUser(3L)
                .withCompetition(2L)
                .withCompetitionName("Juggling Craziness")
                .withAssessorAcceptsDate(now.minusDays(1))
                .withAssessorDeadlineDate(now.plusDays(5))
                .withSubmittedAssessments(1L)
                .withTotalAssessments(3L)
                .withPendingAssessments(2L)
                .withCompetitionStatus(IN_ASSESSMENT)
                .build();
        ReflectionTestUtils.setField(participant, "clock", clock, Clock.class);
        UserProfileStatusResource profileStatusResource = newUserProfileStatusResource()
                .withSkillsComplete(false)
                .withAffliliationsComplete(true)
                .withAgreementComplete(false)
                .build();

        AssessmentReviewPanelParticipantResource assessmentReviewPanelParticipantResource = newAssessmentReviewPanelParticipantResource()
                .withCompetitionName("Juggling Craziness")
                .build();

        when(competitionParticipantRestService.getParticipants(3L, ASSESSOR)).thenReturn(restSuccess(singletonList(participant)));
        when(profileRestService.getUserProfileStatus(3L)).thenReturn(restSuccess(profileStatusResource));
        when(assessmentPanelInviteRestService.getAllInvitesByUser(3L)).thenReturn(restSuccess(singletonList(assessmentReviewPanelParticipantResource)));

        MvcResult result = mockMvc.perform(get("/assessor/dashboard"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-dashboard"))
                .andReturn();

        AssessorDashboardViewModel model = (AssessorDashboardViewModel) result.getModelAndView().getModel().get("model");

        List<AssessorDashboardActiveCompetitionViewModel> expectedActiveCompetitions = singletonList(
                new AssessorDashboardActiveCompetitionViewModel(2L, "Juggling Craziness", 1, 3, 2,
                        now.plusDays(5).toLocalDate(),
                        5,
                        16
                )
        );
        AssessorProfileStatusViewModel expectedAssessorProfileStatusViewModel = new AssessorProfileStatusViewModel(profileStatusResource);

        assertTrue(model.getPendingInvites().isEmpty());
        assertEquals(expectedActiveCompetitions, model.getActiveCompetitions());
        assertEquals(expectedAssessorProfileStatusViewModel, model.getProfileStatus());
        assertTrue(model.getUpcomingCompetitions().isEmpty());
    }

    @Test
    public void dashboard_activeEndsToday() throws Exception {
        CompetitionParticipantResource participant = newCompetitionParticipantResource()
                .withCompetitionParticipantRole(ASSESSOR)
                .withStatus(ACCEPTED)
                .withUser(3L)
                .withCompetition(2L)
                .withCompetitionName("Juggling Craziness")
                .withAssessorAcceptsDate(now().minusDays(2))
                .withAssessorDeadlineDate(now().plusDays(0))
                .withCompetitionStatus(IN_ASSESSMENT)
                .build();
        UserProfileStatusResource profileStatusResource = newUserProfileStatusResource()
                .withSkillsComplete(false)
                .withAffliliationsComplete(false)
                .withAgreementComplete(true)
                .build();

        AssessmentReviewPanelParticipantResource assessmentReviewPanelParticipantResource = newAssessmentReviewPanelParticipantResource()
                .withCompetitionName("Juggling Craziness")
                .build();

        when(competitionParticipantRestService.getParticipants(3L, ASSESSOR)).thenReturn(restSuccess(singletonList(participant)));
        when(profileRestService.getUserProfileStatus(3L)).thenReturn(restSuccess(profileStatusResource));
        when(assessmentPanelInviteRestService.getAllInvitesByUser(3L)).thenReturn(restSuccess(singletonList(assessmentReviewPanelParticipantResource)));

        MvcResult result = mockMvc.perform(get("/assessor/dashboard"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-dashboard"))
                .andReturn();

        AssessorDashboardViewModel model = (AssessorDashboardViewModel) result.getModelAndView().getModel().get("model");

        List<AssessorDashboardActiveCompetitionViewModel> expectedActiveCompetitions = singletonList(
                new AssessorDashboardActiveCompetitionViewModel(2L, "Juggling Craziness", 0, 0, 0,
                        ZonedDateTime.now().plusDays(0).toLocalDate(),
                        0,
                        100
                )
        );
        AssessorProfileStatusViewModel expectedAssessorProfileStatusViewModel = new AssessorProfileStatusViewModel(profileStatusResource);

        assertTrue(model.getPendingInvites().isEmpty());
        assertEquals(expectedActiveCompetitions, model.getActiveCompetitions());
        assertEquals(expectedAssessorProfileStatusViewModel, model.getProfileStatus());
        assertTrue(model.getUpcomingCompetitions().isEmpty());
    }

    @Test
    public void dashboard_fundersPanel() throws Exception {
        CompetitionParticipantResource participant = newCompetitionParticipantResource()
                .withCompetitionParticipantRole(ASSESSOR)
                .withStatus(ACCEPTED)
                .withUser(3L)
                .withCompetition(2L)
                .withCompetitionName("Juggling Craziness")
                .withAssessorAcceptsDate(now().minusDays(2))
                .withAssessorDeadlineDate(now().plusDays(0))
                .withCompetitionStatus(FUNDERS_PANEL)
                .build();
        UserProfileStatusResource profileStatusResource = newUserProfileStatusResource()
                .withSkillsComplete(false)
                .withAffliliationsComplete(false)
                .withAgreementComplete(true)
                .build();

        AssessmentReviewPanelParticipantResource assessmentReviewPanelParticipantResource = newAssessmentReviewPanelParticipantResource()
                .withCompetitionName("Juggling Craziness")
                .build();

        when(competitionParticipantRestService.getParticipants(3L, ASSESSOR)).thenReturn(restSuccess(singletonList(participant)));
        when(profileRestService.getUserProfileStatus(3L)).thenReturn(restSuccess(profileStatusResource));
        when(assessmentPanelInviteRestService.getAllInvitesByUser(3L)).thenReturn(restSuccess(singletonList(assessmentReviewPanelParticipantResource)));

        MvcResult result = mockMvc.perform(get("/assessor/dashboard"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-dashboard"))
                .andReturn();

        AssessorDashboardViewModel model = (AssessorDashboardViewModel) result.getModelAndView().getModel().get("model");
        AssessorProfileStatusViewModel expectedAssessorProfileStatusViewModel = new AssessorProfileStatusViewModel(profileStatusResource);

        assertTrue(model.getPendingInvites().isEmpty());
        assertTrue(model.getActiveCompetitions().isEmpty());
        assertEquals(expectedAssessorProfileStatusViewModel, model.getProfileStatus());
        assertTrue(model.getUpcomingCompetitions().isEmpty());
    }

    @Test
    public void dashboard_upcomingAssessments() throws Exception {
        CompetitionParticipantResource participant = newCompetitionParticipantResource()
                .withCompetitionParticipantRole(ASSESSOR)
                .withStatus(ACCEPTED)
                .withUser(3L)
                .withCompetition(2L)
                .withCompetitionName("Juggling Craziness")
                .withAssessorAcceptsDate(now().plusDays(1))
                .withAssessorDeadlineDate(now().plusDays(7))
                .withCompetitionStatus(CompetitionStatus.CLOSED)
                .build();
        UserProfileStatusResource profileStatusResource = newUserProfileStatusResource()
                .withSkillsComplete(true)
                .withAffliliationsComplete(true)
                .withAgreementComplete(false)
                .build();

        AssessmentReviewPanelParticipantResource assessmentReviewPanelParticipantResource = newAssessmentReviewPanelParticipantResource()
                .withCompetitionName("Juggling Craziness")
                .build();

        when(competitionParticipantRestService.getParticipants(3L, ASSESSOR)).thenReturn(restSuccess(singletonList(participant)));
        when(profileRestService.getUserProfileStatus(3L)).thenReturn(restSuccess(profileStatusResource));
        when(assessmentPanelInviteRestService.getAllInvitesByUser(3L)).thenReturn(restSuccess(singletonList(assessmentReviewPanelParticipantResource)));

        MvcResult result = mockMvc.perform(get("/assessor/dashboard"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-dashboard"))
                .andReturn();

        AssessorDashboardViewModel model = (AssessorDashboardViewModel) result.getModelAndView().getModel().get("model");

        List<AssessorDashboardUpcomingCompetitionViewModel> expectedUpcomingCompetitions = singletonList(
                new AssessorDashboardUpcomingCompetitionViewModel(
                        2L, "Juggling Craziness",
                        ZonedDateTime.now().plusDays(1).toLocalDate(),
                        ZonedDateTime.now().plusDays(7).toLocalDate()
                )
        );
        AssessorProfileStatusViewModel expectedAssessorProfileStatusViewModel = new AssessorProfileStatusViewModel(profileStatusResource);

        assertTrue(model.getPendingInvites().isEmpty());
        assertTrue(model.getActiveCompetitions().isEmpty());
        assertEquals(expectedAssessorProfileStatusViewModel, model.getProfileStatus());
        assertEquals(expectedUpcomingCompetitions, model.getUpcomingCompetitions());
    }

    @Test
    public void dashboard_pastAssessmentInAssessment() throws Exception {
        CompetitionParticipantResource participant = newCompetitionParticipantResource()
                .withCompetitionParticipantRole(ASSESSOR)
                .withStatus(ACCEPTED)
                .withUser(3L)
                .withCompetition(2L)
                .withCompetitionName("Juggling Craziness")
                .withAssessorAcceptsDate(now().minusDays(1))
                .withAssessorDeadlineDate(now().minusDays(0))
                .withCompetitionStatus(IN_ASSESSMENT)
                .build();
        UserProfileStatusResource profileStatusResource = newUserProfileStatusResource()
                .withSkillsComplete(true)
                .withAffliliationsComplete(false)
                .withAgreementComplete(true)
                .build();

        AssessmentReviewPanelParticipantResource assessmentReviewPanelParticipantResource = newAssessmentReviewPanelParticipantResource()
                .withCompetitionName("Juggling Craziness")
                .build();

        when(competitionParticipantRestService.getParticipants(3L, ASSESSOR)).thenReturn(restSuccess(singletonList(participant)));
        when(profileRestService.getUserProfileStatus(3L)).thenReturn(restSuccess(profileStatusResource));
        when(assessmentPanelInviteRestService.getAllInvitesByUser(3L)).thenReturn(restSuccess(singletonList(assessmentReviewPanelParticipantResource)));

        MvcResult result = mockMvc.perform(get("/assessor/dashboard"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-dashboard"))
                .andReturn();

        AssessorDashboardViewModel model = (AssessorDashboardViewModel) result.getModelAndView().getModel().get("model");

        List<AssessorDashboardActiveCompetitionViewModel> expectedActiveCompetitions = singletonList(
                new AssessorDashboardActiveCompetitionViewModel(2L, "Juggling Craziness", 0, 0, 0,
                        ZonedDateTime.now().plusDays(0).toLocalDate(),
                        0,
                        100
                )
        );
        AssessorProfileStatusViewModel expectedAssessorProfileStatusViewModel = new AssessorProfileStatusViewModel(profileStatusResource);

        assertTrue(model.getPendingInvites().isEmpty());
        assertEquals(expectedActiveCompetitions, model.getActiveCompetitions());
        assertEquals(expectedAssessorProfileStatusViewModel, model.getProfileStatus());
        assertTrue(model.getUpcomingCompetitions().isEmpty());
    }

    @Test
    public void dashboard_pendingInvites() throws Exception {
        List<CompetitionInviteResource> inviteResource = newCompetitionInviteResource()
                .withHash("inviteHash1", "inviteHash2")
                .build(2);

        List<CompetitionParticipantResource> participantResources = newCompetitionParticipantResource()
                .withInvite(inviteResource.get(0), inviteResource.get(1))
                .withCompetitionParticipantRole(ASSESSOR)
                .withStatus(PENDING)
                .withUser(3L)
                .withCompetition(1L, 2L)
                .withCompetitionName("Sustainable living models for the future", "Machine learning for transport infrastructure")
                .withAssessorAcceptsDate(now().plusDays(10), now().plusDays(5))
                .withAssessorDeadlineDate(now().plusDays(20), now().plusDays(15))
                .withCompetitionStatus(CLOSED, IN_ASSESSMENT)
                .build(2);

        UserProfileStatusResource profileStatusResource = newUserProfileStatusResource()
                .withSkillsComplete(true)
                .withAffliliationsComplete(true)
                .withAgreementComplete(true)
                .build();

        AssessmentReviewPanelParticipantResource assessmentReviewPanelParticipantResource = newAssessmentReviewPanelParticipantResource()
                .withCompetitionName("Juggling Craziness")
                .build();

        when(competitionParticipantRestService.getParticipants(3L, ASSESSOR)).thenReturn(restSuccess(participantResources));
        when(profileRestService.getUserProfileStatus(3L)).thenReturn(restSuccess(profileStatusResource));
        when(assessmentPanelInviteRestService.getAllInvitesByUser(3L)).thenReturn(restSuccess(singletonList(assessmentReviewPanelParticipantResource)));

        MvcResult result = mockMvc.perform(get("/assessor/dashboard"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-dashboard"))
                .andReturn();

        AssessorDashboardViewModel model = (AssessorDashboardViewModel) result.getModelAndView().getModel().get("model");

        List<AssessorDashboardPendingInviteViewModel> expectedPendingInvitesModel = participantResources.stream().map(competitionParticipantResource ->
                new AssessorDashboardPendingInviteViewModel(
                        competitionParticipantResource.getInvite().getHash(),
                        competitionParticipantResource.getCompetitionName(),
                        competitionParticipantResource.getAssessorAcceptsDate().toLocalDate(),
                        competitionParticipantResource.getAssessorDeadlineDate().toLocalDate())).collect(Collectors.toList());

        AssessorProfileStatusViewModel expectedAssessorProfileStatusViewModel = new AssessorProfileStatusViewModel(profileStatusResource);

        assertEquals(expectedPendingInvitesModel, model.getPendingInvites());
        assertTrue(model.getActiveCompetitions().isEmpty());
        assertEquals(expectedAssessorProfileStatusViewModel, model.getProfileStatus());
        assertTrue(model.getUpcomingCompetitions().isEmpty());
    }

    @Test
    public void dashboard_acceptedPanels() throws Exception {
        List<CompetitionInviteResource> inviteResources = newCompetitionInviteResource()
                .withHash("inviteHash1")
                .build(1);

        CompetitionResource competitionResource = newCompetitionResource()
                .withFundersPanelDate(now().plusDays(30))
                .build();

        List<CompetitionParticipantResource> participantResources = newCompetitionParticipantResource()
                .withInvite(inviteResources.get(0))
                .withCompetitionParticipantRole(PANEL_ASSESSOR)
                .withStatus(ACCEPTED)
                .withUser(3L)
                .withCompetition(competitionResource.getId())
                .withCompetitionName("Sustainable living models for the future")
                .withAssessorAcceptsDate(now().plusDays(10))
                .withAssessorDeadlineDate(now().plusDays(20))
                .withCompetitionStatus(FUNDERS_PANEL)
                .build(1);

        UserProfileStatusResource profileStatusResource = newUserProfileStatusResource()
                .withSkillsComplete(true)
                .withAffliliationsComplete(true)
                .withAgreementComplete(true)
                .build();

        AssessmentReviewPanelInviteResource invite = newAssessmentReviewPanelInviteResource()
                .withInviteHash("")
                .withCompetitionId(competitionResource.getId())
                .withCompetitionName("Juggling Craziness")
                .withPanelDate(now().plusDays(10))
                .build();

        AssessmentReviewPanelParticipantResource assessmentReviewPanelParticipantResource = newAssessmentReviewPanelParticipantResource()
                .withUser(3L)
                .withCompetition(competitionResource.getId())
                .withStatus(ACCEPTED)
                .withCompetitionParticipantRole(PANEL_ASSESSOR)
                .withAwaitingApplications(1L)
                .withCompetitionName("Juggling Craziness")
                .withInvite(invite)
                .build();


        when(competitionParticipantRestService.getParticipants(3L, ASSESSOR)).thenReturn(restSuccess(participantResources));
        when(profileRestService.getUserProfileStatus(3L)).thenReturn(restSuccess(profileStatusResource));
        when(assessmentPanelInviteRestService.getAllInvitesByUser(3L)).thenReturn(restSuccess(singletonList(assessmentReviewPanelParticipantResource)));
        when(competitionService.getById(competitionResource.getId())).thenReturn(competitionResource);

        MvcResult result = mockMvc.perform(get("/assessor/dashboard"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-dashboard"))
                .andReturn();

        AssessorDashboardViewModel model = (AssessorDashboardViewModel) result.getModelAndView().getModel().get("model");

        List<AssessorDashboardAssessmentPanelAcceptedViewModel> expectedPanelAcceptedModel =
                singletonList(new AssessorDashboardAssessmentPanelAcceptedViewModel(
                        assessmentReviewPanelParticipantResource.getCompetitionName(),
                        assessmentReviewPanelParticipantResource.getCompetitionId(),
                        assessmentReviewPanelParticipantResource.getInvite().getPanelDate().toLocalDate(),
                        assessmentReviewPanelParticipantResource.getInvite().getPanelDaysLeft(),
                        assessmentReviewPanelParticipantResource.getAwaitingApplications()));

        AssessorProfileStatusViewModel expectedAssessorProfileStatusViewModel = new AssessorProfileStatusViewModel(profileStatusResource);

        assertTrue(model.getAssessmentPanelInvites().isEmpty());
        assertFalse(model.getAssessmentPanelAccepted().isEmpty());
        assertTrue(model.getActiveCompetitions().isEmpty());
        assertEquals(expectedAssessorProfileStatusViewModel, model.getProfileStatus());
        assertEquals(expectedPanelAcceptedModel, model.getAssessmentPanelAccepted());
    }

    @Test
    public void getTermsAndConditions() throws Exception {
        mockMvc.perform(get("/assessor/terms-and-conditions"))
                .andExpect(status().isOk())
                .andExpect(view().name("terms-and-conditions"))
                .andReturn();
    }
}
