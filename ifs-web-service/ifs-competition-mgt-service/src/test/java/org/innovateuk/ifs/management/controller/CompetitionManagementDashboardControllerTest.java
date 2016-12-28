
package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Class for testing public functions of {@link CompetitionManagementDashboardController}
 */
@RunWith(MockitoJUnitRunner.class)
public class CompetitionManagementDashboardControllerTest {

    @InjectMocks
	private CompetitionManagementDashboardController controller;

    @Mock
    private CompetitionService competitionService;

    private MockMvc mockMvc;


    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void showingDashboard() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/dashboard"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }

    @Test
    public void liveDashboard() throws Exception {

        Map<CompetitionStatus, List<CompetitionSearchResultItem>> competitions = new HashMap<>();
        CompetitionCountResource counts = new CompetitionCountResource();

        Mockito.when(competitionService.getLiveCompetitions()).thenReturn(competitions);
        Mockito.when(competitionService.getCompetitionCounts()).thenReturn(counts);

        mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/live"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("dashboard/live"))
                .andExpect(MockMvcResultMatchers.model().attribute("competitions", is(competitions)))
                .andExpect(MockMvcResultMatchers.model().attribute("counts", is(counts)));
    }

    @Test
    public void projectSetupDashboard() throws Exception {

        Map<CompetitionStatus, List<CompetitionSearchResultItem>> competitions = new HashMap<>();
        CompetitionCountResource counts = new CompetitionCountResource();

        Mockito.when(competitionService.getProjectSetupCompetitions()).thenReturn(competitions);
        Mockito.when(competitionService.getCompetitionCounts()).thenReturn(counts);

        mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/project-setup"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("dashboard/projectSetup"))
                .andExpect(MockMvcResultMatchers.model().attribute("competitions", is(competitions)))
                .andExpect(MockMvcResultMatchers.model().attribute("counts", is(counts)));
    }

    @Test
    public void upcomingDashboard() throws Exception {

        Map<CompetitionStatus, List<CompetitionSearchResultItem>> competitions = new HashMap<>();
        CompetitionCountResource counts = new CompetitionCountResource();

        Mockito.when(competitionService.getUpcomingCompetitions()).thenReturn(competitions);
        Mockito.when(competitionService.getCompetitionCounts()).thenReturn(counts);

        mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/upcoming"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("dashboard/upcoming"))
                .andExpect(MockMvcResultMatchers.model().attribute("competitions", is(competitions)))
                .andExpect(MockMvcResultMatchers.model().attribute("counts", is(counts)));
    }

    @Test
    public void searchDashboard() throws Exception {
        CompetitionSearchResult searchResult = new CompetitionSearchResult();
        String searchQuery = "search";
        int defaultPage = 0;

        Mockito.when(competitionService.searchCompetitions(searchQuery, defaultPage)).thenReturn(searchResult);

        mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/search?searchQuery=" + searchQuery))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("dashboard/search"))
                .andExpect(MockMvcResultMatchers.model().attribute("results", is(searchResult)));
    }

    @Test
    public void createCompetition() throws Exception {
        Long competitionId = 1L;

        when(competitionService.create()).thenReturn(newCompetitionResource().withId(competitionId).build());

        mockMvc.perform(get("/competition/create"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/setup/" + competitionId));
    }
}
