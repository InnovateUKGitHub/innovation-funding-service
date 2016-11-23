
package com.worth.ifs.controller;

import com.worth.ifs.application.service.CompetitionService;
import com.worth.ifs.competition.controller.DashboardController;
import com.worth.ifs.competition.resource.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Class for testing public functions of {@link DashboardController}
 */
@RunWith(MockitoJUnitRunner.class)
public class DashboardControllerTest {

    @InjectMocks
	private DashboardController controller;

    @Mock
    private CompetitionService competitionService;

    private MockMvc mockMvc;


    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void showingDashboard() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void liveDashboard() throws Exception {

        Map<CompetitionStatus, List<CompetitionSearchResultItem>> competitions = new HashMap<>();
        CompetitionCountResource counts = new CompetitionCountResource();

        when(competitionService.getLiveCompetitions()).thenReturn(competitions);
        when(competitionService.getCompetitionCounts()).thenReturn(counts);

        mockMvc.perform(get("/dashboard/live"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/live"))
                .andExpect(model().attribute("competitions", is(competitions)))
                .andExpect(model().attribute("counts", is(counts)));
    }

    @Test
    public void projectSetupDashboard() throws Exception {

        Map<CompetitionStatus, List<CompetitionSearchResultItem>> competitions = new HashMap<>();
        CompetitionCountResource counts = new CompetitionCountResource();

        when(competitionService.getProjectSetupCompetitions()).thenReturn(competitions);
        when(competitionService.getCompetitionCounts()).thenReturn(counts);

        mockMvc.perform(get("/dashboard/projectSetup"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/projectSetup"))
                .andExpect(model().attribute("competitions", is(competitions)))
                .andExpect(model().attribute("counts", is(counts)));
    }

    @Test
    public void upcomingDashboard() throws Exception {

        Map<CompetitionStatus, List<CompetitionSearchResultItem>> competitions = new HashMap<>();
        CompetitionCountResource counts = new CompetitionCountResource();

        when(competitionService.getUpcomingCompetitions()).thenReturn(competitions);
        when(competitionService.getCompetitionCounts()).thenReturn(counts);

        mockMvc.perform(get("/dashboard/upcoming"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/upcoming"))
                .andExpect(model().attribute("competitions", is(competitions)))
                .andExpect(model().attribute("counts", is(counts)));
    }


    @Test
    public void searchDashboard() throws Exception {
        CompetitionSearchResult searchResult = new CompetitionSearchResult();
        String searchQuery = "search";
        int defaultPage = 0;

        when(competitionService.searchCompetitions(searchQuery, defaultPage)).thenReturn(searchResult);

        mockMvc.perform(get("/dashboard/search?searchQuery=" + searchQuery))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/search"))
                .andExpect(model().attribute("results", is(searchResult)));
    }


}