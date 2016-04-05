
package com.worth.ifs;

import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.application.service.ApplicationSummaryRestService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.resource.CompetitionResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class CompetitionManagementControllerTest  {

    public static final long COMPETITION_ID = 123L;
    @InjectMocks
	private CompetitionManagementController controller;
	
    @Mock
    private ApplicationSummaryRestService applicationSummaryRestService;
    @Mock
    private CompetitionService competitionService;

    private MockMvc mockMvc;
    private CompetitionResource competition;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        competition = newCompetitionResource().withId(COMPETITION_ID).withName("Competition Name 123").build();
        when(competitionService.getById(eq(COMPETITION_ID))).thenReturn(competition);
    }



    @Test
    public void getByCompetitionId() throws Exception {
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
        CompetitionSummaryResource competitionSummaryResource = new CompetitionSummaryResource();

    	when(applicationSummaryRestService.findByCompetitionId(Long.valueOf(COMPETITION_ID), 0, null)).thenReturn(restSuccess(resource));
    	when(applicationSummaryRestService.findByCompetitionId(Long.valueOf(123L), 0, null)).thenReturn(restSuccess(resource));
        when(applicationSummaryRestService.getCompetitionSummaryByCompetitionId(123L)).thenReturn(restSuccess(competitionSummaryResource));

    	mockMvc.perform(get("/competition/123"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt"))
                .andExpect(model().attribute("currentCompetition", competition))
                .andExpect(model().attribute("results", resource));
    	
    	verify(applicationSummaryRestService).findByCompetitionId(Long.valueOf(COMPETITION_ID), 0, null);
    }
    
    @Test
    public void getByCompetitionIdProvidingPage() throws Exception {
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
        CompetitionSummaryResource competitionSummaryResource = new CompetitionSummaryResource();

    	when(applicationSummaryRestService.findByCompetitionId(Long.valueOf(COMPETITION_ID), 2, null)).thenReturn(restSuccess(resource));
        
    	when(applicationSummaryRestService.findByCompetitionId(Long.valueOf(123L), 2, null)).thenReturn(restSuccess(resource));
        when(applicationSummaryRestService.getCompetitionSummaryByCompetitionId(123L)).thenReturn(restSuccess(competitionSummaryResource));

    	mockMvc.perform(get("/competition/123?page=3"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt"))
                .andExpect(model().attribute("currentCompetition", competition))
                .andExpect(model().attribute("results", resource));
    	
    	verify(applicationSummaryRestService).findByCompetitionId(Long.valueOf(COMPETITION_ID), 2, null);
    }
    
    @Test
    public void getByCompetitionIdProvidingSort() throws Exception {
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
        CompetitionSummaryResource competitionSummaryResource = new CompetitionSummaryResource();

    	when(applicationSummaryRestService.findByCompetitionId(Long.valueOf(COMPETITION_ID), 0, "lead")).thenReturn(restSuccess(resource));
        
    	when(applicationSummaryRestService.findByCompetitionId(Long.valueOf(123L), 0, "lead")).thenReturn(restSuccess(resource));
        when(applicationSummaryRestService.getCompetitionSummaryByCompetitionId(123L)).thenReturn(restSuccess(competitionSummaryResource));

    	mockMvc.perform(get("/competition/123?sort=lead"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt"))
                .andExpect(model().attribute("currentCompetition", competition))
                .andExpect(model().attribute("results", resource));
    	
    	verify(applicationSummaryRestService).findByCompetitionId(Long.valueOf(COMPETITION_ID), 0, "lead");
    }
}