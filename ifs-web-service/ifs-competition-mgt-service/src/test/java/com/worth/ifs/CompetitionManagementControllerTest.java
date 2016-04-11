
package com.worth.ifs;

import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ClosedCompetitionSubmittedApplicationSummaryPageResource;
import com.worth.ifs.application.resource.ClosedCompetitionNotSubmittedApplicationSummaryPageResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.application.service.ApplicationSummaryService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.resource.CompetitionResource.Status;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class CompetitionManagementControllerTest  {

    public static final Long COMPETITION_ID = Long.valueOf(123L);
    
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
    	
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
        CompetitionSummaryResource competitionSummaryResource = new CompetitionSummaryResource();
        competitionSummaryResource.setCompetitionStatus(Status.OPEN);

    	when(applicationSummaryService.findByCompetitionId(COMPETITION_ID, 0, null)).thenReturn(resource);
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(COMPETITION_ID)).thenReturn(competitionSummaryResource);

    	mockMvc.perform(get("/competition/123"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt"))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource))
                .andExpect(model().attribute("results", resource))
                .andExpect(model().attribute("activeSortField", "percentageComplete"));
    	
    	verify(applicationSummaryService).findByCompetitionId(COMPETITION_ID, 0, null);
    	verify(applicationSummaryService).getCompetitionSummaryByCompetitionId(COMPETITION_ID);
}
    
    @Test
    public void getByCompetitionIdForOpenCompetitionProvidingPage() throws Exception {
    	
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
        CompetitionSummaryResource competitionSummaryResource = new CompetitionSummaryResource();
        competitionSummaryResource.setCompetitionStatus(Status.OPEN);

    	when(applicationSummaryService.findByCompetitionId(COMPETITION_ID, 2, null)).thenReturn(resource);
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(COMPETITION_ID)).thenReturn(competitionSummaryResource);

    	mockMvc.perform(get("/competition/123?page=3"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt"))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource))
                .andExpect(model().attribute("results", resource))
                .andExpect(model().attribute("activeSortField", "percentageComplete"));
    	
    	verify(applicationSummaryService).findByCompetitionId(COMPETITION_ID, 2, null);
    	verify(applicationSummaryService).getCompetitionSummaryByCompetitionId(COMPETITION_ID);
    }
    
    @Test
    public void getByCompetitionIdProvidingSort() throws Exception {
    	
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
        CompetitionSummaryResource competitionSummaryResource = new CompetitionSummaryResource();
        competitionSummaryResource.setCompetitionStatus(Status.OPEN);
        
    	when(applicationSummaryService.findByCompetitionId(COMPETITION_ID, 0, "lead")).thenReturn(resource);
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(COMPETITION_ID)).thenReturn(competitionSummaryResource);

    	mockMvc.perform(get("/competition/123?sort=lead"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt"))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource))
                .andExpect(model().attribute("results", resource))
                .andExpect(model().attribute("activeSortField", "lead"));
    	
    	verify(applicationSummaryService).findByCompetitionId(COMPETITION_ID, 0, "lead");
    	verify(applicationSummaryService).getCompetitionSummaryByCompetitionId(COMPETITION_ID);
    }
    
    @Test
    public void getByCompetitionIdForCompetitionInAssessmentSubmittedIsDefault() throws Exception {
    	
    	CompetitionSummaryResource competitionSummaryResource = new CompetitionSummaryResource();
    	competitionSummaryResource.setCompetitionStatus(Status.IN_ASSESSMENT);
    	 
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
    	ClosedCompetitionSubmittedApplicationSummaryPageResource summary = new ClosedCompetitionSubmittedApplicationSummaryPageResource();

    	when(applicationSummaryService.findByCompetitionId(COMPETITION_ID, 0, null)).thenReturn(resource);
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(COMPETITION_ID)).thenReturn(competitionSummaryResource);

        when(applicationSummaryService.getSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(COMPETITION_ID, 0, null)).thenReturn(summary);

    	mockMvc.perform(get("/competition/123"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-in-assessment"))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource))
                .andExpect(model().attribute("results", summary))
                .andExpect(model().attribute("activeTab", "submitted"));
    	
    	verify(applicationSummaryService).getSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(COMPETITION_ID, 0, null);
    	verify(applicationSummaryService).getCompetitionSummaryByCompetitionId(COMPETITION_ID);
    }
    
    @Test
    public void getByCompetitionIdForCompetitionInAssessmentSubmittedRequested() throws Exception {
    	
    	CompetitionSummaryResource competitionSummaryResource = new CompetitionSummaryResource();
    	competitionSummaryResource.setCompetitionStatus(Status.IN_ASSESSMENT);
    	 
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
    	ClosedCompetitionSubmittedApplicationSummaryPageResource summary = new ClosedCompetitionSubmittedApplicationSummaryPageResource();

    	when(applicationSummaryService.findByCompetitionId(COMPETITION_ID, 0, null)).thenReturn(resource);
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(COMPETITION_ID)).thenReturn(competitionSummaryResource);

        when(applicationSummaryService.getSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(COMPETITION_ID, 0, null)).thenReturn(summary);

    	mockMvc.perform(get("/competition/123?tab=submitted"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-in-assessment"))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource))
                .andExpect(model().attribute("results", summary))
                .andExpect(model().attribute("activeTab", "submitted"));
    	
    	verify(applicationSummaryService).getSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(COMPETITION_ID, 0, null);
    	verify(applicationSummaryService).getCompetitionSummaryByCompetitionId(COMPETITION_ID);
    }
    
    @Test
    public void getByCompetitionIdForCompetitionInAssessmentNotSubmittedRequested() throws Exception {
    	
    	CompetitionSummaryResource competitionSummaryResource = new CompetitionSummaryResource();
    	competitionSummaryResource.setCompetitionStatus(Status.IN_ASSESSMENT);
    	 
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
    	ClosedCompetitionNotSubmittedApplicationSummaryPageResource summary = new ClosedCompetitionNotSubmittedApplicationSummaryPageResource();

    	when(applicationSummaryService.findByCompetitionId(COMPETITION_ID, 0, null)).thenReturn(resource);
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(COMPETITION_ID)).thenReturn(competitionSummaryResource);

        when(applicationSummaryService.getNotSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(COMPETITION_ID, 0, null)).thenReturn(summary);

    	mockMvc.perform(get("/competition/123?tab=notSubmitted"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-in-assessment"))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource))
                .andExpect(model().attribute("results", summary))
                .andExpect(model().attribute("activeTab", "notSubmitted"));
    	
    	verify(applicationSummaryService).getNotSubmittedApplicationSummariesForClosedCompetitionByCompetitionId(COMPETITION_ID, 0, null);
    	verify(applicationSummaryService).getCompetitionSummaryByCompetitionId(COMPETITION_ID);
    }
}