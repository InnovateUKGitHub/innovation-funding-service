package com.worth.ifs.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.commons.rest.RestErrorResponse;
import com.worth.ifs.util.MapFunctions;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static com.worth.ifs.util.JsonMappingUtil.toJson;
import static com.worth.ifs.commons.error.CommonErrors.internalServerErrorError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationFundingDecisionControllerTest extends BaseControllerMockMVCTest<ApplicationFundingDecisionController> {

    @Override
    protected ApplicationFundingDecisionController supplyControllerUnderTest() {
        return new ApplicationFundingDecisionController();
    }

    @Test
    public void applicationFundingDecisionControllerShouldReturnAppropriateStatusCode() throws Exception {
        Long competitionId = 1L;
        Map<Long, FundingDecision> decision = MapFunctions.asMap(1L, FundingDecision.FUNDED, 2L, FundingDecision.UNFUNDED);

        when(applicationFundingServiceMock.makeFundingDecision(competitionId, decision)).thenReturn(serviceSuccess());
        when(applicationFundingServiceMock.notifyLeadApplicantsOfFundingDecisions(competitionId, decision)).thenReturn(serviceSuccess());
        when(projectServiceMock.createProjectsFromFundingDecisions(decision)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/applicationfunding/1/submit")
        			.contentType(MediaType.APPLICATION_JSON)
        			.content(new ObjectMapper().writeValueAsString(decision)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    public void makeFundingDecisionButErrorOccursSendingNotifications() throws Exception {
        Long competitionId = 1L;
        Map<Long, FundingDecision> decision = MapFunctions.asMap(1L, FundingDecision.FUNDED, 2L, FundingDecision.UNFUNDED);

        when(applicationFundingServiceMock.makeFundingDecision(competitionId, decision)).thenReturn(serviceSuccess());
        when(applicationFundingServiceMock.notifyLeadApplicantsOfFundingDecisions(competitionId, decision)).thenReturn(serviceFailure(internalServerErrorError()));
        when(projectServiceMock.createProjectsFromFundingDecisions(decision)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/applicationfunding/1/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(decision)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(toJson(new RestErrorResponse(internalServerErrorError()))));
    }
    
    @Test
    public void testSaveApplicationFundingDecisionData() throws Exception {
        Long competitionId = 1L;
        Map<Long, FundingDecision> decision = MapFunctions.asMap(1L, FundingDecision.FUNDED, 2L, FundingDecision.UNFUNDED);

        when(applicationFundingServiceMock.saveFundingDecisionData(competitionId, decision)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/applicationfunding/1")
        			.contentType(MediaType.APPLICATION_JSON)
        			.content(new ObjectMapper().writeValueAsString(decision)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }
    
}
