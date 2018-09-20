package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.populator.CompetitionSearchPopulator;
import org.innovateuk.ifs.competition.viewmodel.CompetitionSearchViewModel;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class CompetitionSearchControllerTest extends BaseControllerMockMVCTest<CompetitionSearchController> {
    @Mock
    private CompetitionSearchPopulator competitionSearchPopulatorMock;

    @Autowired
    private WebApplicationContext wac;

    @Override
    protected CompetitionSearchController supplyControllerUnderTest() {
        return new CompetitionSearchController();
    }

    @Test
    public void publicContentSearch() throws Exception {
        CompetitionSearchViewModel competitionSearchViewModel = new CompetitionSearchViewModel();

        when(competitionSearchPopulatorMock.createItemSearchViewModel(Optional.empty(), Optional.empty(), Optional.empty())).thenReturn(competitionSearchViewModel);

        mockMvc.perform(get("/competition/search"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/search"));

        verify(competitionSearchPopulatorMock, times(1)).createItemSearchViewModel(Optional.empty(), Optional.empty(), Optional.empty());
    }

    @Test
    public void publicContentSearchValid() throws Exception {
        CompetitionSearchViewModel competitionSearchViewModel = new CompetitionSearchViewModel();

        long expectedInnovationAreaId = 5L;
        String expectedKeywords = "TEST";
        int expectedPageNumber = 23;

        competitionSearchViewModel.setSelectedInnovationAreaId(expectedInnovationAreaId);
        competitionSearchViewModel.setSearchKeywords(expectedKeywords);
        competitionSearchViewModel.setPageNumber(23);

        when(competitionSearchPopulatorMock.createItemSearchViewModel(Optional.of(expectedInnovationAreaId), Optional.of(expectedKeywords), Optional.of(expectedPageNumber))).thenReturn(competitionSearchViewModel);

        mockMvc.perform(get("/competition/search")
                .param("keywords", expectedKeywords)
                .param("innovationAreaId", String.valueOf(expectedInnovationAreaId))
                .param("page", String.valueOf(expectedPageNumber)))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/search"));

        verify(competitionSearchPopulatorMock, times(1)).createItemSearchViewModel(Optional.of(expectedInnovationAreaId), Optional.of(expectedKeywords), Optional.of(expectedPageNumber));
    }

}