package com.worth.ifs.application.controller;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.mockito.Mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.transactional.ApplicationSummaryService;

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

    	when(applicationSummaryService.getApplicationSummariesByCompetitionId(Long.valueOf(3), null, 6, 20)).thenReturn(serviceSuccess(resource));
        
    	mockMvc.perform(get("/applicationSummary/findByCompetition/3?page=6"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(resource)));
    	
    	verify(applicationSummaryService).getApplicationSummariesByCompetitionId(Long.valueOf(3), null, 6, 20);
    }
    
    @Test
    public void searchByCompetitionIdWithSortField() throws Exception {
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();

    	when(applicationSummaryService.getApplicationSummariesByCompetitionId(Long.valueOf(3), "id", 6, 20)).thenReturn(serviceSuccess(resource));
        
    	mockMvc.perform(get("/applicationSummary/findByCompetition/3?page=6&sort=id"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(resource)));
    	
    	verify(applicationSummaryService).getApplicationSummariesByCompetitionId(Long.valueOf(3), "id", 6, 20);
    }
    
    @Test
    public void searchSubmittedByCompetitionId() throws Exception {
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();

    	when(applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(Long.valueOf(3), null, 6, 20)).thenReturn(serviceSuccess(resource));
        
    	mockMvc.perform(get("/applicationSummary/findByCompetition/3/submitted?page=6"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(resource)));
    	
    	verify(applicationSummaryService).getSubmittedApplicationSummariesByCompetitionId(Long.valueOf(3), null, 6, 20);
    }
    
    @Test
    public void searchSubmittedByCompetitionIdWithSortField() throws Exception {
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();

    	when(applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(Long.valueOf(3), "id", 6, 20)).thenReturn(serviceSuccess(resource));
        
    	mockMvc.perform(get("/applicationSummary/findByCompetition/3/submitted?page=6&sort=id"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(resource)));
    	
    	verify(applicationSummaryService).getSubmittedApplicationSummariesByCompetitionId(Long.valueOf(3), "id", 6, 20);
    }
    
    @Test
    public void searchNotSubmittedByCompetitionId() throws Exception {
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();

    	when(applicationSummaryService.getNotSubmittedApplicationSummariesByCompetitionId(Long.valueOf(3), null, 6, 20)).thenReturn(serviceSuccess(resource));
        
    	mockMvc.perform(get("/applicationSummary/findByCompetition/3/not-submitted?page=6"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(resource)));
    	
    	verify(applicationSummaryService).getNotSubmittedApplicationSummariesByCompetitionId(Long.valueOf(3), null, 6, 20);
    }
    
    @Test
    public void searchNotSubmittedByCompetitionIdWithSortField() throws Exception {
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();

    	when(applicationSummaryService.getNotSubmittedApplicationSummariesByCompetitionId(Long.valueOf(3), "id", 6, 20)).thenReturn(serviceSuccess(resource));
        
    	mockMvc.perform(get("/applicationSummary/findByCompetition/3/not-submitted?page=6&sort=id"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(resource)));
    	
    	verify(applicationSummaryService).getNotSubmittedApplicationSummariesByCompetitionId(Long.valueOf(3), "id", 6, 20);
    }
    
    @Test
    public void searchFeedbackRequiredByCompetitionId() throws Exception {
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();

    	when(applicationSummaryService.getFeedbackRequiredApplicationSummariesByCompetitionId(Long.valueOf(3), null, 6, 20)).thenReturn(serviceSuccess(resource));
        
    	mockMvc.perform(get("/applicationSummary/findByCompetition/3/feedback-required?page=6"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(resource)));
    	
    	verify(applicationSummaryService).getFeedbackRequiredApplicationSummariesByCompetitionId(Long.valueOf(3), null, 6, 20);
    }
    
    @Test
    public void searchFeedbackRequiredByCompetitionIdWithSortField() throws Exception {
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();

    	when(applicationSummaryService.getFeedbackRequiredApplicationSummariesByCompetitionId(Long.valueOf(3), "id", 6, 20)).thenReturn(serviceSuccess(resource));
        
    	mockMvc.perform(get("/applicationSummary/findByCompetition/3/feedback-required?page=6&sort=id"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(resource)));
    	
    	verify(applicationSummaryService).getFeedbackRequiredApplicationSummariesByCompetitionId(Long.valueOf(3), "id", 6, 20);
    }

}
