package com.worth.ifs.controller;

import static com.worth.ifs.util.MapFunctions.asMap;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.application.service.ApplicationFundingDecisionService;

public class FundingDecisionRestControllerTest extends BaseControllerMockMVCTest<FundingDecisionRestController> {

	@Override
	protected FundingDecisionRestController supplyControllerUnderTest() {
		return new FundingDecisionRestController();
	}
	
    @Mock
    private ApplicationFundingDecisionService applicationFundingDecisionService;
    
    @Before
    public void setup() {
        when(applicationFundingDecisionService.fundingDecisionForString("Y")).thenReturn(FundingDecision.FUNDED);
    }
    
	@Test
	@Ignore
    public void saveFundingDecision() throws Exception {
    	
		Map<String, String> request = asMap("applicationId", 3, "fundingDecision", "Y");
		String jsonRequestBody = new ObjectMapper().writeValueAsString(request);
		
    	mockMvc.perform(
    				post("/funding/123")
    				.contentType(MediaType.APPLICATION_JSON)
    				.accept(MediaType.APPLICATION_JSON)
    				.content(jsonRequestBody)
    			)
                .andExpect(jsonPath("$.success", is("true")));
    	
    	verify(applicationFundingDecisionService).saveApplicationFundingDecisionData(eq(123L), eq(asMap(3, FundingDecision.FUNDED)));
    }

}
