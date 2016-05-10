package com.worth.ifs.controller;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ApplicationSummaryResource;
import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.application.service.ApplicationFundingDecisionService;
import com.worth.ifs.application.service.ApplicationSummaryService;
import com.worth.ifs.filter.CookieFlashMessageFilter;
import com.worth.ifs.util.MapFunctions;

@RunWith(MockitoJUnitRunner.class)
public class FundingDecisionControllerTest {

	public static final Long COMPETITION_ID = Long.valueOf(123L);
    
    @InjectMocks
	private FundingDecisionController controller;
	
    @Mock
    private ApplicationFundingDecisionService applicationFundingDecisionService;
    
    @Mock
    private ApplicationSummaryService applicationSummaryService;
    
    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;
    
    private MockMvc mockMvc;
    
    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        
        ApplicationSummaryPageResource applicationSummaries = new ApplicationSummaryPageResource();
        ApplicationSummaryResource app8 = app(8L);
        ApplicationSummaryResource app9 = app(9L);
        ApplicationSummaryResource app10 = app(10L);
        applicationSummaries.setContent(Arrays.asList(app8, app9, app10));
        when(applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(123L, null, 0, Integer.MAX_VALUE)).thenReturn(applicationSummaries);
    }
    
    private ApplicationSummaryResource app(Long id) {
		ApplicationSummaryResource app = new ApplicationSummaryResource();
		app.setId(id);
		return app;
	}

	@Test
    public void submitFundingDecisionWithoutAllApplications() throws Exception {
    	
    	mockMvc.perform(
    				post("/competition/123/fundingdecisionsubmit")
    				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
    				.param("8", "Y")
    				.param("9", "N")
    			)
                .andExpect(redirectedUrl("/competition/123"));
    	
    	verifyNoMoreInteractions(applicationFundingDecisionService);
    	verify(cookieFlashMessageFilter).setFlashMessage(isA(HttpServletResponse.class), eq("fundingNotDecidedForAllApplications"));
    }
    
    @Test
    public void submitFundingDecisionWithoutAllApplicationsYesOrNo() throws Exception {
    	
    	mockMvc.perform(
    				post("/competition/123/fundingdecisionsubmit")
    				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
    				.param("8", "Y")
    				.param("9", "N")
    				.param("10", "-")
    			)
                .andExpect(redirectedUrl("/competition/123"));
    	
    	verifyNoMoreInteractions(applicationFundingDecisionService);
    	verify(cookieFlashMessageFilter).setFlashMessage(isA(HttpServletResponse.class), eq("fundingNotDecidedForAllApplications"));
    }
    
    @Test
    public void submitFundingDecision() throws Exception {
    	
    	mockMvc.perform(
    				post("/competition/123/fundingdecisionsubmit")
    				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
    				.param("8", "Y")
    				.param("9", "N")
    				.param("10", "Y")
    			)
                .andExpect(redirectedUrl("/competition/123"));
    	
    	verifyNoMoreInteractions(cookieFlashMessageFilter);
    	
    	Map<Long, FundingDecision> expectedDecisions = MapFunctions.asMap(8L, FundingDecision.FUNDED,
														    			9L, FundingDecision.UNFUNDED,
														    			10L, FundingDecision.FUNDED);
    	verify(applicationFundingDecisionService).makeApplicationFundingDecision(eq(123L), eq(expectedDecisions));
    }
    
    @Test
    public void submitFundingDecisionIrrelaventParamsIgnored() throws Exception {
    	
    	mockMvc.perform(
    				post("/competition/123/fundingdecisionsubmit")
    				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
    				.param("8", "Y")
    				.param("9", "N")
    				.param("10", "Y")
    				.param("something", "irrelevant")
    				.param("11", "N")
    			)
                .andExpect(redirectedUrl("/competition/123"));
    	
    	verifyNoMoreInteractions(cookieFlashMessageFilter);
    	
    	Map<Long, FundingDecision> expectedDecisions = MapFunctions.asMap(8L, FundingDecision.FUNDED,
														    			9L, FundingDecision.UNFUNDED,
														    			10L, FundingDecision.FUNDED);
    	verify(applicationFundingDecisionService).makeApplicationFundingDecision(eq(123L), eq(expectedDecisions));
    }
    
    @Test
    public void fundingDecisionWithoutAllApplications() throws Exception {
    	
    	mockMvc.perform(
    				post("/competition/123/fundingdecision")
    				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
    				.param("8", "Y")
    				.param("9", "N")
    			)
                .andExpect(redirectedUrl("/competition/123"));
    	
    	verifyNoMoreInteractions(applicationFundingDecisionService);
    	verify(cookieFlashMessageFilter).setFlashMessage(isA(HttpServletResponse.class), eq("fundingNotDecidedForAllApplications"));
    }
    
    @Test
    public void fundingDecisionWithoutAllApplicationsYesOrNo() throws Exception {
    	
    	mockMvc.perform(
    				post("/competition/123/fundingdecision")
    				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
    				.param("8", "Y")
    				.param("9", "N")
    				.param("10", "-")
    			)
                .andExpect(redirectedUrl("/competition/123"));
    	
    	verifyNoMoreInteractions(applicationFundingDecisionService);
    	verify(cookieFlashMessageFilter).setFlashMessage(isA(HttpServletResponse.class), eq("fundingNotDecidedForAllApplications"));
    }
    
    @Test
    public void fundingDecision() throws Exception {
    	
    	Map<Long, FundingDecision> expectedDecisions = MapFunctions.asMap(8L, FundingDecision.FUNDED,
														    			9L, FundingDecision.UNFUNDED,
														    			10L, FundingDecision.FUNDED);
    	
    	mockMvc.perform(
    				post("/competition/123/fundingdecision")
    				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
    				.param("8", "Y")
    				.param("9", "N")
    				.param("10", "Y")
    			)
                .andExpect(view().name("funding-decision-confirmation"))
                .andExpect(model().attribute("competitionId", 123L))
                .andExpect(model().attribute("applicationFundingDecisions", expectedDecisions));

    	
    	verifyNoMoreInteractions(cookieFlashMessageFilter);
    	verifyNoMoreInteractions(applicationFundingDecisionService);
    }
}
