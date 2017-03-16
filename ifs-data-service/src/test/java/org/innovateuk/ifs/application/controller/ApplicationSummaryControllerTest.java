package org.innovateuk.ifs.application.controller;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.innovateuk.ifs.application.domain.FundingDecisionStatus.FUNDED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.mockito.Mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.transactional.ApplicationSummaryService;

public class ApplicationSummaryControllerTest extends BaseControllerMockMVCTest<ApplicationSummaryController> {

    @Mock
    protected ApplicationSummaryService applicationSummaryService;

    @Override
    protected ApplicationSummaryController supplyControllerUnderTest() {
        return new ApplicationSummaryController();
    }
    
    @Test
    public void searchByCompetitionId() throws Exception {
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();

    	when(applicationSummaryService.getApplicationSummariesByCompetitionId(Long.valueOf(3), null, 6, 20, empty())).thenReturn(serviceSuccess(resource));
        
    	mockMvc.perform(get("/applicationSummary/findByCompetition/3?page=6"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resource)));
    	
    	verify(applicationSummaryService).getApplicationSummariesByCompetitionId(Long.valueOf(3), null, 6, 20, empty());
    }
    
    @Test
    public void searchByCompetitionIdWithSortField() throws Exception {
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();

    	when(applicationSummaryService.getApplicationSummariesByCompetitionId(Long.valueOf(3), "id", 6, 20,empty())).thenReturn(serviceSuccess(resource));
        
    	mockMvc.perform(get("/applicationSummary/findByCompetition/3?page=6&sort=id"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resource)));
    	
    	verify(applicationSummaryService).getApplicationSummariesByCompetitionId(Long.valueOf(3), "id", 6, 20, empty());
    }

    @Test
	public void searchByCompetitionIdWithFilter() throws Exception {
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
    	 when(applicationSummaryService.getApplicationSummariesByCompetitionId(3L, null, 6, 20, of("filter"))).thenReturn(serviceSuccess(resource));

    	 mockMvc.perform(get("/applicationSummary/findByCompetition/3?page=6&filter=filter"))
				 .andExpect(status().isOk())
				 .andExpect(content().json(objectMapper.writeValueAsString(resource)));

		verify(applicationSummaryService).getApplicationSummariesByCompetitionId(3L, null, 6,20, of("filter"));
	}
    
    @Test
    public void searchSubmittedByCompetitionId() throws Exception {
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();

    	when(applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(Long.valueOf(3), null, 6, 20, empty(), empty())).thenReturn(serviceSuccess(resource));
        
    	mockMvc.perform(get("/applicationSummary/findByCompetition/3/submitted?page=6"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resource)));
    	
    	verify(applicationSummaryService).getSubmittedApplicationSummariesByCompetitionId(Long.valueOf(3), null, 6, 20, empty(), empty());
    }
    
    @Test
    public void searchSubmittedByCompetitionIdWithSortField() throws Exception {
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();

    	when(applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(Long.valueOf(3), "id", 6, 20, empty(), empty())).thenReturn(serviceSuccess(resource));
        
    	mockMvc.perform(get("/applicationSummary/findByCompetition/3/submitted?page=6&sort=id"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resource)));
    	
    	verify(applicationSummaryService).getSubmittedApplicationSummariesByCompetitionId(Long.valueOf(3), "id", 6, 20, empty(), empty());
    }
     @Test
    public void searchSubmittedByCompetitionIdWithFilter() throws Exception {
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();

    	when(applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(Long.valueOf(3), null, 6, 20, of("filter"), of(FUNDED))).thenReturn(serviceSuccess(resource));

    	mockMvc.perform(get("/applicationSummary/findByCompetition/3/submitted?page=6&filter=filter&fundingFilter=FUNDED"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resource)));

    	verify(applicationSummaryService).getSubmittedApplicationSummariesByCompetitionId(Long.valueOf(3), null, 6, 20, of("filter"), of(FUNDED));
    }

    @Test
    public void searchNotSubmittedByCompetitionId() throws Exception {
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();

    	when(applicationSummaryService.getNotSubmittedApplicationSummariesByCompetitionId(Long.valueOf(3), null, 6, 20)).thenReturn(serviceSuccess(resource));
        
    	mockMvc.perform(get("/applicationSummary/findByCompetition/3/not-submitted?page=6"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resource)));
    	
    	verify(applicationSummaryService).getNotSubmittedApplicationSummariesByCompetitionId(Long.valueOf(3), null, 6, 20);
    }
    
    @Test
    public void searchNotSubmittedByCompetitionIdWithSortField() throws Exception {
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();

    	when(applicationSummaryService.getNotSubmittedApplicationSummariesByCompetitionId(Long.valueOf(3), "id", 6, 20)).thenReturn(serviceSuccess(resource));
        
    	mockMvc.perform(get("/applicationSummary/findByCompetition/3/not-submitted?page=6&sort=id"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resource)));
    	
    	verify(applicationSummaryService).getNotSubmittedApplicationSummariesByCompetitionId(Long.valueOf(3), "id", 6, 20);
    }
    
    @Test
    public void searchFeedbackRequiredByCompetitionId() throws Exception {
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();

    	when(applicationSummaryService.getFeedbackRequiredApplicationSummariesByCompetitionId(Long.valueOf(3), null, 6, 20)).thenReturn(serviceSuccess(resource));
        
    	mockMvc.perform(get("/applicationSummary/findByCompetition/3/feedback-required?page=6"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resource)));
    	
    	verify(applicationSummaryService).getFeedbackRequiredApplicationSummariesByCompetitionId(Long.valueOf(3), null, 6, 20);
    }
    
    @Test
    public void searchFeedbackRequiredByCompetitionIdWithSortField() throws Exception {
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();

    	when(applicationSummaryService.getFeedbackRequiredApplicationSummariesByCompetitionId(Long.valueOf(3), "id", 6, 20)).thenReturn(serviceSuccess(resource));
        
    	mockMvc.perform(get("/applicationSummary/findByCompetition/3/feedback-required?page=6&sort=id"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(resource)));
    	
    	verify(applicationSummaryService).getFeedbackRequiredApplicationSummariesByCompetitionId(Long.valueOf(3), "id", 6, 20);
    }

	@Test
	public void searchWithFundingDecisionByCompetitionId() throws Exception {
		ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();

		when(applicationSummaryService.getWithFundingDecisionApplicationSummariesByCompetitionId(Long.valueOf(3), null, 6, 20)).thenReturn(serviceSuccess(resource));

		mockMvc.perform(get("/applicationSummary/findByCompetition/3/with-funding-decision?page=6"))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(resource)));

		verify(applicationSummaryService).getWithFundingDecisionApplicationSummariesByCompetitionId(Long.valueOf(3), null, 6, 20);
	}

}
