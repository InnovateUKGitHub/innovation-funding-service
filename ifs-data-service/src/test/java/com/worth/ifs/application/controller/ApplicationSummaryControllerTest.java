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
import com.worth.ifs.application.resource.ClosedCompetitionNotSubmittedApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ClosedCompetitionSubmittedApplicationSummaryPageResource;
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

    	when(applicationSummaryService.getApplicationSummariesByCompetitionId(Long.valueOf(3), 6, null)).thenReturn(serviceSuccess(resource));
        
    	mockMvc.perform(get("/applicationSummary/findByCompetition/3?page=6"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(resource)));
    	
    	verify(applicationSummaryService).getApplicationSummariesByCompetitionId(Long.valueOf(3), 6, null);
    }
    
    @Test
    public void searchByCompetitionIdWithSortField() throws Exception {
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();

    	when(applicationSummaryService.getApplicationSummariesByCompetitionId(Long.valueOf(3), 6, "id")).thenReturn(serviceSuccess(resource));
        
    	mockMvc.perform(get("/applicationSummary/findByCompetition/3?page=6&sort=id"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(resource)));
    	
    	verify(applicationSummaryService).getApplicationSummariesByCompetitionId(Long.valueOf(3), 6, "id");
    }
    
    @Test
    public void searchSubmittedByClosedCompetitionId() throws Exception {
    	ClosedCompetitionSubmittedApplicationSummaryPageResource resource = new ClosedCompetitionSubmittedApplicationSummaryPageResource();

    	when(applicationSummaryService.getSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(Long.valueOf(3), 6, null)).thenReturn(serviceSuccess(resource));
        
    	mockMvc.perform(get("/applicationSummary/findByClosedCompetition/3/submitted?page=6"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(resource)));
    	
    	verify(applicationSummaryService).getSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(Long.valueOf(3), 6, null);
    }
    
    @Test
    public void searchSubmittedByClosedCompetitionIdWithSortField() throws Exception {
    	ClosedCompetitionSubmittedApplicationSummaryPageResource resource = new ClosedCompetitionSubmittedApplicationSummaryPageResource();

    	when(applicationSummaryService.getSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(Long.valueOf(3), 6, "id")).thenReturn(serviceSuccess(resource));
        
    	mockMvc.perform(get("/applicationSummary/findByClosedCompetition/3/submitted?page=6&sort=id"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(resource)));
    	
    	verify(applicationSummaryService).getSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(Long.valueOf(3), 6, "id");
    }
    
    @Test
    public void searchNotSubmittedByClosedCompetitionId() throws Exception {
    	ClosedCompetitionNotSubmittedApplicationSummaryPageResource resource = new ClosedCompetitionNotSubmittedApplicationSummaryPageResource();

    	when(applicationSummaryService.getNotSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(Long.valueOf(3), 6, null)).thenReturn(serviceSuccess(resource));
        
    	mockMvc.perform(get("/applicationSummary/findByClosedCompetition/3/not-submitted?page=6"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(resource)));
    	
    	verify(applicationSummaryService).getNotSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(Long.valueOf(3), 6, null);
    }
    
    @Test
    public void searchNotSubmittedByClosedCompetitionIdWithSortField() throws Exception {
    	ClosedCompetitionNotSubmittedApplicationSummaryPageResource resource = new ClosedCompetitionNotSubmittedApplicationSummaryPageResource();

    	when(applicationSummaryService.getNotSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(Long.valueOf(3), 6, "id")).thenReturn(serviceSuccess(resource));
        
    	mockMvc.perform(get("/applicationSummary/findByClosedCompetition/3/not-submitted?page=6&sort=id"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(resource)));
    	
    	verify(applicationSummaryService).getNotSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(Long.valueOf(3), 6, "id");
    }

}
