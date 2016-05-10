package com.worth.ifs.assessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.resource.Score;
import com.worth.ifs.assessment.transactional.AssessorService;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.BuilderAmendFunctions.description;
import static com.worth.ifs.BuilderAmendFunctions.id;
import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.assessment.builder.ProcessOutcomeBuilder.newProcessOutcome;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AssessmentControllerTest extends BaseControllerMockMVCTest {

    private static String applicationControllerPath = "/assessment";

    @Mock
    private AssessorService assessorService;

    @Override
    protected AssessmentController supplyControllerUnderTest() {
        return new AssessmentController();
    }

    @Test
    public void testFindAssessmentsByProcessRole() throws Exception {
        AssessmentResource assessment = new AssessmentResource();

        List<AssessmentResource> assessments = new ArrayList<>();
        assessments.add(assessment);

        assessment.setId(123L);

        when(assessorService.getAllByCompetitionAndAssessor(456L, 123L)).thenReturn(serviceSuccess(assessments));

        mockMvc.perform(get(applicationControllerPath+"/findAssessmentsByCompetition/123/456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0]id", is(123)))
                .andDo(document("assessment/find-competition-assessment"));
    }

    @Test
    public void testGetAssessmentByProcessRole() throws Exception {
        AssessmentResource assessment = new AssessmentResource();

        assessment.setId(456L);

        when(assessorService.getOneByProcessRole(123L)).thenReturn(serviceSuccess(assessment));

        mockMvc.perform(get(applicationControllerPath+"/findAssessmentByProcessRole/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(456)))
                .andDo(document("assessment/find-application-user-assessment"));;
    }

    @Test
    public void testGetScore() throws Exception {
        Score score = new Score(200, 20);
        long assessmentId = 1L;
        when(assessorService.getScore(assessmentId)).thenReturn(serviceSuccess(score));

        mockMvc.perform(get(applicationControllerPath+"/"+assessmentId+"/score"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("percentage", is(10)))
                .andExpect(jsonPath("total", is(20)))
                .andExpect(jsonPath("possible", is(200)))
                .andDo(document("assessment/find-assessment-score-assessment"));;
    }

    @Test
    public void testGetTotalAssignedAssessmentsByProcessRole() throws Exception {
        when(assessorService.getTotalAssignedAssessmentsByCompetition(1L, 1L)).thenReturn(serviceSuccess(3));

        mockMvc.perform(get(applicationControllerPath+"/totalAssignedAssessmentsByCompetition/1/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"))
                .andDo(document("assessment/total-assigned-assessment"));
    }

    @Test
    public void testGetTotalSubmittedAssessmentsByCompetition() throws Exception {
        when(assessorService.getTotalSubmittedAssessmentsByCompetition(1L, 1L)).thenReturn(serviceSuccess(35));

        mockMvc.perform(get(applicationControllerPath+"/totalSubmittedAssessmentsByCompetition/1/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("35"))
                .andDo(document("assessment/total-submitted-assessment"));
    }

    @Test
    public void testAcceptAssessmentInvitation() throws Exception {
        long assessmentId = 1L;
        long processRoleId = 2L;
        Assessment assessment = newAssessment().withId(assessmentId).build();
        String json = new ObjectMapper().writeValueAsString(assessment);

        when(assessorService.acceptAssessmentInvitation(eq(processRoleId), isA(AssessmentResource.class))).thenReturn(serviceSuccess());

        mockMvc.perform(post(applicationControllerPath + "/acceptAssessmentInvitation/" + processRoleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andDo(document("assessment/accept-invitation-assessment"));
    }

    @Test
    public void testRejectAssessmentInvitation() throws Exception {
        ProcessOutcome processOutcome = newProcessOutcome()
                .with(id(null))
                .with(description("a description"))
                .withOutcome("conflict-of-interest")
                .build();
        long processRoleId = 1L;
        when(assessorService.rejectAssessmentInvitation(eq(processRoleId), isA(ProcessOutcome.class))).thenReturn(serviceSuccess());

        String json = new ObjectMapper().writeValueAsString(processOutcome);
        mockMvc.perform(post(applicationControllerPath + "/rejectAssessmentInvitation/" + processRoleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andDo(document("assessment/reject-invitation-assessment"));
    }

    @Test
    public void testSubmitAssessments() throws Exception {
        long assessorId = 1L;
        long assessmentId1 = 2L;
        long assessmentId2 = 3L;
        Assessment assessment1 = newAssessment().withId(assessmentId1).build();
        Assessment assessment2 = newAssessment().withId(assessmentId2).build();


        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonNode = mapper.createObjectNode();
        jsonNode.put("assessorId", assessorId);
        jsonNode.putArray("assessmentsToSubmit").add(assessmentId1).add(assessmentId2);
        String json = mapper.writeValueAsString(jsonNode);

        when(assessorService.getOne(assessmentId1)).thenReturn(serviceSuccess(assessment1));
        when(assessorService.getOne(assessmentId2)).thenReturn(serviceSuccess(assessment2));

        when(assessorService.submitAssessments(Sets.newSet(assessmentId1, assessmentId2))).thenReturn(serviceSuccess());

        mockMvc.perform(post(applicationControllerPath + "/submitAssessments").contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andDo(document("assessment/submit-assessments-assessment"));
    }

    @Test
    public void testSubmitAssessment() throws Exception {

        long assessorId = 1L;
        long applicationId = 3L;
        String suitableValue = "yes";

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonNode = mapper.createObjectNode()
                .put("assessorId", assessorId)
                .put("applicationId", applicationId)
                .put("suitableValue", suitableValue)
                .put("comments", "comments")
                .put("suitableFeedback", "feedback");
        String json = mapper.writeValueAsString(jsonNode);

        when(assessorService.submitAssessment(assessorId, applicationId, suitableValue, "feedback", "comments")).thenReturn(serviceSuccess());

        mockMvc.perform(post(applicationControllerPath + "/saveAssessmentSummary").contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andDo(document("assessment/submit-assessment-assessment"));
    }
}