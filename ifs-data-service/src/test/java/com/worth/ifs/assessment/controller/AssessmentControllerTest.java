package com.worth.ifs.assessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.dto.Score;
import com.worth.ifs.assessment.workflow.AssessmentWorkflowEventHandler;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.BuilderAmendFunctions.description;
import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.assessment.builder.ProcessOutcomeBuilder.newProcessOutcome;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AssessmentControllerTest extends BaseControllerMockMVCTest {

    private static String applicationControllerPath = "/assessment";

    @Mock
    AssessmentHandler assessmentHandler;

    @Mock
    AssessmentWorkflowEventHandler assessmentWorkflowEventHandler;

    @Override
    protected AssessmentController supplyControllerUnderTest() {
        return new AssessmentController();
    }

    @Test
    public void testFindAssessmentsByProcessRole() throws Exception {
        Assessment assessment = new Assessment();

        List<Assessment> assessments = new ArrayList<>();
        assessments.add(assessment);

        assessment.setId(123L);

        when(assessmentHandler.getAllByCompetitionAndAssessor(1L, 1L)).thenReturn(assessments);

        mockMvc.perform(get(applicationControllerPath+"/findAssessmentsByCompetition/1/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0]id", is(123)))
                .andDo(document("assessment/find-competition-assessment"));
    }

    @Test
    public void testGetAssessmentByProcessRole() throws Exception {
        Assessment assessment = new Assessment();

        assessment.setId(456L);

        when(assessmentHandler.getOneByProcessRole(1L)).thenReturn(assessment);

        mockMvc.perform(get(applicationControllerPath+"/findAssessmentByProcessRole/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(456)))
                .andDo(document("assessment/find-application-user-assessment"));;
    }

    @Test
    public void testGetScore() throws Exception {
        Score score = new Score(200, 20);
        long assessmentId = 1L;
        when(assessmentHandler.getScore(assessmentId)).thenReturn(score);

        mockMvc.perform(get(applicationControllerPath+"/"+assessmentId+"/score"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("percentage", is(10)))
                .andExpect(jsonPath("total", is(20)))
                .andExpect(jsonPath("possible", is(200)))
                .andDo(document("assessment/find-assessment-score-assessment"));;
    }

    @Test
    public void testGetTotalAssignedAssessmentsByProcessRole() throws Exception {
        when(assessmentHandler.getTotalAssignedAssessmentsByCompetition(1L, 1L)).thenReturn(3);

        mockMvc.perform(get(applicationControllerPath+"/totalAssignedAssessmentsByCompetition/1/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"))
                .andDo(document("assessment/total-assigned-assessment"));
    }

    @Test
    public void testGetTotalSubmittedAssessmentsByCompetition() throws Exception {
        when(assessmentHandler.getTotalSubmittedAssessmentsByCompetition(1L, 1L)).thenReturn(35);

        mockMvc.perform(get(applicationControllerPath+"/totalSubmittedAssessmentsByCompetition/1/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("35"))
                .andDo(document("assessment/total-submitted-assessment"));
    }

    @Test
    public void testAcceptAssessmentInvitation() throws Exception {

    }

    @Test
    public void testRejectAssessmentInvitation() throws Exception {
        ProcessOutcome processOutcome = newProcessOutcome()
                .with(description("a description"))
                .withOutcome("conflict-of-interest")
                .build();
        long processRoleId = 1L;
        String processState = "test";
        Assessment a = newAssessment().withProcessState(processState).build();
        when(assessmentHandler.getOneByProcessRole(processRoleId)).thenReturn(a);

        String json = new ObjectMapper().writeValueAsString(processOutcome);
        mockMvc.perform(post(applicationControllerPath + "/rejectAssessmentInvitation/" + processRoleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andDo(document("assessment/reject-invitation-assessment"));
    }

    @Test
    public void testSubmitAssessments() throws Exception {

    }

    @Test
    public void testSubmitAssessment() throws Exception {

    }
}