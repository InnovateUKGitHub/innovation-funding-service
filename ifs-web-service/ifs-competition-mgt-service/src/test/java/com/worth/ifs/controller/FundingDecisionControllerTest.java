package com.worth.ifs.controller;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.application.service.ApplicationFundingDecisionService;
import com.worth.ifs.util.MapFunctions;

@RunWith(MockitoJUnitRunner.class)
public class FundingDecisionControllerTest {

	public static final Long COMPETITION_ID = Long.valueOf(123L);
    
    @InjectMocks
	private FundingDecisionController controller;
	
    @Mock
    private ApplicationFundingDecisionService applicationFundingDecisionService;
    
    private MockMvc mockMvc;
    
    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }
    
    @Test
    public void submitFundingDecision() throws Exception {
    	
    	mockMvc.perform(
    				post("/competition/123/fundingdecision")
    				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
    				.param("8", "Y")
    				.param("9", "N")
    				.param("10", "Y")
    				.param("something", "irrelevant")
    			)
                .andExpect(redirectedUrl("/competition/123"));
    	
    	Map<Long, FundingDecision> expectedDecisions = MapFunctions.asMap(8L, FundingDecision.FUNDED,
														    			9L, FundingDecision.NOT_FUNDED,
														    			10L, FundingDecision.FUNDED);
    	verify(applicationFundingDecisionService).makeApplicationFundingDecision(eq(123L), eq(expectedDecisions));
    }
}
