package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.management.model.ApplicationsMenuModelPopulator;
import org.innovateuk.ifs.management.viewmodel.ApplicationsMenuViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MvcResult;

import static org.innovateuk.ifs.application.builder.CompetitionSummaryResourceBuilder.newCompetitionSummaryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.class)
public class CompetitionManagementApplicationsControllerTest extends BaseControllerMockMVCTest<CompetitionManagementApplicationsController> {

    private long competitionId = 1L;

    @InjectMocks
    @Spy
    private ApplicationsMenuModelPopulator applicationsMenuModelPopulator;

    @Override
    protected CompetitionManagementApplicationsController supplyControllerUnderTest() {
        return new CompetitionManagementApplicationsController();
    }

    @Test
    public void applicationsMenu() throws Exception {
        CompetitionSummaryResource expectedSummaryResource = newCompetitionSummaryResource()
                .withId(competitionId)
                .withCompetitionName("Test Competition")
                .withApplicationsInProgress(10)
                .withApplicationsSubmitted(20)
                .withIneligibleApplications(5)
                .withAssesorsInvited(30)
                .build();

        when(applicationSummaryRestService.getCompetitionSummary(competitionId)).thenReturn(restSuccess(expectedSummaryResource));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/applications", competitionId))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/applications-menu"))
                .andReturn();

        ApplicationsMenuViewModel model = (ApplicationsMenuViewModel) result.getModelAndView().getModel().get("model");

        verify(applicationSummaryRestService, only()).getCompetitionSummary(competitionId);

        assertEquals(competitionId, model.getCompetitionId());
        assertEquals(expectedSummaryResource.getCompetitionName(), model.getCompetitionName());
        assertEquals(expectedSummaryResource.getApplicationsInProgress(), model.getApplicationsInProgress());
        assertEquals(expectedSummaryResource.getApplicationsSubmitted(), model.getApplicationsSubmitted());
        assertEquals(expectedSummaryResource.getIneligibleApplications(), model.getIneligibleApplications());
        assertEquals(expectedSummaryResource.getAssessorsInvited(), model.getAssessorsInvited());
    }
}
