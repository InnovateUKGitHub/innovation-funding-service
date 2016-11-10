
package com.worth.ifs.controller;

import static com.worth.ifs.application.builder.CompetitionSummaryResourceBuilder.newCompetitionSummaryResource;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDateTime;

import com.worth.ifs.competition.controller.CompetitionManagementController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.worth.ifs.application.resource.ApplicationSummaryPageResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.application.service.ApplicationSummaryService;
import com.worth.ifs.application.service.AssessorFeedbackService;
import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.competition.resource.CompetitionStatus;
import com.worth.ifs.competition.service.ApplicationSummarySortFieldService;

@RunWith(MockitoJUnitRunner.class)
public class CompetitionManagementControllerTest  {

    public static final Long COMPETITION_ID = Long.valueOf(123L);
    
    @InjectMocks
	private CompetitionManagementController controller;
	
    @Mock
    private ApplicationSummaryService applicationSummaryService;
    
    @Mock
    private CompetitionService competitionService;

    @Mock
    private ApplicationSummarySortFieldService applicationSummarySortFieldService;
    
    @Mock
    private AssessorFeedbackService assessorFeedbackService;
    
    private MockMvc mockMvc;
    
    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }
    
    @Test
    public void getByCompetitionIdForOpenCompetition() throws Exception {
        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(Status.OPEN).build();
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(COMPETITION_ID)).thenReturn(competitionSummaryResource);

        when(applicationSummarySortFieldService.sortFieldForOpenCompetition(null)).thenReturn("sortfield");
        
        ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
    	when(applicationSummaryService.findByCompetitionId(COMPETITION_ID, "sortfield", 0, 20)).thenReturn(resource);

    	mockMvc.perform(get("/competition/123"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-open"))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource))
                .andExpect(model().attribute("results", resource))
                .andExpect(model().attribute("activeSortField", "sortfield"))
                .andExpect(model().attribute("activeTab", "allApplications"));
    	
    	verify(applicationSummaryService).findByCompetitionId(COMPETITION_ID, "sortfield", 0, 20);
    	verify(applicationSummaryService).getCompetitionSummaryByCompetitionId(COMPETITION_ID);
    }
    
    @Test
    public void getByCompetitionIdForOpenCompetitionProvidingPage() throws Exception {
        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(Status.OPEN).build();
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(COMPETITION_ID)).thenReturn(competitionSummaryResource);

        when(applicationSummarySortFieldService.sortFieldForOpenCompetition(null)).thenReturn("sortfield");
        
    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
    	when(applicationSummaryService.findByCompetitionId(COMPETITION_ID, "sortfield", 2, 20)).thenReturn(resource);

    	mockMvc.perform(get("/competition/123?page=3"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-open"))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource))
                .andExpect(model().attribute("results", resource))
                .andExpect(model().attribute("activeSortField", "sortfield"))
                .andExpect(model().attribute("activeTab", "allApplications"));
    	
    	verify(applicationSummaryService).findByCompetitionId(COMPETITION_ID, "sortfield", 2, 20);
    	verify(applicationSummaryService).getCompetitionSummaryByCompetitionId(COMPETITION_ID);
    }
    
    @Test
    public void getByCompetitionIdProvidingSort() throws Exception {
        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(Status.OPEN).build();
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(COMPETITION_ID)).thenReturn(competitionSummaryResource);

        when(applicationSummarySortFieldService.sortFieldForOpenCompetition("lead")).thenReturn("properSort");

    	ApplicationSummaryPageResource resource = new ApplicationSummaryPageResource();
    	when(applicationSummaryService.findByCompetitionId(COMPETITION_ID, "properSort", 0, 20)).thenReturn(resource);

    	mockMvc.perform(get("/competition/123?sort=lead"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-open"))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource))
                .andExpect(model().attribute("results", resource))
                .andExpect(model().attribute("activeSortField", "properSort"))
                .andExpect(model().attribute("activeTab", "allApplications"));
    	
    	verify(applicationSummaryService).findByCompetitionId(COMPETITION_ID, "properSort", 0, 20);
    	verify(applicationSummaryService).getCompetitionSummaryByCompetitionId(COMPETITION_ID);
    }
    
    @Test
    public void getByCompetitionIdForCompetitionInAssessmentSubmittedIsDefault() throws Exception {
        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(Status.IN_ASSESSMENT).build();
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(COMPETITION_ID)).thenReturn(competitionSummaryResource);

        when(applicationSummarySortFieldService.sortFieldForSubmittedApplications(null)).thenReturn("sortfield");

        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource();
        when(applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, "sortfield", 0, 20)).thenReturn(summary);

    	mockMvc.perform(get("/competition/123"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-in-assessment"))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource))
                .andExpect(model().attribute("results", summary))
                .andExpect(model().attribute("activeTab", "submitted"))
    			.andExpect(model().attribute("activeSortField", "sortfield"));
    	verify(applicationSummaryService).getSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, "sortfield", 0, 20);
    	verify(applicationSummaryService).getCompetitionSummaryByCompetitionId(COMPETITION_ID);
    }
    
    @Test
    public void getByCompetitionIdForCompetitionInAssessmentSubmittedRequested() throws Exception {
        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(Status.IN_ASSESSMENT).build();
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(COMPETITION_ID)).thenReturn(competitionSummaryResource);

        when(applicationSummarySortFieldService.sortFieldForSubmittedApplications(null)).thenReturn("sortfield");

    	ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource();
        when(applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, "sortfield", 0, 20)).thenReturn(summary);

    	mockMvc.perform(get("/competition/123?tab=submitted"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-in-assessment"))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource))
                .andExpect(model().attribute("results", summary))
                .andExpect(model().attribute("activeTab", "submitted"))
    			.andExpect(model().attribute("activeSortField", "sortfield"));
    	verify(applicationSummaryService).getSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, "sortfield", 0, 20);
    	verify(applicationSummaryService).getCompetitionSummaryByCompetitionId(COMPETITION_ID);
    }
    
    @Test
    public void getByCompetitionIdForCompetitionInAssessmentNotSubmittedRequested() throws Exception {
        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(Status.IN_ASSESSMENT).build();
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(COMPETITION_ID)).thenReturn(competitionSummaryResource);

        when(applicationSummarySortFieldService.sortFieldForNotSubmittedApplications(null)).thenReturn("sortfield");

    	ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource();
        when(applicationSummaryService.getNotSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, "sortfield", 0, 20)).thenReturn(summary);

    	mockMvc.perform(get("/competition/123?tab=notSubmitted"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-in-assessment"))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource))
                .andExpect(model().attribute("results", summary))
                .andExpect(model().attribute("activeTab", "notSubmitted"))
    			.andExpect(model().attribute("activeSortField", "sortfield"));
    	
    	verify(applicationSummaryService).getNotSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, "sortfield", 0, 20);
    	verify(applicationSummaryService).getCompetitionSummaryByCompetitionId(COMPETITION_ID);
    }
    
    @Test
    public void getByCompetitionIdForCompetitionFundersPanelNotSubmittedRequested() throws Exception {
        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(Status.FUNDERS_PANEL).build();
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(COMPETITION_ID)).thenReturn(competitionSummaryResource);

        when(applicationSummarySortFieldService.sortFieldForNotSubmittedApplications(null)).thenReturn("sortfield");

    	ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource();
        when(applicationSummaryService.getNotSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, "sortfield", 0, 20)).thenReturn(summary);

    	mockMvc.perform(get("/competition/123?tab=notSubmitted"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-funders-panel"))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource))
                .andExpect(model().attribute("results", summary))
                .andExpect(model().attribute("activeTab", "notSubmitted"))
    			.andExpect(model().attribute("activeSortField", "sortfield"));
    	
    	verify(applicationSummaryService).getNotSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, "sortfield", 0, 20);
    	verify(applicationSummaryService).getCompetitionSummaryByCompetitionId(COMPETITION_ID);
    }
    
	@Test
    public void getByCompetitionIdForCompetitionFundersPanelSubmittedRequested() throws Exception {
        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(Status.FUNDERS_PANEL).build();
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(COMPETITION_ID)).thenReturn(competitionSummaryResource);

        when(applicationSummarySortFieldService.sortFieldForSubmittedApplications(null)).thenReturn("sortfield");

    	ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource();
        when(applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, "sortfield", 0, Integer.MAX_VALUE)).thenReturn(summary);

    	mockMvc.perform(get("/competition/123?tab=submitted"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-funders-panel"))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource))
                .andExpect(model().attribute("results", summary))
                .andExpect(model().attribute("activeTab", "submitted"))
    			.andExpect(model().attribute("activeSortField", "sortfield"));
    	
    	verify(applicationSummaryService).getSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, "sortfield", 0, Integer.MAX_VALUE);
    	verify(applicationSummaryService).getCompetitionSummaryByCompetitionId(COMPETITION_ID);
    }
    
    @Test
    public void getByCompetitionIdForCompetitionAssessorFeedbackNotSubmittedRequested() throws Exception {
        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(Status.ASSESSOR_FEEDBACK).build();
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(COMPETITION_ID)).thenReturn(competitionSummaryResource);

        when(applicationSummarySortFieldService.sortFieldForNotSubmittedApplications(null)).thenReturn("sortfield");

    	ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource();
        when(applicationSummaryService.getNotSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, "sortfield", 0, 20)).thenReturn(summary);

    	mockMvc.perform(get("/competition/123?tab=notSubmitted"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-assessor-feedback"))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource))
                .andExpect(model().attribute("results", summary))
                .andExpect(model().attribute("activeTab", "notSubmitted"))
    			.andExpect(model().attribute("activeSortField", "sortfield"));
    	
    	verify(applicationSummaryService).getNotSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, "sortfield", 0, 20);
    	verify(applicationSummaryService).getCompetitionSummaryByCompetitionId(COMPETITION_ID);
    }
    
    @Test
    public void getByCompetitionIdForCompetitionAssessorFeedbackSubmittedRequested() throws Exception {
        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(Status.ASSESSOR_FEEDBACK).build();
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(COMPETITION_ID)).thenReturn(competitionSummaryResource);

        when(applicationSummarySortFieldService.sortFieldForSubmittedApplications(null)).thenReturn("sortfield");

    	ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource();
        when(applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, "sortfield", 0, 20)).thenReturn(summary);

        when(assessorFeedbackService.feedbackUploaded(COMPETITION_ID)).thenReturn(false);

    	mockMvc.perform(get("/competition/123?tab=submitted"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-assessor-feedback"))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource))
                .andExpect(model().attribute("results", summary))
                .andExpect(model().attribute("activeTab", "submitted"))
    			.andExpect(model().attribute("activeSortField", "sortfield"))
    			.andExpect(model().attribute("canPublishAssessorFeedback", false));
    	
    	verify(applicationSummaryService).getSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, "sortfield", 0, 20);
    	verify(applicationSummaryService).getCompetitionSummaryByCompetitionId(COMPETITION_ID);
    	verify(assessorFeedbackService).feedbackUploaded(COMPETITION_ID);
    }
    
    @Test
    public void getByCompetitionIdForCompetitionAssessorFeedbackOverviewRequested() throws Exception {
        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(Status.ASSESSOR_FEEDBACK).build();
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(COMPETITION_ID)).thenReturn(competitionSummaryResource);

        when(applicationSummaryService.getApplicationsRequiringFeedbackCountByCompetitionId(COMPETITION_ID)).thenReturn(3L);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime assessmentStartDate = now.minusDays(7L);
        LocalDateTime assessorDeadlineDate = now.plusDays(2L);
        LocalDateTime fundersPanelDate = now.plusDays(3L);
        
        CompetitionResource competition = newCompetitionResource()
        		.withAssessorAcceptsDate(assessmentStartDate)
                .withAssessorDeadlineDate(assessorDeadlineDate)
        		.withFundersPanelDate(fundersPanelDate)
        		.build();
        when(competitionService.getById(COMPETITION_ID)).thenReturn(competition);
        
    	mockMvc.perform(get("/competition/123?tab=overview"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-assessor-feedback"))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource))
                .andExpect(model().attribute("activeTab", "overview"))
                .andExpect(model().attribute("applicationsRequiringFeedback", 3L))
                .andExpect(model().attribute("assessmentEndDate", fundersPanelDate))
                .andExpect(model().attribute("assessmentDaysLeft", 1L))
                .andExpect(model().attribute("assessmentDaysLeftPercentage", 88L));
    	
    	verify(applicationSummaryService).getCompetitionSummaryByCompetitionId(COMPETITION_ID);
    }
    
    @Test
    public void getByCompetitionIdProvidingInvalidPage() throws Exception {
    	
    	mockMvc.perform(get("/competition/123?page=0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/123"));
    	
    	verifyNoMoreInteractions(applicationSummaryService);
    }

    @Test
    public void createCompetition() throws Exception {
        when(competitionService.create()).thenReturn(newCompetitionResource().withId(COMPETITION_ID).build());

        mockMvc.perform(get("/competition/create"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/setup/" + COMPETITION_ID));
    }
}