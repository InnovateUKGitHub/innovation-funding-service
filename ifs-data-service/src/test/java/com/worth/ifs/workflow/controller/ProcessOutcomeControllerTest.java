package com.worth.ifs.workflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import com.worth.ifs.workflow.mapper.ProcessOutcomeMapper;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import com.worth.ifs.workflow.transactional.ProcessOutcomeService;
import org.junit.Test;
import org.mockito.Mock;

import static com.worth.ifs.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.assessment.builder.ProcessOutcomeBuilder.newProcessOutcome;
import static com.worth.ifs.assessment.builder.ProcessOutcomeResourceBuilder.newProcessOutcomeResource;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProcessOutcomeControllerTest extends BaseControllerMockMVCTest<ProcessOutcomeController>  {

    @Mock
    ProcessOutcomeService processOutcomeService;

    @Mock
    private ProcessOutcomeMapper processOutcomeMapper;

    @Override
    protected ProcessOutcomeController supplyControllerUnderTest() {
        return new ProcessOutcomeController();
    }


    @Test
    public void processOutcomeControllerShouldReturnProcessOutcomeById() throws Exception {
        Long processOutcomeId = 1L;

        ProcessOutcome processOutcome = newProcessOutcome().with(id(processOutcomeId)).build();
        ProcessOutcomeResource processOutcomeResource = newProcessOutcomeResource().with(id(processOutcomeId)).build();
        when(processOutcomeService.findOne(processOutcomeId)).thenReturn(processOutcome);
        when(processOutcomeMapper.mapToResource(processOutcome)).thenReturn(processOutcomeResource);

        mockMvc.perform(get("/processoutcome/{id}", processOutcomeId))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(processOutcomeResource)));

        verify(processOutcomeService, only()).findOne(processOutcomeId);
    }

}
