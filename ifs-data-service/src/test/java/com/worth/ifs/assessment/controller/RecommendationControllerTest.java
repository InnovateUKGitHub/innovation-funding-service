package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.domain.Recommendation;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RecommendationControllerTest extends BaseControllerMockMVCTest {

    private static String applicationControllerPath = "/assessment";

    @Mock
    AssessmentHandler assessmentHandler;

    @Override
    protected AssessmentController supplyControllerUnderTest() {
        return new AssessmentController();
    }

    @Test
    public void testFindAssessmentsByCompetition() throws Exception {
        Recommendation recommendation = new Recommendation();

        List<Recommendation> recommendations = new ArrayList<Recommendation>();
        recommendations.add(recommendation);

        recommendation.setId(123L);

        when(assessmentHandler.getAllByCompetitionAndAssessor(1L, 1L)).thenReturn(recommendations);

        mockMvc.perform(get(applicationControllerPath+"/findAssessmentsByCompetition/1/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0]id", is(123)))
                .andDo(document("recommendation/find-competition-recommendation"));
    }

    @Test
    public void testGetAssessmentByUserAndApplication() throws Exception {
        Recommendation recommendation = new Recommendation();

        recommendation.setId(456L);

        when(assessmentHandler.getOneByAssessorAndApplication(1L, 1L)).thenReturn(recommendation);

        mockMvc.perform(get(applicationControllerPath+"/findAssessmentByApplication/1/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(456)))
                .andDo(document("recommendation/find-application-user-recommendation"));;
    }

    @Test
    public void testGetTotalAssignedAssessmentsByCompetition() throws Exception {
        when(assessmentHandler.getTotalAssignedAssessmentsByCompetition(1L, 1L)).thenReturn(3);

        mockMvc.perform(get(applicationControllerPath+"/totalAssignedAssessmentsByCompetition/1/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"))
                .andDo(document("assessment/total-assigned-assessments"));;
    }

    @Test
    public void testGetTotalSubmittedAssessmentsByCompetition() throws Exception {
        when(assessmentHandler.getTotalSubmittedAssessmentsByCompetition(1L, 1L)).thenReturn(35);

        mockMvc.perform(get(applicationControllerPath+"/totalSubmittedAssessmentsByCompetition/1/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("35"))
                .andDo(document("assessment/total-submitted-assessments"));;
    }

    @Test
    public void testAcceptAssessmentInvitation() throws Exception {
    }

    @Test
    public void testRejectAssessmentInvitation() throws Exception {

    }

    @Test
    public void testSubmitAssessments() throws Exception {

    }

    @Test
    public void testSubmitAssessment() throws Exception {

    }
}