package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.commons.error.exception.ObjectNotFoundException;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.service.CompetitionKeyStatisticsRestService;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.management.model.CompetitionInFlightModelPopulator;
import org.innovateuk.ifs.management.model.CompetitionInFlightStatsModelPopulator;
import org.innovateuk.ifs.management.viewmodel.CompetitionInFlightViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.internal.matchers.InstanceOf;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionInAssessmentKeyStatisticsResourceBuilder.newCompetitionInAssessmentKeyStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Class for testing public functions of {@link CompetitionManagementCompetitionController}
 */
@RunWith(MockitoJUnitRunner.class)
public class CompetitionManagementCompetitionControllerTest extends BaseControllerMockMVCTest<CompetitionManagementCompetitionController> {

    @InjectMocks
    @Spy
    private CompetitionInFlightModelPopulator competitionInFlightModelPopulator;

    @InjectMocks
    @Spy
    private CompetitionInFlightStatsModelPopulator competitionInFlightStatsModelPopulator;

    @Mock
    private MilestoneRestService milestoneRestService;

    @Mock
    private CompetitionKeyStatisticsRestService competitionKeyStatisticsRestService;


    @Override
    protected CompetitionManagementCompetitionController supplyControllerUnderTest() {
        return new CompetitionManagementCompetitionController();
    }

    @Test
    public void competitionInFlight() throws Exception {
        long competitionId = 1L;
        String expectedCompetitionName = "Test Competition";
        CompetitionStatus expectedCompetitionStatus = CompetitionStatus.IN_ASSESSMENT;

        CompetitionResource competitionResource = newCompetitionResource()
                .withName(expectedCompetitionName)
                .withCompetitionStatus(expectedCompetitionStatus)
                .withHasAssessmentPanel(true)
                .build();

        when(competitionService.getById(competitionId)).thenReturn(competitionResource);
        competitionResource.setMilestones(singletonList(10L));

        MilestoneResource milestone = newMilestoneResource()
                .withId(1L)
                .withName(MilestoneType.OPEN_DATE)
                .withDate()
                .withCompetitionId(1L).build();

        List<MilestoneResource> milestones = new ArrayList<>();
        milestones.add(milestone);

        when(milestoneRestService.getAllMilestonesByCompetitionId(competitionResource.getId())).thenReturn(restSuccess(milestones));

        CompetitionInAssessmentKeyStatisticsResource keyStatisticsResource = newCompetitionInAssessmentKeyStatisticsResource()
                .withAssignmentCount(1)
                .withAssignmentsWaiting(2)
                .withAssignmentsAccepted(3)
                .withAssessmentsStarted(4)
                .withAssessmentsSubmitted(5)
                .build();

        when(competitionKeyStatisticsRestService.getInAssessmentKeyStatisticsByCompetition(competitionResource.getId())).thenReturn(restSuccess(keyStatisticsResource));
        when(assessmentRestService.countByStateAndCompetition(AssessmentState.CREATED, competitionResource.getId())).thenReturn(restSuccess(2L));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/competition/{competitionId}", competitionId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("competition/competition-in-flight"))
                .andReturn();

        InOrder inOrder = inOrder(competitionService, milestoneRestService, competitionKeyStatisticsRestService, assessmentRestService);
        inOrder.verify(competitionService).getById(competitionId);
        inOrder.verify(milestoneRestService).getAllMilestonesByCompetitionId(competitionResource.getId());
        inOrder.verify(competitionKeyStatisticsRestService).getInAssessmentKeyStatisticsByCompetition(competitionResource.getId());
        inOrder.verify(assessmentRestService).countByStateAndCompetition(AssessmentState.CREATED, competitionResource.getId());
        inOrder.verifyNoMoreInteractions();

        CompetitionInFlightViewModel model = (CompetitionInFlightViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(competitionResource.getId(), model.getCompetitionId());
        assertEquals(expectedCompetitionName, model.getCompetitionName());
        assertEquals(expectedCompetitionStatus, model.getCompetitionStatus());
        assertEquals(1, model.getKeyStatistics().getStatOne());
        assertEquals(2, model.getKeyStatistics().getStatTwo());
        assertEquals(3, (int) model.getKeyStatistics().getStatThree());
        assertEquals(4, (int) model.getKeyStatistics().getStatFour());
        assertEquals(5, (int) model.getKeyStatistics().getStatFive());
        assertEquals(true, model.isAssessmentPanelEnabled());
    }

    @Test
    public void competitionInProjectSetup() throws Exception {
        long competitionId = 1L;
        String competitionName = "Test Competition";
        CompetitionStatus competitionStatus = CompetitionStatus.PROJECT_SETUP;

        CompetitionResource competitionResource = newCompetitionResource()
                .withName(competitionName)
                .withCompetitionStatus(competitionStatus)
                .build();

        when(competitionService.getById(competitionId)).thenReturn(competitionResource);

        mockMvc.perform(MockMvcRequestBuilders.get("/competition/{competitionId}", competitionId))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(model().attribute("exception", new InstanceOf(ObjectNotFoundException.class)));

        InOrder inOrder = inOrder(competitionService);
        inOrder.verify(competitionService).getById(competitionId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void competitionInIllegalState() throws Exception {
        long competitionId = 1L;
        String competitionName = "Test Competition";
        CompetitionStatus competitionStatus = CompetitionStatus.COMPETITION_SETUP;

        CompetitionResource competitionResource = newCompetitionResource()
                .withName(competitionName)
                .withCompetitionStatus(competitionStatus)
                .build();

        when(competitionService.getById(competitionId)).thenReturn(competitionResource);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/competition/{competitionId}", competitionId))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                .andReturn();

        IllegalStateException exception = (IllegalStateException) result.getModelAndView().getModel().get("exception");
        assertEquals(format("Unexpected competition state for competition: %s", competitionId), exception.getMessage());

        InOrder inOrder = inOrder(competitionService);
        inOrder.verify(competitionService).getById(competitionId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void closeAssessment() throws Exception {
        long competitionId = 1L;

        when(competitionService.closeAssessment(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/{competitionId}/close-assessment", competitionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/competition/%s", competitionId)));

        verify(competitionService).closeAssessment(competitionId);
        verifyNoMoreInteractions(competitionService);
    }

    @Test
    public void notifyAssessors() throws Exception {
        long competitionId = 1L;

        when(competitionService.notifyAssessors(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/{competitionId}/notify-assessors", competitionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/competition/%s", competitionId)));

        verify(competitionService).notifyAssessors(competitionId);
        verifyNoMoreInteractions(competitionService);
    }

    @Test
    public void releaseFeedback() throws Exception {
        long competitionId = 1L;

        doNothing().when(competitionService).releaseFeedback(competitionId);

        mockMvc.perform(post("/competition/{competitionId}/release-feedback", competitionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard/project-setup"));

        verify(competitionService).releaseFeedback(competitionId);
        verifyNoMoreInteractions(competitionService);
    }
}
