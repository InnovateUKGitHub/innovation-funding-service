package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.management.model.AllApplicationsPageModelPopulator;
import org.innovateuk.ifs.management.model.ApplicationsMenuModelPopulator;
import org.innovateuk.ifs.management.viewmodel.AllApplicationsRowViewModel;
import org.innovateuk.ifs.management.viewmodel.AllApplicationsViewModel;
import org.innovateuk.ifs.management.viewmodel.ApplicationsMenuViewModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationSummaryResourceBuilder.newApplicationSummaryResource;
import static org.innovateuk.ifs.application.builder.CompetitionSummaryResourceBuilder.newCompetitionSummaryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class CompetitionManagementApplicationsControllerTest extends BaseControllerMockMVCTest<CompetitionManagementApplicationsController> {

    private long COMPETITION_ID = 1L;
    private CompetitionSummaryResource defaultExpectedCompetitionSummary;

    @InjectMocks
    @Spy
    private ApplicationsMenuModelPopulator applicationsMenuModelPopulator;

    @InjectMocks
    @Spy
    private AllApplicationsPageModelPopulator allApplicationsPageModelPopulator;

    @Override
    protected CompetitionManagementApplicationsController supplyControllerUnderTest() {
        return new CompetitionManagementApplicationsController();
    }

    @Before
    public void setDefaults() {
        defaultExpectedCompetitionSummary = newCompetitionSummaryResource()
                .withId(COMPETITION_ID)
                .withCompetitionName("Test Competition")
                .withApplicationsInProgress(10)
                .withApplicationsSubmitted(20)
                .withIneligibleApplications(5)
                .withAssesorsInvited(30)
                .build();
    }

    @Test
    public void applicationsMenu() throws Exception {
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(defaultExpectedCompetitionSummary));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/applications", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/applications-menu"))
                .andReturn();

        ApplicationsMenuViewModel model = (ApplicationsMenuViewModel) result.getModelAndView().getModel().get("model");

        verify(applicationSummaryRestService, only()).getCompetitionSummary(COMPETITION_ID);

        assertEquals(COMPETITION_ID, model.getCompetitionId());
        assertEquals(defaultExpectedCompetitionSummary.getCompetitionName(), model.getCompetitionName());
        assertEquals(defaultExpectedCompetitionSummary.getApplicationsInProgress(), model.getApplicationsInProgress());
        assertEquals(defaultExpectedCompetitionSummary.getApplicationsSubmitted(), model.getApplicationsSubmitted());
        assertEquals(defaultExpectedCompetitionSummary.getIneligibleApplications(), model.getIneligibleApplications());
        assertEquals(defaultExpectedCompetitionSummary.getAssessorsInvited(), model.getAssessorsInvited());
    }

    @Test
    public void allApplications() throws Exception {
        Long[] ids = {1L, 2L, 3L};
        String[] titles = {"Title 1", "Title 2", "Title 3"};
        String[] leads = {"Lead 1", "Lead 2", "Lead 3"};
        String[] innovationAreas = {"Innovation Area 1", "Innovation Area 1", "Innovation Area 1"};
        String[] statuses = {"Submitted", "Started", "Started"};
        Integer[] percentages = {100, 70, 20};

        List<AllApplicationsRowViewModel> expectedApplicationRows = asList(
                new AllApplicationsRowViewModel(ids[0], titles[0], leads[0], innovationAreas[0], statuses[0], percentages[0]),
                new AllApplicationsRowViewModel(ids[1], titles[1], leads[1], innovationAreas[1], statuses[1], percentages[1]),
                new AllApplicationsRowViewModel(ids[2], titles[2], leads[2], innovationAreas[2], statuses[2], percentages[2])
        );

        List<ApplicationSummaryResource> expectedSummaries = newApplicationSummaryResource()
                .withId(ids)
                .withName(titles)
                .withLead(leads)
                .withInnovationArea(innovationAreas)
                .withStatus(statuses)
                .withCompletedPercentage(percentages)
                .build(3);

        ApplicationSummaryPageResource expectedSummaryPageResource = new ApplicationSummaryPageResource();
        expectedSummaryPageResource.setContent(expectedSummaries);

        when(applicationSummaryRestService.getAllApplications(COMPETITION_ID, "", 0, Integer.MAX_VALUE))
                .thenReturn(restSuccess(expectedSummaryPageResource));
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID))
                .thenReturn(restSuccess(defaultExpectedCompetitionSummary));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/applications/all", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/all-applications"))
                .andReturn();

        AllApplicationsViewModel model = (AllApplicationsViewModel) result.getModelAndView().getModel().get("model");

        verify(applicationSummaryRestService).getAllApplications(COMPETITION_ID, "", 0, Integer.MAX_VALUE);
        verify(applicationSummaryRestService).getCompetitionSummary(COMPETITION_ID);

        assertEquals(COMPETITION_ID, model.getCompetitionId());
        assertEquals(defaultExpectedCompetitionSummary.getCompetitionName(), model.getCompetitionName());
        assertEquals(defaultExpectedCompetitionSummary.getApplicationsInProgress(), model.getApplicationsInProgress());
        assertEquals(defaultExpectedCompetitionSummary.getApplicationsStarted(), model.getApplicationsStarted());
        assertEquals(defaultExpectedCompetitionSummary.getApplicationsSubmitted(), model.getApplicationsSubmitted());
        assertEquals(defaultExpectedCompetitionSummary.getTotalNumberOfApplications(), model.getTotalNumberOfApplications());
        assertEquals(expectedApplicationRows, model.getApplications());
    }
}
