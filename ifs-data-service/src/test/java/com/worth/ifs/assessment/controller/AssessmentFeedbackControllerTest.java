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
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
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
                .withId(1000L, 1001L)
                .build(2);

        final Long assessmentId = 9999L;

        when(assessmentFeedbackServiceMock.getAllAssessmentFeedback(assessmentId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/assessment-feedback/assessment/{assessmentId}", assessmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("[0]id", is(1000)))
                .andExpect(jsonPath("[1]id", is(1001)));
    }

    @Test
    public void getAssessmentFeedbackByAssessmentAndQuestion() throws Exception {
        final AssessmentFeedbackResource expected = newAssessmentFeedbackResource()
                .withId(1000L)
                .build();

        final Long assessmentId = 9999L;
        final Long questionId = 8888L;

        when(assessmentFeedbackServiceMock.getAssessmentFeedbackByAssessmentAndQuestion(assessmentId, questionId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/assessment-feedback/assessment/{assessmentId}/question/{questionId}", assessmentId, questionId))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expected)));
    }
}