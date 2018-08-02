package org.innovateuk.ifs.interview.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionKeyApplicationStatisticsRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.interview.model.InterviewModelPopulator;
import org.innovateuk.ifs.interview.resource.InterviewStatisticsResource;
import org.innovateuk.ifs.interview.viewmodel.InterviewViewModel;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.web.servlet.MvcResult;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.name;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.CLOSED;
import static org.innovateuk.ifs.interview.builder.InterviewStatisticsResourceBuilder.newInterviewStatisticsResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class InterviewControllerTest extends BaseControllerMockMVCTest<InterviewController> {

    private CompetitionResource competitionResource;

    @Spy
    @InjectMocks
    private InterviewModelPopulator interviewModelPopulator;

    @Mock
    private CompetitionKeyApplicationStatisticsRestService competitionKeyApplicationStatisticsRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Override
    protected InterviewController supplyControllerUnderTest() {
        return new InterviewController();
    }

    @Test
    public void interviewPanel() throws Exception {
        long competitionId = 1L;
        String competitionName = "Competition x";
        CompetitionStatus competitionStatus = CLOSED;

        competitionResource = newCompetitionResource()
                .with(id(competitionId))
                .with(name(competitionName))
                .withCompetitionStatus(competitionStatus)
                .build();

        InterviewStatisticsResource keyStats = newInterviewStatisticsResource().build();

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competitionResource));
        when(competitionKeyApplicationStatisticsRestService.getInterviewStatisticsByCompetition(competitionId))
                .thenReturn(restSuccess(keyStats));

        MvcResult result = mockMvc.perform(get("/assessment/interview/competition/{competitionId}", competitionId))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/manage-interview-panel"))
                .andReturn();

        InterviewViewModel model = (InterviewViewModel) result.getModelAndView().getModel().get("model");

        verify(competitionRestService, only()).getCompetitionById(competitionId);

        assertEquals(competitionId, model.getCompetitionId());
        assertEquals(competitionName, model.getCompetitionName());
        assertEquals(competitionStatus, model.getCompetitionStatus());
        assertEquals(keyStats, model.getKeyStats());
    }
}