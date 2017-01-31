package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.management.model.CompetitionInFlightModelPopulator;
import org.innovateuk.ifs.management.viewmodel.CompetitionInFlightStatsViewModel;
import org.innovateuk.ifs.management.viewmodel.CompetitionInFlightViewModel;
import org.innovateuk.ifs.management.viewmodel.MilestonesRowViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionClosedKeyStatisticsResourceBuilder.newCompetitionClosedKeyStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionFunderResourceBuilder.newCompetitionFunderResource;
import static org.innovateuk.ifs.competition.builder.CompetitionInAssessmentKeyStatisticsResourceBuilder.newCompetitionInAssessmentKeyStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionOpenKeyStatisticsResourceBuilder.newCompetitionOpenKeyStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionReadyToOpenKeyStatisticsResourceBuilder.newCompetitionReadyToOpenKeyStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.*;
import static org.innovateuk.ifs.util.CollectionFunctions.asLinkedSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class CompetitionManagementCompetitionControllerTest extends BaseControllerMockMVCTest<CompetitionManagementCompetitionController> {

    private static final long competitionId = 1L;

    @Spy
    @InjectMocks
    private CompetitionInFlightModelPopulator competitionInFlightModelPopulator;

    @Override
    protected CompetitionManagementCompetitionController supplyControllerUnderTest() {
        return new CompetitionManagementCompetitionController();
    }

    @Test
    public void competition_readyToOpen() throws Exception {
        CompetitionReadyToOpenKeyStatisticsResource keyStatisticsResource = newCompetitionReadyToOpenKeyStatisticsResource()
                .withAssessorsInvited(1L)
                .withAssessorsAccepted(2L)
                .build();
        when(competitionKeyStatisticsRestServiceMock.getReadyToOpenKeyStatisticsByCompetition(competitionId)).thenReturn(restSuccess(keyStatisticsResource));
        competition_inFlight(READY_TO_OPEN);
        verify(competitionKeyStatisticsRestServiceMock,only()).getReadyToOpenKeyStatisticsByCompetition(competitionId);
    }

    @Test
    public void competition_open() throws Exception {
        CompetitionOpenKeyStatisticsResource keyStatisticsResource = newCompetitionOpenKeyStatisticsResource()
                .withAssessorsInvited(1L)
                .withAssessorsAccepted(2L)
                .withApplicationsPerAssessor(3L)
                .withApplicationsStarted(4L)
                .withApplicationsPastHalf(5L)
                .withApplicationsSubmitted(6L)
                .build();
        when(competitionKeyStatisticsRestServiceMock.getOpenKeyStatisticsByCompetition(competitionId)).thenReturn(restSuccess(keyStatisticsResource));
        competition_inFlight(OPEN);
        verify(competitionKeyStatisticsRestServiceMock,only()).getOpenKeyStatisticsByCompetition(competitionId);
    }

    @Test
    public void competition_closed() throws Exception {
        CompetitionClosedKeyStatisticsResource keyStatisticsResource = newCompetitionClosedKeyStatisticsResource()
                .withApplicationsRequiringAssessors(1L)
                .withAssignmentCount(2L)
                .withAssessorsWithoutApplications(3L)
                .withAssessorsInvited(4L)
                .withAssessorsAccepted(5L)
                .withApplicationsPerAssessor(6L)
                .build();
        when(competitionKeyStatisticsRestServiceMock.getClosedKeyStatisticsByCompetition(competitionId)).thenReturn(restSuccess(keyStatisticsResource));
        competition_inFlight(CLOSED);
        verify(competitionKeyStatisticsRestServiceMock,only()).getClosedKeyStatisticsByCompetition(competitionId);
    }

    @Test
    public void competition_inAssessment() throws Exception {
        CompetitionInAssessmentKeyStatisticsResource keyStatisticsResource = newCompetitionInAssessmentKeyStatisticsResource()
                .withAssignmentCount(1L)
                .withAssignmentsWaiting(2L)
                .withAssignmentsAccepted(3L)
                .withAssessmentsStarted(4L)
                .withAssessmentsSubmitted(5L)
                .build();
        when(competitionKeyStatisticsRestServiceMock.getInAssessmentKeyStatisticsByCompetition(competitionId)).thenReturn(restSuccess(keyStatisticsResource));
        competition_inFlight(IN_ASSESSMENT);
        verify(competitionKeyStatisticsRestServiceMock,only()).getInAssessmentKeyStatisticsByCompetition(competitionId);
    }

    private void competition_inFlight(CompetitionStatus status) throws Exception {

        CompetitionResource competition = newCompetitionResource()
                .withId(competitionId)
                .withCompetitionStatus(status)
                .withCompetitionTypeName("Programme")
                .withInnovationSectorName("Materials and manufacturing")
                .withInnovationAreaNames(asLinkedSet("Earth Observation", "Transport Systems"))
                .withExecutiveName("Toby Reader")
                .withLeadTechnologistName("Ian Cooper")
                .withFunders(newCompetitionFunderResource()
                        .withFunderBudget(new BigInteger("1000000"))
                        .build(2))
                .withName("Technology inspired")
                .build();


        List<MilestoneResource> milestoneResources = newMilestoneResource()
                .withId(1L)
                .withName(MilestoneType.OPEN_DATE, MilestoneType.ASSESSMENT_CLOSED, MilestoneType.FUNDERS_PANEL)
                .withDate(LocalDateTime.now())
                .withCompetitionId(1L).build(3);

        when(competitionService.getById(competition.getId())).thenReturn(competition);
        when(assessmentRestService.countByStateAndCompetition(AssessmentStates.CREATED, competition.getId())).thenReturn(restSuccess(3L));
        when(milestoneServiceMock.getAllMilestonesByCompetitionId(competition.getId())).thenReturn(milestoneResources);

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}", competition.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("competition/competition-in-flight"))
                .andReturn();

        CompetitionInFlightViewModel model = (CompetitionInFlightViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(competition.getId(), model.getCompetitionId());
        assertEquals("Technology inspired", model.getCompetitionName());
        assertEquals(status, model.getCompetitionStatus());
        assertEquals("Programme", model.getCompetitionType());
        assertEquals("Materials and manufacturing", model.getInnovationSector());
        assertEquals("Earth Observation, Transport Systems", model.getInnovationArea());
        assertEquals("Toby Reader", model.getExecutive());
        assertEquals("Ian Cooper", model.getLead());
        assertTrue(new BigInteger("2000000").compareTo(model.getFunding()) == 0);
        assertMilestones(milestoneResources, model.getMilestones());
        assertKeyStatistics(model.getKeyStatistics(),status);

        verify(competitionService, only()).getById(competition.getId());
        verify(assessmentRestService, only()).countByStateAndCompetition(AssessmentStates.CREATED, competition.getId());
    }

    private void assertKeyStatistics(CompetitionInFlightStatsViewModel model, CompetitionStatus state) {
        switch (state) {
            case OPEN:
            case CLOSED:
                assertEquals(6L, (long) model.getStatSix());
            case IN_ASSESSMENT:
                assertEquals(5L, (long) model.getStatFive());
                assertEquals(4L, (long) model.getStatFour());
                assertEquals(3L, (long) model.getStatThree());
            case READY_TO_OPEN:
                assertEquals(2L, model.getStatTwo());
                assertEquals(1L, model.getStatOne());
        }
    }

    @Test
    public void closeAssessment() throws Exception {
        Long competitionId = 1L;
        mockMvc.perform(post("/competition/{competitionId}/close-assessment", competitionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/" + competitionId));
        verify(competitionService, only()).closeAssessment(competitionId);
    }

    @Test
    public void notifyAssessors() throws Exception {
        Long competitionId = 1L;
        mockMvc.perform(post("/competition/{competitionId}/notify-assessors", competitionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/" + competitionId));
        verify(competitionService, only()).notifyAssessors(competitionId);
    }

    private void assertMilestones(List<MilestoneResource> expectedMilestones, List<MilestonesRowViewModel> actualMilestones) {
        assertEquals(expectedMilestones.size(), actualMilestones.size());
        expectedMilestones.sort(Comparator.comparing(MilestoneResource::getType));
        for (int i = 0; i < expectedMilestones.size(); i++) {
            assertEquals(expectedMilestones.get(i).getDate(), actualMilestones.get(i).getDateTime());
            assertEquals(expectedMilestones.get(i).getType(), actualMilestones.get(i).getMilestoneType());
        }
    }
}
