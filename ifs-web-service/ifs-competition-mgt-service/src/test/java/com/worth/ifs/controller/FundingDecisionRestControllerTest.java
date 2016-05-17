package com.worth.ifs.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.springframework.http.MediaType;

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
    public void saveFundingDecision() throws Exception {
    	
    	mockMvc.perform(
    				post("/funding/123")
    				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
    				.param("applicationId", "3")
    				.param("fundingDecision", "Y")
    			)
                .andExpect(jsonPath("$.success", is("true")));
    	
    	verify(applicationFundingDecisionService).saveApplicationFundingDecisionData(eq(123L), argThat(new ArgumentMatcher<Map<Long, FundingDecision>> (){

			@SuppressWarnings("unchecked")
			@Override
			public boolean matches(Object argument) {
				Map<Long, FundingDecision> arg = (Map<Long, FundingDecision>) argument;
				return arg.size() == 1 && arg.containsKey(3L) && FundingDecision.FUNDED.equals(arg.get(3L));
			}}));
    }
	
}
