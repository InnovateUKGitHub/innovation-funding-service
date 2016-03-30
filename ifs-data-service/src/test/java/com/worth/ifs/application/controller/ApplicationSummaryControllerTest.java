package com.worth.ifs.application.controller;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.mockito.Mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ApplicationSummaryResource;
import com.worth.ifs.application.transactional.ApplicationSummaryService;

public class ApplicationSummaryControllerTest extends BaseControllerMockMVCTest<ApplicationSummaryController> {

    @Mock
    protected ApplicationSummaryService applicationSummaryService;

    @Override
    protected ApplicationSummaryController supplyControllerUnderTest() {
        return new ApplicationSummaryController();
    }

    @Test
    public void getById() throws Exception {
    	ApplicationSummaryResource resource = new ApplicationSummaryResource();

    	when(applicationSummaryService.getApplicationSummaryById(Long.valueOf(123L))).thenReturn(serviceSuccess(resource));
        
    	mockMvc.perform(get("/applicationSummary/123"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(resource)));
    	
    	verify(applicationSummaryService).getApplicationSummaryById(Long.valueOf(123L));
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

}
