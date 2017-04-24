package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.management.model.AllApplicationsPageModelPopulator;
import org.innovateuk.ifs.management.model.ApplicationsMenuModelPopulator;
import org.innovateuk.ifs.management.model.IneligibleApplicationsModelPopulator;
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
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.innovateuk.ifs.application.builder.ApplicationSummaryResourceBuilder.newApplicationSummaryResource;
import static org.innovateuk.ifs.application.builder.CompetitionSummaryResourceBuilder.newCompetitionSummaryResource;
import static org.innovateuk.ifs.application.resource.FundingDecision.FUNDED;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @InjectMocks
    @Spy
    private IneligibleApplicationsModelPopulator ineligibleApplicationsModelPopulator;

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

        when(applicationSummaryRestService.getAllApplications(COMPETITION_ID, "", 0, 20, ""))
                .thenReturn(restSuccess(expectedSummaryPageResource));
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID))
                .thenReturn(restSuccess(defaultExpectedCompetitionSummary));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/applications/all", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/all-applications"))
                .andExpect(model().attribute("originQuery", "?origin=ALL_APPLICATIONS"))
                .andReturn();

        AllApplicationsViewModel model = (AllApplicationsViewModel) result.getModelAndView().getModel().get("model");

        verify(applicationSummaryRestService).getAllApplications(COMPETITION_ID, "", 0, 20, "");
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
    public void allApplicationsPagedSortedFiltered() throws Exception {
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

        ApplicationSummaryPageResource expectedSummaryPageResource = new ApplicationSummaryPageResource(41, 3,expectedSummaries, 1, 20);


        when(applicationSummaryRestService.getAllApplications(COMPETITION_ID, "id", 1, 20, "filter"))
                .thenReturn(restSuccess(expectedSummaryPageResource));
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID))
                .thenReturn(restSuccess(defaultExpectedCompetitionSummary));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/applications/all?page=1&sort=id&filterSearch=filter", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/all-applications"))
                .andExpect(model().attribute("originQuery", "?origin=ALL_APPLICATIONS&page=1&sort=id&filterSearch=filter"))
                .andReturn();

        AllApplicationsViewModel model = (AllApplicationsViewModel) result.getModelAndView().getModel().get("model");

        verify(applicationSummaryRestService).getAllApplications(COMPETITION_ID, "id", 1, 20, "filter");
        verify(applicationSummaryRestService).getCompetitionSummary(COMPETITION_ID);

        assertEquals(COMPETITION_ID, model.getCompetitionId());
        assertEquals(defaultExpectedCompetitionSummary.getCompetitionName(), model.getCompetitionName());
        assertEquals(defaultExpectedCompetitionSummary.getApplicationsInProgress(), model.getApplicationsInProgress());
        assertEquals(defaultExpectedCompetitionSummary.getApplicationsStarted(), model.getApplicationsStarted());
        assertEquals(defaultExpectedCompetitionSummary.getApplicationsSubmitted(), model.getApplicationsSubmitted());
        assertEquals(defaultExpectedCompetitionSummary.getTotalNumberOfApplications(), model.getTotalNumberOfApplications());
        PaginationViewModel actualPagination = model.getPagination();
        assertEquals(1,actualPagination.getCurrentPage());
        assertEquals(20,actualPagination.getPageSize());
        assertEquals(3, actualPagination.getTotalPages());
        assertEquals("1 to 20", actualPagination.getPageNames().get(0).getTitle());
        assertEquals("21 to 40", actualPagination.getPageNames().get(1).getTitle());
        assertEquals("41 to 41", actualPagination.getPageNames().get(2).getTitle());
        assertEquals("?origin=ALL_APPLICATIONS&sort=id&filterSearch=filter&page=2", actualPagination.getPageNames().get(2).getPath());
        assertEquals(expectedApplicationRows, model.getApplications());
    }

    @Test
    public void allApplications_preservesQueryParams() throws Exception {
        ApplicationSummaryPageResource expectedSummaryPageResource = new ApplicationSummaryPageResource();
        expectedSummaryPageResource.setContent(emptyList());

        when(applicationSummaryRestService.getAllApplications(COMPETITION_ID, "", 0, 20, ""))
                .thenReturn(restSuccess(expectedSummaryPageResource));
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID))
                .thenReturn(restSuccess(defaultExpectedCompetitionSummary));

        mockMvc.perform(get("/competition/{competitionId}/applications/all?param1=abc&param2=def", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/all-applications"))
                .andExpect(model().attribute("originQuery", "?origin=ALL_APPLICATIONS&param1=abc&param2=def"));

        verify(applicationSummaryRestService).getAllApplications(COMPETITION_ID, "", 0, 20, "");
        verify(applicationSummaryRestService).getCompetitionSummary(COMPETITION_ID);
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

        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "", 0, 20, "", empty()))
                .thenReturn(restSuccess(expectedSummaryPageResource));
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID))
                .thenReturn(restSuccess(defaultExpectedCompetitionSummary));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/applications/submitted", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/submitted-applications"))
                .andExpect(model().attribute("originQuery", "?origin=SUBMITTED_APPLICATIONS"))
                .andReturn();

        SubmittedApplicationsViewModel model = (SubmittedApplicationsViewModel) result.getModelAndView().getModel().get("model");

        verify(applicationSummaryRestService).getSubmittedApplications(COMPETITION_ID, "", 0, 20, "", empty());
        verify(applicationSummaryRestService).getCompetitionSummary(COMPETITION_ID);

        assertEquals(COMPETITION_ID, model.getCompetitionId());
        assertEquals(defaultExpectedCompetitionSummary.getCompetitionName(), model.getCompetitionName());
        assertEquals(defaultExpectedCompetitionSummary.getApplicationsSubmitted(), model.getApplicationsSubmitted());
        assertEquals(defaultExpectedCompetitionSummary.getAssessorDeadline(), model.getAssessmentDeadline());
        assertEquals(expectedApplicationRows, model.getApplications());
    }

    @Test
    public void submittedApplicationsPagedSortedFiltered() throws Exception {
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

        ApplicationSummaryPageResource expectedSummaryPageResource = new ApplicationSummaryPageResource(50, 3,expectedSummaries, 1, 20);


        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "id", 1, 20, "filter", empty()))
                .thenReturn(restSuccess(expectedSummaryPageResource));
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID))
                .thenReturn(restSuccess(defaultExpectedCompetitionSummary));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/applications/submitted?page=1&sort=id&filterSearch=filter", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/submitted-applications"))
                .andExpect(model().attribute("originQuery", "?origin=SUBMITTED_APPLICATIONS&page=1&sort=id&filterSearch=filter"))
                .andReturn();

        SubmittedApplicationsViewModel model = (SubmittedApplicationsViewModel) result.getModelAndView().getModel().get("model");

        verify(applicationSummaryRestService).getSubmittedApplications(COMPETITION_ID, "id", 1, 20, "filter", empty());
        verify(applicationSummaryRestService).getCompetitionSummary(COMPETITION_ID);

        assertEquals(COMPETITION_ID, model.getCompetitionId());
        assertEquals(defaultExpectedCompetitionSummary.getCompetitionName(), model.getCompetitionName());
        assertEquals(defaultExpectedCompetitionSummary.getApplicationsSubmitted(), model.getApplicationsSubmitted());
        assertEquals(defaultExpectedCompetitionSummary.getAssessorDeadline(), model.getAssessmentDeadline());
        PaginationViewModel actualPagination = model.getPagination();
        assertEquals(1,actualPagination.getCurrentPage());
        assertEquals(20,actualPagination.getPageSize());
        assertEquals(3, actualPagination.getTotalPages());
        assertEquals("1 to 20", actualPagination.getPageNames().get(0).getTitle());
        assertEquals("21 to 40", actualPagination.getPageNames().get(1).getTitle());
        assertEquals("41 to 50", actualPagination.getPageNames().get(2).getTitle());
        assertEquals("?origin=SUBMITTED_APPLICATIONS&sort=id&filterSearch=filter&page=2", actualPagination.getPageNames().get(2).getPath());
        assertEquals(expectedApplicationRows, model.getApplications());
    }

    @Test
    public void submittedApplications_preservesQueryParams() throws Exception {
        ApplicationSummaryPageResource expectedSummaryPageResource = new ApplicationSummaryPageResource();
        expectedSummaryPageResource.setContent(emptyList());

        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "", 0, 20, "", empty()))
                .thenReturn(restSuccess(expectedSummaryPageResource));
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID))
                .thenReturn(restSuccess(defaultExpectedCompetitionSummary));

        mockMvc.perform(get("/competition/{competitionId}/applications/submitted?param1=abc&param2=def", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/submitted-applications"))
                .andExpect(model().attribute("originQuery", "?origin=SUBMITTED_APPLICATIONS&param1=abc&param2=def"))
                .andReturn();

        verify(applicationSummaryRestService).getSubmittedApplications(COMPETITION_ID, "", 0, 20, "", empty());
        verify(applicationSummaryRestService).getCompetitionSummary(COMPETITION_ID);
    }
    @Test
    public void ineligibleApplications() throws Exception {
        Long[] ids = {1L, 2L, 3L};
        String[] titles = {"Title 1", "Title 2", "Title 3"};
        String[] leads = {"Lead 1", "Lead 2", "Lead 3"};
        String[] applicant = {"LeadApplicant 1","LeadApplicant 2","LeadApplicant 3"};
        Boolean[] informed = {true, true, false};

        List<IneligibleApplicationsRowViewModel> expectedApplicationRows = asList(
                new IneligibleApplicationsRowViewModel(ids[0], titles[0], leads[0], applicant[0], informed[0]),
                new IneligibleApplicationsRowViewModel(ids[1], titles[1], leads[1], applicant[1], informed[1]),
                new IneligibleApplicationsRowViewModel(ids[2], titles[2], leads[2], applicant[2], informed[2])
        );

        List<ApplicationSummaryResource> expectedSummaries = newApplicationSummaryResource()
                .withId(ids)
                .withName(titles)
                .withLead(leads)
                .withLeadApplicant(applicant)
                .withIneligibleInformed(informed)
                .build(3);

        ApplicationSummaryPageResource expectedSummaryPageResource = new ApplicationSummaryPageResource();
        expectedSummaryPageResource.setContent(expectedSummaries);

        when(applicationSummaryRestService.getIneligibleApplications(COMPETITION_ID, "", 0, 20, "", empty()))
                .thenReturn(restSuccess(expectedSummaryPageResource));
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID))
                .thenReturn(restSuccess(defaultExpectedCompetitionSummary));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/applications/ineligible", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/ineligible-applications"))
                .andExpect(model().attribute("originQuery", "?origin=INELIGIBLE_APPLICATIONS"))
                .andReturn();

        IneligibleApplicationsViewModel model = (IneligibleApplicationsViewModel) result.getModelAndView().getModel().get("model");

        verify(applicationSummaryRestService).getIneligibleApplications(COMPETITION_ID, "", 0, 20, "", empty());
        verify(applicationSummaryRestService).getCompetitionSummary(COMPETITION_ID);

        assertEquals(COMPETITION_ID, model.getCompetitionId());
        assertEquals(defaultExpectedCompetitionSummary.getCompetitionName(), model.getCompetitionName());
        assertEquals(expectedApplicationRows, model.getApplications());
    }

    @Test
    public void ineligibleApplicationsPagedSortedFiltered() throws Exception {
        Long[] ids = {1L, 2L, 3L};
        String[] titles = {"Title 1", "Title 2", "Title 3"};
        String[] leads = {"Lead 1", "Lead 2", "Lead 3"};
        String[] applicant = {"LeadApplicant 1","LeadApplicant 2","LeadApplicant 3"};
        Boolean[] informed = {true, true, false};

        List<IneligibleApplicationsRowViewModel> expectedApplicationRows = asList(
                new IneligibleApplicationsRowViewModel(ids[0], titles[0], leads[0], applicant[0], informed[0]),
                new IneligibleApplicationsRowViewModel(ids[1], titles[1], leads[1], applicant[1], informed[1]),
                new IneligibleApplicationsRowViewModel(ids[2], titles[2], leads[2], applicant[2], informed[2])
        );

        List<ApplicationSummaryResource> expectedSummaries = newApplicationSummaryResource()
                .withId(ids)
                .withName(titles)
                .withLead(leads)
                .withLeadApplicant(applicant)
                .withIneligibleInformed(informed)
                .build(3);

        ApplicationSummaryPageResource expectedSummaryPageResource = new ApplicationSummaryPageResource(50, 3,expectedSummaries, 1, 20);


        when(applicationSummaryRestService.getIneligibleApplications(COMPETITION_ID, "id", 1, 20, "filter",empty()))
                .thenReturn(restSuccess(expectedSummaryPageResource));
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID))
                .thenReturn(restSuccess(defaultExpectedCompetitionSummary));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/applications/ineligible?page=1&sort=id&filterSearch=filter", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/ineligible-applications"))
                .andExpect(model().attribute("originQuery", "?origin=INELIGIBLE_APPLICATIONS&page=1&sort=id&filterSearch=filter"))
                .andReturn();

        IneligibleApplicationsViewModel model = (IneligibleApplicationsViewModel) result.getModelAndView().getModel().get("model");

        verify(applicationSummaryRestService).getIneligibleApplications(COMPETITION_ID, "id", 1, 20, "filter", empty());
        verify(applicationSummaryRestService).getCompetitionSummary(COMPETITION_ID);

        assertEquals(COMPETITION_ID, model.getCompetitionId());
        assertEquals(defaultExpectedCompetitionSummary.getCompetitionName(), model.getCompetitionName());
        PaginationViewModel actualPagination = model.getPagination();
        assertEquals(1,actualPagination.getCurrentPage());
        assertEquals(20,actualPagination.getPageSize());
        assertEquals(3, actualPagination.getTotalPages());
        assertEquals("1 to 20", actualPagination.getPageNames().get(0).getTitle());
        assertEquals("21 to 40", actualPagination.getPageNames().get(1).getTitle());
        assertEquals("41 to 50", actualPagination.getPageNames().get(2).getTitle());
        assertEquals("?origin=INELIGIBLE_APPLICATIONS&sort=id&filterSearch=filter&page=2", actualPagination.getPageNames().get(2).getPath());
        assertEquals(expectedApplicationRows, model.getApplications());
    }

    @Test
    public void ineligibleApplications_preservesQueryParams() throws Exception {
        ApplicationSummaryPageResource expectedSummaryPageResource = new ApplicationSummaryPageResource();
        expectedSummaryPageResource.setContent(emptyList());

        when(applicationSummaryRestService.getIneligibleApplications(COMPETITION_ID, "", 0, 20, "", empty()))
                .thenReturn(restSuccess(expectedSummaryPageResource));
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID))
                .thenReturn(restSuccess(defaultExpectedCompetitionSummary));

        mockMvc.perform(get("/competition/{competitionId}/applications/ineligible?param1=abc&param2=def", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/ineligible-applications"))
                .andExpect(model().attribute("originQuery", "?origin=INELIGIBLE_APPLICATIONS&param1=abc&param2=def"))
                .andReturn();

        verify(applicationSummaryRestService).getIneligibleApplications(COMPETITION_ID, "", 0, 20, "", empty());
        verify(applicationSummaryRestService).getCompetitionSummary(COMPETITION_ID);
    }
}
