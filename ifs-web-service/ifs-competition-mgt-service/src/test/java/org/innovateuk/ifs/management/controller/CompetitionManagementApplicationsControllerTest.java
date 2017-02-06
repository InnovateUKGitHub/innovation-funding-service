package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.management.model.AllApplicationsPageModelPopulator;
import org.innovateuk.ifs.management.model.ApplicationsMenuModelPopulator;
import org.innovateuk.ifs.management.model.SubmittedApplicationsModelPopulator;
import org.innovateuk.ifs.management.viewmodel.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
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

    @InjectMocks
    @Spy
    private SubmittedApplicationsModelPopulator submittedApplicationsModelPopulator;

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

    @Test
    public void submittedApplications() throws Exception {
        Long[] ids = {1L, 2L, 3L};
        String[] titles = {"Title 1", "Title 2", "Title 3"};
        String[] leads = {"Lead 1", "Lead 2", "Lead 3"};
        String[] innovationAreas = {"Innovation Area 1", "Innovation Area 1", "Innovation Area 1"};
        Integer[] numberOfPartners = {5, 10, 12};
        BigDecimal[] grantRequested = {BigDecimal.valueOf(1000), BigDecimal.valueOf(2000), BigDecimal.valueOf(3000)};
        BigDecimal[] totalProjectCost = {BigDecimal.valueOf(5000), BigDecimal.valueOf(10000), BigDecimal.valueOf(15000)};
        Long[] durations = {10L, 20L, 30L};

        List<SubmittedApplicationsRowViewModel> expectedApplicationRows = asList(
                new SubmittedApplicationsRowViewModel(ids[0], titles[0], leads[0], innovationAreas[0], numberOfPartners[0], grantRequested[0], totalProjectCost[0], durations[0]),
                new SubmittedApplicationsRowViewModel(ids[1], titles[1], leads[1], innovationAreas[1], numberOfPartners[1], grantRequested[1], totalProjectCost[1], durations[1]),
                new SubmittedApplicationsRowViewModel(ids[2], titles[2], leads[2], innovationAreas[2], numberOfPartners[2], grantRequested[2], totalProjectCost[2], durations[2])
        );

        List<ApplicationSummaryResource> expectedSummaries = newApplicationSummaryResource()
                .withId(ids)
                .withName(titles)
                .withLead(leads)
                .withInnovationArea(innovationAreas)
                .withNumberOfPartners(numberOfPartners)
                .withGrantRequested(grantRequested)
                .withTotalProjectCost(totalProjectCost)
                .withDuration(durations)
                .build(3);

        ApplicationSummaryPageResource expectedSummaryPageResource = new ApplicationSummaryPageResource();
        expectedSummaryPageResource.setContent(expectedSummaries);

        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "", 0, Integer.MAX_VALUE))
                .thenReturn(restSuccess(expectedSummaryPageResource));
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID))
                .thenReturn(restSuccess(defaultExpectedCompetitionSummary));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/applications/submitted", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/submitted-applications"))
                .andReturn();

        SubmittedApplicationsViewModel model = (SubmittedApplicationsViewModel) result.getModelAndView().getModel().get("model");

        verify(applicationSummaryRestService).getSubmittedApplications(COMPETITION_ID, "", 0, Integer.MAX_VALUE);
        verify(applicationSummaryRestService).getCompetitionSummary(COMPETITION_ID);

        assertEquals(COMPETITION_ID, model.getCompetitionId());
        assertEquals(defaultExpectedCompetitionSummary.getCompetitionName(), model.getCompetitionName());
        assertEquals(defaultExpectedCompetitionSummary.getApplicationsSubmitted(), model.getApplicationsSubmitted());
        assertEquals(defaultExpectedCompetitionSummary.getAssessorDeadline(), model.getAssessmentDeadline());
        assertEquals(expectedApplicationRows, model.getApplications());

        String contextUrl = (String) result.getModelAndView().getModel().get("originQuery");
    }
}
