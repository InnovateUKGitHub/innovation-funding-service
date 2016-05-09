package com.worth.ifs.controller;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

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

import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.application.service.ApplicationFundingDecisionService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.builder.CompetitionResourceBuilder;
import com.worth.ifs.competition.resource.CompetitionResource;
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
    private CompetitionService competitionService;
    
    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;
    
    private MockMvc mockMvc;
    
    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        
        CompetitionResource competition = CompetitionResourceBuilder.newCompetitionResource().withApplications(Arrays.asList(8L, 9L, 10L)).build();
        when(competitionService.getById(123L)).thenReturn(competition);
    }
    
    @Test
    public void submitFundingDecisionWithoutAllApplications() throws Exception {
    	
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
    public void submitFundingDecisionWithoutAllApplicationsYesOrNo() throws Exception {
    	
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
    public void submitFundingDecision() throws Exception {
    	
    	mockMvc.perform(
    				post("/competition/123/fundingdecision")
    				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
    				.param("8", "Y")
    				.param("9", "N")
    				.param("10", "Y")
    			)
                .andExpect(redirectedUrl("/competition/123"));
    	
    	verifyNoMoreInteractions(cookieFlashMessageFilter);
    	
    	Map<Long, FundingDecision> expectedDecisions = MapFunctions.asMap(8L, FundingDecision.FUNDED,
														    			9L, FundingDecision.NOT_FUNDED,
														    			10L, FundingDecision.FUNDED);
    	verify(applicationFundingDecisionService).makeApplicationFundingDecision(eq(123L), eq(expectedDecisions));
    }
    
    @Test
    public void submitFundingDecisionIrrelaventParamsIgnored() throws Exception {
    	
    	mockMvc.perform(
    				post("/competition/123/fundingdecision")
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
														    			9L, FundingDecision.NOT_FUNDED,
														    			10L, FundingDecision.FUNDED);
    	verify(applicationFundingDecisionService).makeApplicationFundingDecision(eq(123L), eq(expectedDecisions));
    }
}
