package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.management.model.InterviewPanelModelPopulator;
import org.innovateuk.ifs.management.viewmodel.InterviewPanelDashboardViewModel;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.test.web.servlet.MvcResult;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.name;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.CLOSED;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class CompetitionManagementInterviewPanelControllerTest extends BaseControllerMockMVCTest<CompetitionManagementInterviewPanelController> {

    @Spy
    @InjectMocks
    private InterviewPanelModelPopulator interviewPanelModelPopulator;

    @Override
    protected CompetitionManagementInterviewPanelController supplyControllerUnderTest() {
        return new CompetitionManagementInterviewPanelController();
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

        when(competitionService.getById(competitionId)).thenReturn(competitionResource);

        MvcResult result = mockMvc.perform(get("/interview/panel/competition/{competitionId}", competitionId))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/manage-interview-panel"))
                .andReturn();

        InterviewPanelDashboardViewModel model = (InterviewPanelDashboardViewModel) result.getModelAndView().getModel().get("model");

        verify(competitionService, only()).getById(competitionId);

        assertEquals(competitionId, model.getCompetitionId());
        assertEquals(competitionName, model.getCompetitionName());
        assertEquals(competitionStatus, model.getCompetitionStatus());
    }
}