package org.innovateuk.ifs.management.competition.setup.closecompetition.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionPostSubmissionRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionCompletionStage.PROJECT_SETUP;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AlwaysOpenCloseCompetitionControllerTest extends BaseControllerMockMVCTest<AlwaysOpenCloseCompetitionController> {

    private static final String URL = "/competition/{competitionId}/always-open";
    private long competitionId;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private CompetitionPostSubmissionRestService competitionPostSubmissionRestService;

    @Override
    protected AlwaysOpenCloseCompetitionController supplyControllerUnderTest() {
        return new AlwaysOpenCloseCompetitionController();
    }

    @Before
    public void setup() {
        competitionId = 100L;
        CompetitionResource competition = newCompetitionResource()
                .withId(competitionId)
                .withAlwaysOpen(true)
                .withCompletionStage(PROJECT_SETUP)
                .build();

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
    }

    @Test
    public void viewCloseCompetitionPage() throws Exception {
        mockMvc.perform(get(URL, competitionId))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup/close-always-open-competition"));
    }

    @Test
    public void closeCompetition() throws Exception {
        when(competitionPostSubmissionRestService.releaseFeedback(competitionId)).thenReturn(restSuccess());
        mockMvc.perform(post(URL + "/close", competitionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/dashboard/project-setup")));
    }
}