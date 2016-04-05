
package com.worth.ifs;

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
import com.worth.ifs.application.resource.ClosedCompetitionApplicationSummaryPageResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.application.service.ApplicationSummaryService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionResource.Status;

@RunWith(MockitoJUnitRunner.class)
public class CompetitionManagementControllerTest  {

	@InjectMocks
	private CompetitionManagementController controller;
	
    @Mock
    private ApplicationSummaryService applicationSummaryService;
    
    @Mock
    private CompetitionService competitionService;

    private MockMvc mockMvc;
    
    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }
    
    @Test
    public void getByCompetitionIdForOpenCompetition() throws Exception {
    	
    	CompetitionResource competition = new CompetitionResource();
    	competition.setCompetitionStatus(Status.OPEN);
    	when(competitionService.getById(Long.valueOf(123L))).thenReturn(competition);
    	
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
        CompetitionSummaryResource competitionSummaryResource = new CompetitionSummaryResource();

    	when(applicationSummaryService.findByCompetitionId(Long.valueOf(123L), 0, null)).thenReturn(resource);
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(123L)).thenReturn(competitionSummaryResource);

    	mockMvc.perform(get("/competition/123"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt"))
                .andExpect(model().attribute("results", resource))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource));
    	
    	verify(applicationSummaryService).findByCompetitionId(Long.valueOf(123L), 0, null);
    	verify(applicationSummaryService).getCompetitionSummaryByCompetitionId(Long.valueOf(123L));
}
    
    @Test
    public void getByCompetitionIdForOpenCompetitionProvidingPage() throws Exception {
    	
    	CompetitionResource competition = new CompetitionResource();
    	competition.setCompetitionStatus(Status.OPEN);
    	when(competitionService.getById(Long.valueOf(123L))).thenReturn(competition);
    	
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
        CompetitionSummaryResource competitionSummaryResource = new CompetitionSummaryResource();

    	when(applicationSummaryService.findByCompetitionId(Long.valueOf(123L), 2, null)).thenReturn(resource);
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(123L)).thenReturn(competitionSummaryResource);

    	mockMvc.perform(get("/competition/123?page=3"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt"))
                .andExpect(model().attribute("results", resource))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource));
    	
    	verify(applicationSummaryService).findByCompetitionId(Long.valueOf(123L), 2, null);
    	verify(applicationSummaryService).getCompetitionSummaryByCompetitionId(Long.valueOf(123L));
    }
    
    @Test
    public void getByCompetitionIdProvidingSort() throws Exception {
    	CompetitionResource competition = new CompetitionResource();
    	competition.setCompetitionStatus(Status.OPEN);
    	when(competitionService.getById(Long.valueOf(123L))).thenReturn(competition);
    	
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
        CompetitionSummaryResource competitionSummaryResource = new CompetitionSummaryResource();

    	when(applicationSummaryService.findByCompetitionId(Long.valueOf(123L), 0, "lead")).thenReturn(resource);
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(123L)).thenReturn(competitionSummaryResource);

    	mockMvc.perform(get("/competition/123?sort=lead"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt"))
                .andExpect(model().attribute("results", resource))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource));
    	
    	verify(applicationSummaryService).findByCompetitionId(Long.valueOf(123L), 0, "lead");
    	verify(applicationSummaryService).getCompetitionSummaryByCompetitionId(Long.valueOf(123L));
}
    
    @Test
    public void getByCompetitionIdForCompetitionInAssessment() throws Exception {
    	
    	CompetitionResource competition = new CompetitionResource();
    	competition.setCompetitionStatus(Status.IN_ASSESSMENT);
    	when(competitionService.getById(Long.valueOf(123L))).thenReturn(competition);
    	
    	CompetitionSummaryResource competitionSummaryResource = new CompetitionSummaryResource();

    	 
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
    	ClosedCompetitionApplicationSummaryPageResource summary1 = new ClosedCompetitionApplicationSummaryPageResource();
    	ClosedCompetitionApplicationSummaryPageResource summary2 = new ClosedCompetitionApplicationSummaryPageResource();

    	when(applicationSummaryService.findByCompetitionId(Long.valueOf(123L), 0, null)).thenReturn(resource);
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(123L)).thenReturn(competitionSummaryResource);

        when(applicationSummaryService.getSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(Long.valueOf(123L), 0, null)).thenReturn(summary1);
        when(applicationSummaryService.getNotSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(Long.valueOf(123L), 0, null)).thenReturn(summary2);

    	mockMvc.perform(get("/competition/123"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-in-assessment"))
                .andExpect(model().attribute("submittedResults", summary1))
                .andExpect(model().attribute("notSubmittedResults", summary2))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource));
    	
    	verify(applicationSummaryService).getSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(Long.valueOf(123L), 0, null);
    	verify(applicationSummaryService).getNotSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(Long.valueOf(123L), 0, null);
    	verify(applicationSummaryService).getCompetitionSummaryByCompetitionId(Long.valueOf(123L));
    }
}