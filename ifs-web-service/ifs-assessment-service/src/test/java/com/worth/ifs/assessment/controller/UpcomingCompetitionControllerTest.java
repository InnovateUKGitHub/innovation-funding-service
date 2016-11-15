package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.model.UpcomingCompetitionModelPopulator;
import com.worth.ifs.competition.resource.CompetitionResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource("classpath:application.properties")
public class UpcomingCompetitionControllerTest extends BaseControllerMockMVCTest<UpcomingCompetitionController> {

    @Spy
    @InjectMocks
    private UpcomingCompetitionModelPopulator upcomingCompetitionModelPopulator;

    private static final String restUrl = "/competition";

    @Override
    protected UpcomingCompetitionController supplyControllerUnderTest() {
        return new UpcomingCompetitionController();
    }

    @Test
    public void viewSummary_loggedIn() throws Exception {
        CompetitionResource competitionResource = newCompetitionResource().withId(1L).build();

        when(competitionRestService.getCompetitionById(1L)).thenReturn(restSuccess(competitionResource));

        mockMvc.perform(get(restUrl + "/{competitionId}/upcoming", "1"))
        .andExpect(status().isOk());

        verify(competitionRestService, times(1)).getCompetitionById(1L);
    }

    @Test
    public void viewSummary_competitionNotExists() throws Exception {
        when(competitionRestService.getCompetitionById(1L)).thenReturn(restFailure(notFoundError(CompetitionResource.class, "notExistId")));

        mockMvc.perform(get(restUrl + "/{competitionId}/upcoming", "1"))
                .andExpect(model().attributeDoesNotExist("model"))
                .andExpect(status().isNotFound());

        verify(competitionRestService, times(1)).getCompetitionById(1L);
    }
}
