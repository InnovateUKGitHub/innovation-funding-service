package com.worth.ifs.assessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.resource.AssessmentFeedbackResource;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.assessment.builder.AssessmentFeedbackResourceBuilder.newAssessmentFeedbackResource;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AssessmentFeedbackControllerTest extends BaseControllerMockMVCTest<AssessmentFeedbackController> {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {
    }

    @Override
    protected AssessmentFeedbackController supplyControllerUnderTest() {
        return new AssessmentFeedbackController();
    }

    @Test
    public void getAllAssessmentFeedback() throws Exception {
        final List<AssessmentFeedbackResource> expected = newAssessmentFeedbackResource()
                .build(2);

        final Long assessmentId = 1L;

        when(assessmentFeedbackServiceMock.getAllAssessmentFeedback(assessmentId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/assessment-feedback/assessment/{assessmentId}", assessmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("[0]id", is(expected.get(0).getId().intValue())))
                .andExpect(jsonPath("[1]id", is(expected.get(1).getId().intValue())));

        verify(assessmentFeedbackServiceMock, only()).getAllAssessmentFeedback(assessmentId);
    }

    @Test
    public void getAssessmentFeedbackByAssessmentAndQuestion() throws Exception {
        final AssessmentFeedbackResource expected = newAssessmentFeedbackResource()
                .build();

        final Long assessmentId = 1L;
        final Long questionId = 2L;

        when(assessmentFeedbackServiceMock.getAssessmentFeedbackByAssessmentAndQuestion(assessmentId, questionId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/assessment-feedback/assessment/{assessmentId}/question/{questionId}", assessmentId, questionId))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expected)));

        verify(assessmentFeedbackServiceMock, only()).getAssessmentFeedbackByAssessmentAndQuestion(assessmentId, questionId);
    }

    @Test
    public void updateFeedbackValue() throws Exception {
        final Long assessmentId = 1L;
        final Long questionId = 2L;
        final String value = "Blah";

        when(assessmentFeedbackServiceMock.updateFeedbackValue(assessmentId, questionId, value)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/assessment-feedback/assessment/{assessmentId}/question/{questionId}/feedback-value", assessmentId, questionId)
                .param("value", value))
                .andExpect(status().isOk());

        verify(assessmentFeedbackServiceMock, only()).updateFeedbackValue(assessmentId, questionId, value);
    }

    @Test
    public void updateFeedbackScore() throws Exception {
        final Long assessmentId = 1L;
        final Long questionId = 2L;
        final Integer score = 10;

        when(assessmentFeedbackServiceMock.updateFeedbackScore(assessmentId, questionId, score)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/assessment-feedback/assessment/{assessmentId}/question/{questionId}/feedback-score", assessmentId, questionId)
                .param("score", String.valueOf(score)))
                .andExpect(status().isOk());

        verify(assessmentFeedbackServiceMock, only()).updateFeedbackScore(assessmentId, questionId, score);
    }
}