package org.innovateuk.ifs.management.competition.inflight.controller;

import org.hamcrest.core.IsInstanceOf;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.assessment.resource.CompetitionInAssessmentKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.assessment.service.AssessorRestService;
import org.innovateuk.ifs.assessment.service.CompetitionKeyAssessmentStatisticsRestService;
import org.innovateuk.ifs.commons.exception.IncorrectStateForPageException;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.service.CompetitionAssessmentConfigRestService;
import org.innovateuk.ifs.competition.service.CompetitionPostSubmissionRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.management.competition.inflight.populator.CompetitionInFlightModelPopulator;
import org.innovateuk.ifs.management.competition.inflight.populator.CompetitionInFlightStatsModelPopulator;
import org.innovateuk.ifs.management.competition.inflight.viewmodel.CompetitionInFlightViewModel;
import org.innovateuk.ifs.util.NavigationUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.assessment.builder.CompetitionInAssessmentKeyAssessmentStatisticsResourceBuilder.newCompetitionInAssessmentKeyAssessmentStatisticsResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionAssessmentConfigResourceBuilder.newCompetitionAssessmentConfigResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;
import static org.innovateuk.ifs.competition.resource.AssessorFinanceView.DETAILED;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Class for testing public functions of {@link CompetitionManagementCompetitionController}
 */
@RunWith(MockitoJUnitRunner.Silent.class)
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
    private CompetitionPostSubmissionRestService competitionPostSubmissionRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private CompetitionKeyAssessmentStatisticsRestService competitionKeyAssessmentStatisticsRestService;

    @Mock
    private AssessmentRestService assessmentRestService;

    @Mock
    private AssessorRestService assessorRestService;

    @Mock
    CompetitionAssessmentConfigRestService competitionAssessmentConfigRestService;

    @Spy
    private NavigationUtils navigationUtilsMock;

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
                .withCompetitionTypeName("Programme")
                .withHasAssessmentStage(true)
                .build();

        CompetitionAssessmentConfigResource competitionAssessmentConfigResource = newCompetitionAssessmentConfigResource()
                .withIncludeAverageAssessorScoreInNotifications(false)
                .withAssessorCount(5)
                .withAssessorPay(BigDecimal.valueOf(100))
                .withHasAssessmentPanel(true)
                .withHasInterviewStage(true)
                .withAssessorFinanceView(DETAILED)
                .build();

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competitionResource));
        when(competitionAssessmentConfigRestService.findOneByCompetitionId(competitionId)).thenReturn(restSuccess(competitionAssessmentConfigResource));
        competitionResource.setMilestones(singletonList(10L));

        MilestoneResource milestone = newMilestoneResource()
                .withId(1L)
                .withName(MilestoneType.OPEN_DATE)
                .withDate()
                .withCompetitionId(1L).build();

        List<MilestoneResource> milestones = new ArrayList<>();
        milestones.add(milestone);

        when(milestoneRestService.getAllMilestonesByCompetitionId(competitionResource.getId())).thenReturn(restSuccess(milestones));

        CompetitionInAssessmentKeyAssessmentStatisticsResource keyStatisticsResource = newCompetitionInAssessmentKeyAssessmentStatisticsResource()
                .withAssignmentCount(1)
                .withAssignmentsWaiting(2)
                .withAssignmentsAccepted(3)
                .withAssessmentsStarted(4)
                .withAssessmentsSubmitted(5)
                .build();

        when(competitionKeyAssessmentStatisticsRestService.getInAssessmentKeyStatisticsByCompetition(competitionResource.getId())).thenReturn(restSuccess(keyStatisticsResource));
        when(assessmentRestService.countByStateAndCompetition(AssessmentState.CREATED, competitionResource.getId())).thenReturn(restSuccess(2L));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/competition/{competitionId}", competitionId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("competition/competition-in-flight"))
                .andReturn();

        InOrder inOrder = inOrder(competitionRestService, milestoneRestService, competitionKeyAssessmentStatisticsRestService, assessmentRestService);
        inOrder.verify(competitionRestService).getCompetitionById(competitionId);
        inOrder.verify(milestoneRestService).getAllMilestonesByCompetitionId(competitionResource.getId());
        inOrder.verify(competitionKeyAssessmentStatisticsRestService).getInAssessmentKeyStatisticsByCompetition(competitionResource.getId());
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
        assertEquals(true, model.isInterviewPanelEnabled());
        assertEquals(false, model.isFundingDecisionEnabled());
        assertEquals(false, model.isFundingNotificationDisplayed());
        assertEquals(DETAILED, model.getAssessorFinanceView());
    }

    @Test
    public void competitionInProjectSetup() throws Exception {
        String competitionName = "Test Competition";
        CompetitionStatus competitionStatus = CompetitionStatus.PROJECT_SETUP;

        CompetitionResource competitionResource = newCompetitionResource()
                .withName(competitionName)
                .withCompetitionStatus(competitionStatus)
                .build();

        when(competitionRestService.getCompetitionById(competitionResource.getId())).thenReturn(restSuccess(competitionResource));

        mockMvc.perform(MockMvcRequestBuilders.get("/competition/{competitionId}", competitionResource.getId()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());

        InOrder inOrder = inOrder(competitionRestService);
        inOrder.verify(competitionRestService).getCompetitionById(competitionResource.getId());
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

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competitionResource));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/competition/{competitionId}", competitionId))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(model().attribute("exception", new IsInstanceOf(IncorrectStateForPageException.class)))
                .andReturn();

        IncorrectStateForPageException exception = (IncorrectStateForPageException) result.getModelAndView().getModel().get("exception");
        assertEquals(format("Unexpected competition state for competition: %s", competitionId), exception.getMessage());

        InOrder inOrder = inOrder(competitionRestService);
        inOrder.verify(competitionRestService).getCompetitionById(competitionId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void closeAssessment() throws Exception {
        long competitionId = 1L;

        when(competitionPostSubmissionRestService.closeAssessment(competitionId)).thenReturn(restSuccess());

        mockMvc.perform(post("/competition/{competitionId}/close-assessment", competitionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/competition/%s", competitionId)));

        verify(competitionPostSubmissionRestService).closeAssessment(competitionId);
        verifyNoMoreInteractions(competitionRestService);
    }

    @Test
    public void notifyAssessors() throws Exception {
        long competitionId = 1L;

        when(assessorRestService.notifyAssessors(competitionId)).thenReturn(restSuccess());

        mockMvc.perform(post("/competition/{competitionId}/notify-assessors", competitionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/competition/%s", competitionId)));

        verify(assessorRestService).notifyAssessors(competitionId);
        verifyNoMoreInteractions(competitionRestService);
    }

    @Test
    public void releaseFeedback() throws Exception {
        long competitionId = 1L;

        CompetitionResource competition = newCompetitionResource()
                .withCompletionStage(CompetitionCompletionStage.PROJECT_SETUP)
                .build();

        when(competitionPostSubmissionRestService.releaseFeedback(competitionId)).thenReturn(restSuccess());
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));

        mockMvc.perform(post("/competition/{competitionId}/release-feedback", competitionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard/project-setup"));

        InOrder inOrder = inOrder(competitionPostSubmissionRestService, competitionRestService);
        inOrder.verify(competitionPostSubmissionRestService).releaseFeedback(competitionId);
        inOrder.verify(competitionRestService).getCompetitionById(competitionId);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void releaseFeedbackWithReleaseFeedbackCompletionStage() throws Exception {
        long competitionId = 1L;

        CompetitionResource competition = newCompetitionResource()
                .withCompletionStage(CompetitionCompletionStage.RELEASE_FEEDBACK)
                .build();

        when(competitionPostSubmissionRestService.releaseFeedback(competitionId)).thenReturn(restSuccess());
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));

        mockMvc.perform(post("/competition/{competitionId}/release-feedback", competitionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard/previous"));

        InOrder inOrder = inOrder(competitionPostSubmissionRestService, competitionRestService);
        inOrder.verify(competitionPostSubmissionRestService).releaseFeedback(competitionId);
        inOrder.verify(competitionRestService).getCompetitionById(competitionId);
        inOrder.verifyNoMoreInteractions();
    }
}
