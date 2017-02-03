package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationSummaryService;
import org.innovateuk.ifs.competition.service.ApplicationSummarySortFieldService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.innovateuk.ifs.application.builder.CompetitionSummaryResourceBuilder.newCompetitionSummaryResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.FUNDERS_PANEL;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.class)
public class CompetitionManagementFundingControllerTest {

    public static final Long COMPETITION_ID = Long.valueOf(123L);

    @InjectMocks
    private CompetitionManagementFundingController controller;

    @Mock
    private ApplicationSummaryService applicationSummaryService;

    @Mock
    private ApplicationSummarySortFieldService applicationSummarySortFieldService;

    private MockMvc mockMvc;

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }


    @Test
    public void getByCompetitionIdForCompetitionFundersPanelNotSubmittedRequested() throws Exception {
        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(FUNDERS_PANEL).build();
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(COMPETITION_ID)).thenReturn(competitionSummaryResource);

        when(applicationSummarySortFieldService.sortFieldForNotSubmittedApplications(null)).thenReturn("sortfield");

        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource();
        when(applicationSummaryService.getNotSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, "sortfield", 0, 20)).thenReturn(summary);

        mockMvc.perform(get("/competition/{competitionId}/funding?tab=notSubmitted", COMPETITION_ID))
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
        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(FUNDERS_PANEL).build();
        when(applicationSummaryService.getCompetitionSummaryByCompetitionId(COMPETITION_ID)).thenReturn(competitionSummaryResource);

        when(applicationSummarySortFieldService.sortFieldForSubmittedApplications(null)).thenReturn("sortfield");

        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource();
        when(applicationSummaryService.getSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, "sortfield", 0, Integer.MAX_VALUE)).thenReturn(summary);

        mockMvc.perform(get("/competition/{competitionId}/funding?tab=submitted", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-funders-panel"))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource))
                .andExpect(model().attribute("results", summary))
                .andExpect(model().attribute("activeTab", "submitted"))
                .andExpect(model().attribute("activeSortField", "sortfield"));

        verify(applicationSummaryService).getSubmittedApplicationSummariesByCompetitionId(COMPETITION_ID, "sortfield", 0, Integer.MAX_VALUE);
        verify(applicationSummaryService).getCompetitionSummaryByCompetitionId(COMPETITION_ID);
    }
}
