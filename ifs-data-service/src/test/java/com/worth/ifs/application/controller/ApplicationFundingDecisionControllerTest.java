package com.worth.ifs.application.controller;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.Test;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.FundingDecision;

public class ApplicationFundingDecisionControllerTest extends BaseControllerMockMVCTest<ApplicationFundingDecisionController> {

    @Override
    protected ApplicationFundingDecisionController supplyControllerUnderTest() {
        return new ApplicationFundingDecisionController();
    }

    @Test
    public void applicationFundingDecisionControllerShouldReturnAppropriateStatusCode() throws Exception {
        Long competitionId = 1L;
        Map<Long, FundingDecision> decision = ImmutableMap.of(1L, FundingDecision.FUNDED, 2L, FundingDecision.NOT_FUNDED);

        when(applicationFundingServiceMock.makeFundingDecision(competitionId, decision)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/applicationfunding/1")
        			.contentType(MediaType.APPLICATION_JSON)
        			.content(new ObjectMapper().writeValueAsString(decision)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }
}
