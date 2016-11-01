package com.worth.ifs.assessment.controller.dashboard;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.model.AssessorDashboardModelPopulator;
import com.worth.ifs.assessment.service.CompetitionParticipantRestService;
import com.worth.ifs.assessment.viewmodel.AssessorDashboardActiveCompetitionViewModel;
import com.worth.ifs.assessment.viewmodel.AssessorDashboardUpcomingCompetitionViewModel;
import com.worth.ifs.assessment.viewmodel.AssessorDashboardViewModel;
import com.worth.ifs.invite.resource.CompetitionParticipantResource;
import com.worth.ifs.invite.resource.CompetitionParticipantRoleResource;
import com.worth.ifs.invite.resource.ParticipantStatusResource;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;

import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.invite.builder.CompetitionParticipantResourceBuilder.newCompetitionParticipantResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
        CompetitionParticipantResource participant = newCompetitionParticipantResource()
                .withCompetitionParticipantRole(CompetitionParticipantRoleResource.ASSESSOR)
                .withStatus(ParticipantStatusResource.ACCEPTED)
                .withUser(3L)
                .withCompetition(2L)
                .withCompetitionName("Juggling Craziness")
                .withAssessorAcceptsDate(now().minusDays(2))
                .withAssessorDeadlineDate(now().plusDays(4))
                .build();

        when(competitionParticipantRestService.getParticipants(3L, CompetitionParticipantRoleResource.ASSESSOR, ParticipantStatusResource.ACCEPTED)).thenReturn(restSuccess(asList(participant)));

        MvcResult result = mockMvc.perform(get("/assessor/dashboard"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-dashboard"))
                .andReturn();

        AssessorDashboardViewModel model = (AssessorDashboardViewModel) result.getModelAndView().getModel().get("model");

        List<AssessorDashboardActiveCompetitionViewModel> expectedActiveCompetitions = asList(
                new AssessorDashboardActiveCompetitionViewModel(2L, "Juggling Craziness", 1, 2,
                        LocalDateTime.now().plusDays(4).toLocalDate(),
                        3,
                        50
                )
        );

        assertEquals(expectedActiveCompetitions, model.getActiveCompetitions());
        assertTrue(model.getUpcomingCompetitions().isEmpty());
    }

    @Test
    public void dashboard_activeStartsToday() throws Exception {
        CompetitionParticipantResource participant = newCompetitionParticipantResource()
                .withCompetitionParticipantRole(CompetitionParticipantRoleResource.ASSESSOR)
                .withStatus(ParticipantStatusResource.ACCEPTED)
                .withUser(3L)
                .withCompetition(2L)
                .withCompetitionName("Juggling Craziness")
                .withAssessorAcceptsDate(now().minusDays(0))
                .withAssessorDeadlineDate(now().plusDays(6))
                .build();

        when(competitionParticipantRestService.getParticipants(3L, CompetitionParticipantRoleResource.ASSESSOR, ParticipantStatusResource.ACCEPTED)).thenReturn(restSuccess(asList(participant)));

        MvcResult result = mockMvc.perform(get("/assessor/dashboard"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-dashboard"))
                .andReturn();

        AssessorDashboardViewModel model = (AssessorDashboardViewModel) result.getModelAndView().getModel().get("model");

        List<AssessorDashboardActiveCompetitionViewModel> expectedActiveCompetitions = asList(
                new AssessorDashboardActiveCompetitionViewModel(2L, "Juggling Craziness", 1, 2,
                        LocalDateTime.now().plusDays(6).toLocalDate(),
                        5,
                        16
                )
        );

        assertEquals(expectedActiveCompetitions, model.getActiveCompetitions());
        assertTrue(model.getUpcomingCompetitions().isEmpty());
    }

    @Test
    public void dashboard_activeEndsToday() throws Exception {
        CompetitionParticipantResource participant = newCompetitionParticipantResource()
                .withCompetitionParticipantRole(CompetitionParticipantRoleResource.ASSESSOR)
                .withStatus(ParticipantStatusResource.ACCEPTED)
                .withUser(3L)
                .withCompetition(2L)
                .withCompetitionName("Juggling Craziness")
                .withAssessorAcceptsDate(now().minusDays(2))
                .withAssessorDeadlineDate(now().plusDays(0))
                .build();

        when(competitionParticipantRestService.getParticipants(3L, CompetitionParticipantRoleResource.ASSESSOR, ParticipantStatusResource.ACCEPTED)).thenReturn(restSuccess(asList(participant)));

        MvcResult result = mockMvc.perform(get("/assessor/dashboard"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-dashboard"))
                .andReturn();

        AssessorDashboardViewModel model = (AssessorDashboardViewModel) result.getModelAndView().getModel().get("model");

        assertTrue(model.getActiveCompetitions().isEmpty());
        assertTrue(model.getUpcomingCompetitions().isEmpty());
    }


    @Test
    public void dashboard_upcomingAssessments() throws Exception {
        CompetitionParticipantResource participant = newCompetitionParticipantResource()
                .withCompetitionParticipantRole(CompetitionParticipantRoleResource.ASSESSOR)
                .withStatus(ParticipantStatusResource.ACCEPTED)
                .withUser(3L)
                .withCompetition(2L)
                .withCompetitionName("Juggling Craziness")
                .withAssessorAcceptsDate(now().plusDays(1))
                .withAssessorDeadlineDate(now().plusDays(7))
                .build();

        when(competitionParticipantRestService.getParticipants(3L, CompetitionParticipantRoleResource.ASSESSOR, ParticipantStatusResource.ACCEPTED)).thenReturn(restSuccess(asList(participant)));

        MvcResult result = mockMvc.perform(get("/assessor/dashboard"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-dashboard"))
                .andReturn();

        AssessorDashboardViewModel model = (AssessorDashboardViewModel) result.getModelAndView().getModel().get("model");

        List<AssessorDashboardUpcomingCompetitionViewModel> expectedUpcomingCompetitions = asList(
                new AssessorDashboardUpcomingCompetitionViewModel(
                        2L, "Juggling Craziness",
                        LocalDateTime.now().plusDays(1).toLocalDate(),
                        LocalDateTime.now().plusDays(7).toLocalDate()
                )
        );

        assertTrue(model.getActiveCompetitions().isEmpty());
        assertEquals(expectedUpcomingCompetitions, model.getUpcomingCompetitions());
    }

    @Test
    public void dashboard_pastAssessments() throws Exception {
        CompetitionParticipantResource participant = newCompetitionParticipantResource()
                .withCompetitionParticipantRole(CompetitionParticipantRoleResource.ASSESSOR)
                .withStatus(ParticipantStatusResource.ACCEPTED)
                .withUser(3L)
                .withCompetition(2L)
                .withCompetitionName("Juggling Craziness")
                .withAssessorAcceptsDate(now().minusDays(1))
                .withAssessorDeadlineDate(now().minusDays(0))
                .build();

        when(competitionParticipantRestService.getParticipants(3L, CompetitionParticipantRoleResource.ASSESSOR, ParticipantStatusResource.ACCEPTED)).thenReturn(restSuccess(asList(participant)));

        MvcResult result = mockMvc.perform(get("/assessor/dashboard"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessor-dashboard"))
                .andReturn();

        AssessorDashboardViewModel model = (AssessorDashboardViewModel) result.getModelAndView().getModel().get("model");

        assertTrue(model.getActiveCompetitions().isEmpty());
        assertTrue(model.getUpcomingCompetitions().isEmpty());
    }
}