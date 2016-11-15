package com.worth.ifs.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.builder.QuestionAssessmentResourceBuilder;
import com.worth.ifs.application.resource.QuestionAssessmentResource;
import com.worth.ifs.application.transactional.QuestionAssessmentService;
import org.junit.Test;
import org.mockito.Mock;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class QuestionAssessmentControllerTest extends BaseControllerMockMVCTest<QuestionAssessmentController> {
    private static String baseURL = "/questionAssessment";
    @Mock
    protected QuestionAssessmentService questionAssessmentService;

    @Override
    protected QuestionAssessmentController supplyControllerUnderTest() {
        return new QuestionAssessmentController();
    }

    @Test
    public void testGetById() throws Exception {
        QuestionAssessmentResource resource = QuestionAssessmentResourceBuilder.newQuestionAssessment().build();
        when(questionAssessmentService.getById(anyLong())).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get(baseURL + "/" + 1L))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(resource)))
                .andReturn();
    }

    @Test
    public void testFindByQuestion() throws Exception {
        QuestionAssessmentResource resource = QuestionAssessmentResourceBuilder.newQuestionAssessment().build();
        when(questionAssessmentService.findByQuestion(anyLong())).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get(baseURL + "/findByQuestion/" + 1L))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(resource)))
                .andReturn();
    }
}