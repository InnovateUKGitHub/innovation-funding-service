package com.worth.ifs.workflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import com.worth.ifs.workflow.transactional.ProcessOutcomeService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.assessment.builder.ProcessOutcomeResourceBuilder.newProcessOutcomeResource;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProcessOutcomeControllerTest extends BaseControllerMockMVCTest<ProcessOutcomeController> {

    @Mock
    ProcessOutcomeService processOutcomeService;

    @Override
    protected ProcessOutcomeController supplyControllerUnderTest() {
        return new ProcessOutcomeController();
    }


    @Test
    public void findById() throws Exception {
        Long processOutcomeId = 1L;

        ProcessOutcomeResource processOutcome = newProcessOutcomeResource()
                .with(id(processOutcomeId))
                .build();
        when(processOutcomeService.findOne(processOutcomeId)).thenReturn(serviceSuccess(processOutcome));


        mockMvc.perform(get("/processoutcome/{id}", processOutcomeId))
                .andExpect(content().string(new ObjectMapper().writeValueAsString(processOutcome)))
                .andExpect(status().isOk());

        verify(processOutcomeService, only()).findOne(processOutcomeId);
    }

    @Test
    public void findByAssessmentId() throws Exception {
        ProcessOutcomeResource expected = newProcessOutcomeResource().build();

        Long assessmentId = 1L;

        when(processOutcomeService.findLatestByProcess(assessmentId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(MockMvcRequestBuilders.get("/processoutcome/process/{id}", assessmentId))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(expected)));
        verify(processOutcomeService, only()).findLatestByProcess(assessmentId);
    }

    @Test
    public void findByAssessmentIdAndAssessmentOutcome() throws Exception {
        ProcessOutcomeResource expected = newProcessOutcomeResource().build();

        Long assessmentId = 1L;
        String processType =  AssessmentOutcomes.FEEDBACK.getType();

        when(processOutcomeService.findLatestByProcessAndOutcomeType(assessmentId, processType)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(MockMvcRequestBuilders.get("/processoutcome/process/{id}/type/{type}", assessmentId, processType))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(expected)));
        verify(processOutcomeService, only()).findLatestByProcessAndOutcomeType(assessmentId,processType);
    }

}
