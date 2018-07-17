package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.builder.ApplicationResourceBuilder;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.application.service.ApplicationFundingDecisionService;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.application.controller.CompetitionManagementApplicationsController;
import org.innovateuk.ifs.management.application.populator.*;
import org.innovateuk.ifs.management.application.viewmodel.*;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static org.innovateuk.ifs.application.builder.ApplicationSummaryResourceBuilder.newApplicationSummaryResource;
import static org.innovateuk.ifs.application.builder.CompetitionSummaryResourceBuilder.newCompetitionSummaryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CompetitionManagementApplicationsControllerTest extends BaseControllerMockMVCTest<CompetitionManagementApplicationsController> {

    private long COMPETITION_ID = 1L;
    private String COMPETITION_NAME = "comp1";
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

    @Mock
    private UnsuccessfulApplicationsModelPopulator unsuccessfulApplicationsModelPopulator;

    @Mock
    private ApplicationSummaryRestService applicationSummaryRestService;

    @Mock
    private ApplicationFundingDecisionService applicationFundingDecisionService;

    @Mock
    private ProjectService projectService;

    @Mock
    private CompetitionRestService competitionRestService;

    @InjectMocks
    @Spy
    private NavigateApplicationsModelPopulator navigateApplicationsModelPopulator;

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

        CompetitionResource competitionResource = newCompetitionResource()
                .withCompetitionStatus(CompetitionStatus.OPEN).build();
        when(competitionRestService.getCompetitionById(anyLong())).thenReturn(restSuccess(competitionResource));
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
        assertEquals(false, model.isInnovationLeadView());
    }

    @Test
    public void applicationsMenuCheckInnovationLeadViewFlag() throws Exception {

        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(defaultExpectedCompetitionSummary));

        {
            UserResource userResource = newUserResource().withRolesGlobal(singletonList(Role.COMP_ADMIN)).build();

            setLoggedInUser(userResource);

            MvcResult result = mockMvc.perform(get("/competition/{competitionId}/applications", COMPETITION_ID))
                    .andExpect(status().isOk())
                    .andExpect(view().name("competition/applications-menu"))
                    .andReturn();

            ApplicationsMenuViewModel model = (ApplicationsMenuViewModel) result.getModelAndView().getModel().get("model");

            assertEquals(false, model.isInnovationLeadView());
        }

        {
            UserResource userResource = newUserResource().withRolesGlobal(singletonList(Role.INNOVATION_LEAD)).build();

            setLoggedInUser(userResource);

            MvcResult result = mockMvc.perform(get("/competition/{competitionId}/applications", COMPETITION_ID))
                    .andExpect(status().isOk())
                    .andExpect(view().name("competition/applications-menu"))
                    .andReturn();

            ApplicationsMenuViewModel model = (ApplicationsMenuViewModel) result.getModelAndView().getModel().get("model");

            assertEquals(true, model.isInnovationLeadView());
        }
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

        when(applicationSummaryRestService.getAllApplications(COMPETITION_ID, "", 0, 20, Optional.empty()))
                .thenReturn(restSuccess(expectedSummaryPageResource));
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID))
                .thenReturn(restSuccess(defaultExpectedCompetitionSummary));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/applications/all", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/all-applications"))
                .andExpect(model().attribute("originQuery", "?origin=ALL_APPLICATIONS"))
                .andReturn();

        AllApplicationsViewModel model = (AllApplicationsViewModel) result.getModelAndView().getModel().get("model");

        verify(applicationSummaryRestService).getAllApplications(COMPETITION_ID, "", 0, 20, Optional.empty());
        verify(applicationSummaryRestService).getCompetitionSummary(COMPETITION_ID);

        assertEquals(COMPETITION_ID, model.getCompetitionId());
        assertEquals(defaultExpectedCompetitionSummary.getCompetitionName(), model.getCompetitionName());
        assertEquals(defaultExpectedCompetitionSummary.getApplicationsInProgress(), model.getApplicationsInProgress());
        assertEquals(defaultExpectedCompetitionSummary.getApplicationsStarted(), model.getApplicationsStarted());
        assertEquals(defaultExpectedCompetitionSummary.getApplicationsSubmitted(), model.getApplicationsSubmitted());
        assertEquals(defaultExpectedCompetitionSummary.getTotalNumberOfApplications(), model.getTotalNumberOfApplications());
        assertEquals("Applications", model.getBackTitle());
        assertEquals("/competition/" + COMPETITION_ID + "/applications", model.getBackURL());
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


        when(applicationSummaryRestService.getAllApplications(COMPETITION_ID, "id", 1, 20, Optional.of("filter")))
                .thenReturn(restSuccess(expectedSummaryPageResource));
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID))
                .thenReturn(restSuccess(defaultExpectedCompetitionSummary));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/applications/all?page=1&sort=id&filterSearch=filter", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/all-applications"))
                .andExpect(model().attribute("originQuery", "?origin=ALL_APPLICATIONS&page=1&sort=id&filterSearch=filter"))
                .andReturn();

        AllApplicationsViewModel model = (AllApplicationsViewModel) result.getModelAndView().getModel().get("model");

        verify(applicationSummaryRestService).getAllApplications(COMPETITION_ID, "id", 1, 20, Optional.of("filter"));
        verify(applicationSummaryRestService).getCompetitionSummary(COMPETITION_ID);

        assertEquals(COMPETITION_ID, model.getCompetitionId());
        assertEquals(defaultExpectedCompetitionSummary.getCompetitionName(), model.getCompetitionName());
        assertEquals(defaultExpectedCompetitionSummary.getApplicationsInProgress(), model.getApplicationsInProgress());
        assertEquals(defaultExpectedCompetitionSummary.getApplicationsStarted(), model.getApplicationsStarted());
        assertEquals(defaultExpectedCompetitionSummary.getApplicationsSubmitted(), model.getApplicationsSubmitted());
        assertEquals(defaultExpectedCompetitionSummary.getTotalNumberOfApplications(), model.getTotalNumberOfApplications());
        Pagination actualPagination = model.getPagination();
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

        when(applicationSummaryRestService.getAllApplications(COMPETITION_ID, "", 0, 20, Optional.empty()))
                .thenReturn(restSuccess(expectedSummaryPageResource));
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID))
                .thenReturn(restSuccess(defaultExpectedCompetitionSummary));

        mockMvc.perform(get("/competition/{competitionId}/applications/all?param1=abc&param2=def", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/all-applications"))
                .andExpect(model().attribute("originQuery", "?origin=ALL_APPLICATIONS&param1=abc&param2=def"));

        verify(applicationSummaryRestService).getAllApplications(COMPETITION_ID, "", 0, 20, Optional.empty());
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

        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "", 0, 20, empty(), empty()))
                .thenReturn(restSuccess(expectedSummaryPageResource));
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID))
                .thenReturn(restSuccess(defaultExpectedCompetitionSummary));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/applications/submitted", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/submitted-applications"))
                .andExpect(model().attribute("originQuery", "?origin=SUBMITTED_APPLICATIONS"))
                .andReturn();

        SubmittedApplicationsViewModel model = (SubmittedApplicationsViewModel) result.getModelAndView().getModel().get("model");

        verify(applicationSummaryRestService).getSubmittedApplications(COMPETITION_ID, "", 0, 20, empty(), empty());
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


        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "id", 1, 20, Optional.of("filter"), empty()))
                .thenReturn(restSuccess(expectedSummaryPageResource));
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID))
                .thenReturn(restSuccess(defaultExpectedCompetitionSummary));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/applications/submitted?page=1&sort=id&filterSearch=filter", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/submitted-applications"))
                .andExpect(model().attribute("originQuery", "?origin=SUBMITTED_APPLICATIONS&page=1&sort=id&filterSearch=filter"))
                .andReturn();

        SubmittedApplicationsViewModel model = (SubmittedApplicationsViewModel) result.getModelAndView().getModel().get("model");

        verify(applicationSummaryRestService).getSubmittedApplications(COMPETITION_ID, "id", 1, 20, Optional.of("filter"), empty());
        verify(applicationSummaryRestService).getCompetitionSummary(COMPETITION_ID);

        assertEquals(COMPETITION_ID, model.getCompetitionId());
        assertEquals(defaultExpectedCompetitionSummary.getCompetitionName(), model.getCompetitionName());
        assertEquals(defaultExpectedCompetitionSummary.getApplicationsSubmitted(), model.getApplicationsSubmitted());
        assertEquals(defaultExpectedCompetitionSummary.getAssessorDeadline(), model.getAssessmentDeadline());
        Pagination actualPagination = model.getPagination();
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

        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "", 0, 20, Optional.empty(), empty()))
                .thenReturn(restSuccess(expectedSummaryPageResource));
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID))
                .thenReturn(restSuccess(defaultExpectedCompetitionSummary));

        mockMvc.perform(get("/competition/{competitionId}/applications/submitted?param1=abc&param2=def", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/submitted-applications"))
                .andExpect(model().attribute("originQuery", "?origin=SUBMITTED_APPLICATIONS&param1=abc&param2=def"))
                .andReturn();

        verify(applicationSummaryRestService).getSubmittedApplications(COMPETITION_ID, "", 0, 20, empty(), empty());
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

        when(applicationSummaryRestService.getIneligibleApplications(COMPETITION_ID, "", 0, 20, Optional.of(""), empty()))
                .thenReturn(restSuccess(expectedSummaryPageResource));
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID))
                .thenReturn(restSuccess(defaultExpectedCompetitionSummary));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/applications/ineligible", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/ineligible-applications"))
                .andExpect(model().attribute("originQuery", "?origin=INELIGIBLE_APPLICATIONS"))
                .andReturn();

        IneligibleApplicationsViewModel model = (IneligibleApplicationsViewModel) result.getModelAndView().getModel().get("model");

        verify(applicationSummaryRestService).getIneligibleApplications(COMPETITION_ID, "", 0, 20, Optional.of(""), empty());
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


        when(applicationSummaryRestService.getIneligibleApplications(COMPETITION_ID, "id", 1, 20, Optional.of("filter"),empty()))
                .thenReturn(restSuccess(expectedSummaryPageResource));
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID))
                .thenReturn(restSuccess(defaultExpectedCompetitionSummary));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/applications/ineligible?page=1&sort=id&filterSearch=filter", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/ineligible-applications"))
                .andExpect(model().attribute("originQuery", "?origin=INELIGIBLE_APPLICATIONS&page=1&sort=id&filterSearch=filter"))
                .andReturn();

        IneligibleApplicationsViewModel model = (IneligibleApplicationsViewModel) result.getModelAndView().getModel().get("model");

        verify(applicationSummaryRestService).getIneligibleApplications(COMPETITION_ID, "id", 1, 20, Optional.of("filter"), empty());
        verify(applicationSummaryRestService).getCompetitionSummary(COMPETITION_ID);

        assertEquals(COMPETITION_ID, model.getCompetitionId());
        assertEquals(defaultExpectedCompetitionSummary.getCompetitionName(), model.getCompetitionName());
        Pagination actualPagination = model.getPagination();
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

        when(applicationSummaryRestService.getIneligibleApplications(COMPETITION_ID, "", 0, 20, Optional.of(""), empty()))
                .thenReturn(restSuccess(expectedSummaryPageResource));
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID))
                .thenReturn(restSuccess(defaultExpectedCompetitionSummary));

        mockMvc.perform(get("/competition/{competitionId}/applications/ineligible?param1=abc&param2=def", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/ineligible-applications"))
                .andExpect(model().attribute("originQuery", "?origin=INELIGIBLE_APPLICATIONS&param1=abc&param2=def"))
                .andReturn();

        verify(applicationSummaryRestService).getIneligibleApplications(COMPETITION_ID, "", 0, 20, Optional.of(""), empty());
        verify(applicationSummaryRestService).getCompetitionSummary(COMPETITION_ID);
    }

    @Test
    public void unsuccessfulApplications() throws Exception {

        Long competitionId = 1L;
        int pageIndex = 0;
        int pageSize = 20;
        String sortField = "id";
        String filter = "ALL";

        String competitionName = "Competition One";
        List<ApplicationResource> unsuccessfulApplications = ApplicationResourceBuilder.newApplicationResource().build(2);
        ApplicationPageResource applicationPageResource = new ApplicationPageResource();
        UnsuccessfulApplicationsViewModel viewModel = new UnsuccessfulApplicationsViewModel(competitionId,
                competitionName, true, unsuccessfulApplications, unsuccessfulApplications.size(), new Pagination(applicationPageResource, ""));

        when(unsuccessfulApplicationsModelPopulator.populateModel(eq(competitionId), eq(pageIndex), eq(pageSize), eq(sortField), eq(filter), any(UserResource.class), any()))
                .thenReturn(viewModel);

        mockMvc.perform(get("/competition/{competitionId}/applications/previous?page={pageIndex}&size={pageSize}&sort={sortField}&filter={filter}",
                competitionId, pageIndex, pageSize, sortField, filter))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/previous-applications"))
                .andExpect(model().attribute("model", viewModel))
                .andReturn();
    }

    @Test
    public void markApplicationAsSuccessful() throws Exception {

        setLoggedInUser(newUserResource()
                .withRolesGlobal(singletonList(Role.IFS_ADMINISTRATOR))
                .build());

        ProjectResource projectResource = newProjectResource()
                .withId(1L)
                .withName("Successful project")
                .build();

        when(applicationFundingDecisionService.saveApplicationFundingDecisionData(anyLong(), any(FundingDecision.class), anyListOf(Long.class)))
                .thenReturn(ServiceResult.serviceSuccess());
        when(projectService.createProjectFromApplicationId(anyLong()))
                .thenReturn(ServiceResult.serviceSuccess(projectResource));

        mockMvc.perform(post("/competition/1/applications/mark-successful/application/2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/competition/{competitionId}/applications/previous"))
                .andReturn();

        verify(unsuccessfulApplicationsModelPopulator, never()).populateModel(anyLong(), anyInt(), anyInt(), anyString(), anyString(), any(UserResource.class), any());
        verify(applicationFundingDecisionService).saveApplicationFundingDecisionData(anyLong(), any(FundingDecision.class), anyListOf(Long.class));
        verify(projectService).createProjectFromApplicationId(anyLong());
        verifyNoMoreInteractions(applicationFundingDecisionService, projectService);

    }

    @Test
    public void allApplicationsSupportView() throws Exception {
        UserResource userResource = newUserResource().withRolesGlobal(singletonList(Role.SUPPORT)).build();

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

        when(applicationSummaryRestService.getAllApplications(COMPETITION_ID, "", 0, 20, Optional.empty()))
                .thenReturn(restSuccess(expectedSummaryPageResource));
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID))
                .thenReturn(restSuccess(defaultExpectedCompetitionSummary));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/applications/all", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/all-applications"))
                .andExpect(model().attribute("originQuery", "?origin=ALL_APPLICATIONS"))
                .andReturn();

        AllApplicationsViewModel model = (AllApplicationsViewModel) result.getModelAndView().getModel().get("model");

        verify(applicationSummaryRestService).getAllApplications(COMPETITION_ID, "", 0, 20, Optional.empty());
        verify(applicationSummaryRestService).getCompetitionSummary(COMPETITION_ID);

        assertEquals(COMPETITION_ID, model.getCompetitionId());
        assertEquals(defaultExpectedCompetitionSummary.getCompetitionName(), model.getCompetitionName());
        assertEquals(defaultExpectedCompetitionSummary.getApplicationsInProgress(), model.getApplicationsInProgress());
        assertEquals(defaultExpectedCompetitionSummary.getApplicationsStarted(), model.getApplicationsStarted());
        assertEquals(defaultExpectedCompetitionSummary.getApplicationsSubmitted(), model.getApplicationsSubmitted());
        assertEquals(defaultExpectedCompetitionSummary.getTotalNumberOfApplications(), model.getTotalNumberOfApplications());
        assertEquals("Applications", model.getBackTitle());
        assertEquals("/competition/1/applications", model.getBackURL());
        assertEquals(expectedApplicationRows, model.getApplications());
    }

    @Test
    public void allApplicationsSupportViewInnovationLead() throws Exception {
        UserResource userResource = newUserResource().withRolesGlobal(singletonList(Role.INNOVATION_LEAD)).build();

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

        when(applicationSummaryRestService.getAllApplications(COMPETITION_ID, "", 0, 20, Optional.empty()))
                .thenReturn(restSuccess(expectedSummaryPageResource));
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID))
                .thenReturn(restSuccess(defaultExpectedCompetitionSummary));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/applications/all", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/all-applications"))
                .andExpect(model().attribute("originQuery", "?origin=ALL_APPLICATIONS"))
                .andReturn();

        AllApplicationsViewModel model = (AllApplicationsViewModel) result.getModelAndView().getModel().get("model");

        verify(applicationSummaryRestService).getAllApplications(COMPETITION_ID, "", 0, 20, Optional.empty());
        verify(applicationSummaryRestService).getCompetitionSummary(COMPETITION_ID);

        assertEquals(COMPETITION_ID, model.getCompetitionId());
        assertEquals(defaultExpectedCompetitionSummary.getCompetitionName(), model.getCompetitionName());
        assertEquals(defaultExpectedCompetitionSummary.getApplicationsInProgress(), model.getApplicationsInProgress());
        assertEquals(defaultExpectedCompetitionSummary.getApplicationsStarted(), model.getApplicationsStarted());
        assertEquals(defaultExpectedCompetitionSummary.getApplicationsSubmitted(), model.getApplicationsSubmitted());
        assertEquals(defaultExpectedCompetitionSummary.getTotalNumberOfApplications(), model.getTotalNumberOfApplications());
        assertEquals("Applications", model.getBackTitle());
        assertEquals("/competition/1/applications", model.getBackURL());
        assertEquals(expectedApplicationRows, model.getApplications());
    }
}
