package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentPanelKeyStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.management.model.AssessmentPanelModelPopulator;
import org.innovateuk.ifs.management.service.CompetitionAssessmentPanelService;
import org.innovateuk.ifs.management.viewmodel.AssessmentPanelViewModel;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.web.servlet.MvcResult;

import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.name;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.CLOSED;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class CompetitionManagementAssessmentPanelControllerTest extends BaseControllerMockMVCTest<CompetitionManagementAssessmentPanelController> {

    @Spy
    @InjectMocks
    private AssessmentPanelModelPopulator assessmentPanelModelPopulator;

    @Mock
    private CompetitionAssessmentPanelService competitionAssessmentPanelService;

    @Mock
    private AssessmentPanelKeyStatisticsResource assessmentPanelKeyStatisticsResource;

    @Override
    protected CompetitionManagementAssessmentPanelController supplyControllerUnderTest() {
        return new CompetitionManagementAssessmentPanelController();
    }

    @Test
    public void assessmentPanel() throws Exception {
        Long competitionId = 1L;
        String competitionName = "Competition x";
        CompetitionStatus competitionStatus = CLOSED;

        competitionResource = newCompetitionResource()
                .with(id(competitionId))
                .with(name(competitionName))
                .withCompetitionStatus(competitionStatus)
                .build();

        assessmentPanelKeyStatisticsResource.setApplicationsInPanel(1);
        assessmentPanelKeyStatisticsResource.setAssessorsPending(2);
        assessmentPanelKeyStatisticsResource.setAssessorsAccepted(1);


        when(competitionService.getById(competitionId)).thenReturn(competitionResource);
        when(competitionAssessmentPanelService.getAssessmentPanelKeyStatistics(competitionId)).thenReturn(assessmentPanelKeyStatisticsResource);

        MvcResult result = mockMvc.perform(get("/assessment/panel/competition/{competitionId}", competitionId))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/manage-assessment-panel"))
                .andReturn();

        AssessmentPanelViewModel model = (AssessmentPanelViewModel) result.getModelAndView().getModel().get("model");

        verify(competitionService, only()).getById(competitionId);

        assertEquals(competitionId, model.getCompetitionId());
        assertEquals(competitionName, model.getCompetitionName());
        assertEquals(competitionStatus, model.getCompetitionStatus());
    }
}
