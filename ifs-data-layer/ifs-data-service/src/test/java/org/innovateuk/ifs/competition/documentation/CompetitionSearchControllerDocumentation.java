package org.innovateuk.ifs.competition.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.controller.CompetitionSearchController;
import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.search.CompetitionSearchResult;
import org.innovateuk.ifs.competition.transactional.CompetitionSearchService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.UpcomingCompetitionSearchResultItemBuilder.newUpcomingCompetitionSearchResultItem;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionSearchControllerDocumentation extends BaseControllerMockMVCTest<CompetitionSearchController> {
    @Mock
    private CompetitionSearchService competitionSearchService;

    @Override
    protected CompetitionSearchController supplyControllerUnderTest() {
        return new CompetitionSearchController();
    }

    @Test
    public void live() throws Exception {
        when(competitionSearchService.findLiveCompetitions()).thenReturn(serviceSuccess(new ArrayList<>(newUpcomingCompetitionSearchResultItem().build(2))));

        mockMvc.perform(get("/competition/live")
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void projectSetup() throws Exception {
        CompetitionSearchResult results = new CompetitionSearchResult();
        int page = 1;
        int size = 20;
        when(competitionSearchService.findProjectSetupCompetitions(page, size)).thenReturn(serviceSuccess(results));

        mockMvc.perform(get("/competition/project-setup")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void upcoming() throws Exception {
        when(competitionSearchService.findUpcomingCompetitions()).thenReturn(serviceSuccess(new ArrayList<>(newUpcomingCompetitionSearchResultItem().build(2))));

        mockMvc.perform(get("/competition/upcoming")
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void nonIfs() throws Exception {
        CompetitionSearchResult results = new CompetitionSearchResult();
        int page = 1;
        int size = 20;
        when(competitionSearchService.findNonIfsCompetitions(page, size)).thenReturn(serviceSuccess(results));

        mockMvc.perform(get("/competition/non-ifs")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void count() throws Exception {
        CompetitionCountResource resource = new CompetitionCountResource();
        when(competitionSearchService.countCompetitions()).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/competition/count")
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void search() throws Exception {
        CompetitionSearchResult results = new CompetitionSearchResult();
        String searchQuery = "test";
        int page = 1;
        int size = 20;
        when(competitionSearchService.searchCompetitions(searchQuery, page, size)).thenReturn(serviceSuccess(results));

        mockMvc.perform(get("/competition/search")
                .param("searchQuery", searchQuery)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void feedbackReleased() throws Exception {
        CompetitionSearchResult results = new CompetitionSearchResult();
        int page = 1;
        int size = 20;
        when(competitionSearchService.findPreviousCompetitions(page, size)).thenReturn(serviceSuccess(results));

        mockMvc.perform(get("/competition/post-submission/feedback-released")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }
}
