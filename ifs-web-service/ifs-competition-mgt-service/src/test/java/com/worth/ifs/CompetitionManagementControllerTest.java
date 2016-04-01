
package com.worth.ifs;

import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.service.ApplicationSummaryRestService;

@RunWith(MockitoJUnitRunner.class)
public class CompetitionManagementControllerTest  {

	@InjectMocks
	private CompetitionManagementController controller;
	
    @Mock
    private ApplicationSummaryRestService applicationSummaryRestService;

    private MockMvc mockMvc;
    
    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }
    
    @Test
    public void getByCompetitionId() throws Exception {
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();

    	when(applicationSummaryRestService.findByCompetitionId(Long.valueOf(123L), 0, null)).thenReturn(restSuccess(resource));
        
    	mockMvc.perform(get("/competition/123"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt"))
                .andExpect(model().attribute("results", resource));
    	
    	verify(applicationSummaryRestService).findByCompetitionId(Long.valueOf(123L), 0, null);
    }
    
    @Test
    public void getByCompetitionIdProvidingPage() throws Exception {
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();

    	when(applicationSummaryRestService.findByCompetitionId(Long.valueOf(123L), 2, null)).thenReturn(restSuccess(resource));
        
    	mockMvc.perform(get("/competition/123?page=3"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt"))
                .andExpect(model().attribute("results", resource));
    	
    	verify(applicationSummaryRestService).findByCompetitionId(Long.valueOf(123L), 2, null);
    }
    
    @Test
    public void getByCompetitionIdProvidingSort() throws Exception {
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();

    	when(applicationSummaryRestService.findByCompetitionId(Long.valueOf(123L), 0, "lead")).thenReturn(restSuccess(resource));
        
    	mockMvc.perform(get("/competition/123?sort=lead"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt"))
                .andExpect(model().attribute("results", resource));
    	
    	verify(applicationSummaryRestService).findByCompetitionId(Long.valueOf(123L), 0, "lead");
    }
}