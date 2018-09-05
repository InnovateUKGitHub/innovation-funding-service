package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.builder.ApplicationResourceBuilder;
import org.innovateuk.ifs.application.resource.ApplicationPageResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResult;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.management.competition.controller.CompetitionManagementDashboardController;
import org.innovateuk.ifs.management.dashboard.service.CompetitionDashboardSearchService;
import org.innovateuk.ifs.management.dashboard.viewmodel.*;
import org.innovateuk.ifs.project.bankdetails.service.BankDetailsRestService;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.ZonedDateTime;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.CoreMatchers.is;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionSearchResultItemBuilder.newCompetitionSearchResultItem;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.COMPETITION_SETUP;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.PROJECT_SETUP;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Class for testing public functions of {@link CompetitionManagementDashboardController}
 */
@RunWith(MockitoJUnitRunner.class)
public class CompetitionManagementDashboardControllerTest extends BaseControllerMockMVCTest<CompetitionManagementDashboardController> {

    private static final String INNOVATION_AREA_NAME_ONE = "one";
    private static final String INNOVATION_AREA_NAME_TWO = "two";

    @InjectMocks
	private CompetitionManagementDashboardController controller;

    @Mock
    private CompetitionDashboardSearchService competitionDashboardSearchService;

    @Mock
    private CompetitionSetupRestService competitionSetupRestService;

    @Mock
    private BankDetailsRestService bankDetailsRestService;

    private CompetitionCountResource counts;

    private Map<CompetitionStatus, List<CompetitionSearchResultItem>> competitions;

    @Override
    @Before
    public void setUp() {
        super.setUp();

        competitions = new HashMap<>();
        addInnovationAreaNamesToCompetitions(competitions);
        counts = new CompetitionCountResource();

        when(competitionDashboardSearchService.getCompetitionCounts()).thenReturn(counts);
    }

    @Test
    public void showingDashboard() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/dashboard"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void liveDashboard() throws Exception {

        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.COMP_ADMIN)).build());

        when(competitionDashboardSearchService.getLiveCompetitions()).thenReturn(competitions);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/live"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/live"))
                .andReturn();

        Object model = result.getModelAndView().getModelMap().get("model");
        assertTrue(model.getClass().equals(LiveDashboardViewModel.class));

        LiveDashboardViewModel viewModel = (LiveDashboardViewModel) model;
        assertEquals(competitions, viewModel.getCompetitions());
        assertEquals(counts, viewModel.getCounts());
        assertTrue(viewModel.getTabs().live());
    }

    @Test
    public void projectSetupDashboardWithNonProjectFinanceUser() throws Exception {

        Long countBankDetails = 0L;
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.COMP_ADMIN)).build());

        when(competitionDashboardSearchService.getProjectSetupCompetitions()).thenReturn(competitions);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/project-setup"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/projectSetup"))
                .andReturn();

        Object model = result.getModelAndView().getModelMap().get("model");
        assertTrue(model.getClass().equals(ProjectSetupDashboardViewModel.class));

        ProjectSetupDashboardViewModel viewModel = (ProjectSetupDashboardViewModel) model;
        assertEquals(competitions.get(INNOVATION_AREA_NAME_ONE), viewModel.getCompetitions().get(PROJECT_SETUP));
        assertEquals(counts, viewModel.getCounts());
        assertEquals(countBankDetails, viewModel.getCountBankDetails());
        assertEquals(false, viewModel.isProjectFinanceUser());

        verify(bankDetailsRestService, never()).countPendingBankDetailsApprovals();
    }

    @Test
    public void projectSetupDashboardWithProjectFinanceUser() throws Exception {

        Long countBankDetails = 8L;
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.PROJECT_FINANCE)).build());

        when(competitionDashboardSearchService.getProjectSetupCompetitions()).thenReturn(competitions);
        when(bankDetailsRestService.countPendingBankDetailsApprovals()).thenReturn(restSuccess(countBankDetails));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/project-setup"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/projectSetup"))
                .andReturn();

        Object model = result.getModelAndView().getModelMap().get("model");
        assertTrue(model.getClass().equals(ProjectSetupDashboardViewModel.class));

        ProjectSetupDashboardViewModel viewModel = (ProjectSetupDashboardViewModel) model;
        assertEquals(competitions.get(INNOVATION_AREA_NAME_ONE), viewModel.getCompetitions().get(PROJECT_SETUP));
        assertEquals(counts, viewModel.getCounts());
        assertEquals(countBankDetails, viewModel.getCountBankDetails());
        assertEquals(true, viewModel.isProjectFinanceUser());

        verify(bankDetailsRestService, only()).countPendingBankDetailsApprovals();
    }

    private void addInnovationAreaNamesToCompetitions(Map<CompetitionStatus, List<CompetitionSearchResultItem>> competitions ) {
        CompetitionSearchResultItem openItem = newCompetitionSearchResultItem().withInnovationAreaNames(new HashSet<>(asList(INNOVATION_AREA_NAME_ONE, INNOVATION_AREA_NAME_TWO))).build();
        competitions.put(CompetitionStatus.OPEN, asList(openItem));
    }

    @Test
    public void upcomingDashboard() throws Exception {

        when(competitionDashboardSearchService.getUpcomingCompetitions()).thenReturn(competitions);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/upcoming"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/upcoming"))
                .andReturn();

        Object model = result.getModelAndView().getModelMap().get("model");
        assertTrue(model.getClass().equals(UpcomingDashboardViewModel.class));

        UpcomingDashboardViewModel viewModel = (UpcomingDashboardViewModel) model;
        assertEquals(competitions, viewModel.getCompetitions());
        assertEquals(counts, viewModel.getCounts());
        assertEquals(asList(INNOVATION_AREA_NAME_ONE + ", " + INNOVATION_AREA_NAME_TWO), viewModel.getFormattedInnovationAreas());
    }

    @Test
    public void nonIfsDashboard() throws Exception {

        List<CompetitionSearchResultItem> competitions = new ArrayList<>();
        CompetitionCountResource counts = new CompetitionCountResource();

        Map<CompetitionStatus, List<CompetitionSearchResultItem>> competitionMap = new HashMap<>();
        competitionMap.put(COMPETITION_SETUP, competitions);

        when(competitionDashboardSearchService.getNonIfsCompetitions()).thenReturn(competitionMap);
        when(competitionDashboardSearchService.getCompetitionCounts()).thenReturn(counts);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/non-ifs"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/non-ifs"))
                .andReturn();

        Object model = result.getModelAndView().getModelMap().get("model");
        assertTrue(model.getClass().equals(NonIFSDashboardViewModel.class));

        NonIFSDashboardViewModel viewModel = (NonIFSDashboardViewModel) model;
        assertEquals(competitionMap, viewModel.getCompetitions());
        assertEquals(counts, viewModel.getCounts());
    }

    @Test
    public void previousDashboard() throws Exception {

        List<CompetitionSearchResultItem> competitions = new ArrayList<>();
        competitions.add(newCompetitionSearchResultItem().withId(111L).withOpenDate(ZonedDateTime.now()).build());
        competitions.add(newCompetitionSearchResultItem().withId(222L).withOpenDate(ZonedDateTime.now().plusMinutes(10L)).build());
        CompetitionCountResource counts = new CompetitionCountResource();

        Map<CompetitionStatus, List<CompetitionSearchResultItem>> competitionMap = new HashMap<>();
        competitionMap.put(PROJECT_SETUP, competitions);

        when(competitionDashboardSearchService.getPreviousCompetitions()).thenReturn(competitionMap);
        when(competitionDashboardSearchService.getCompetitionCounts()).thenReturn(counts);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/previous"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/previous"))
                .andReturn();

        Object model = result.getModelAndView().getModelMap().get("model");
        assertTrue(model.getClass().equals(PreviousDashboardViewModel.class));

        PreviousDashboardViewModel viewModel = (PreviousDashboardViewModel) model;
        assertEquals(competitions.get(1), viewModel.getCompetitions().get(PROJECT_SETUP).get(1));
        assertEquals(competitions.get(0), viewModel.getCompetitions().get(PROJECT_SETUP).get(0));
        assertEquals(counts, viewModel.getCounts());
    }

    @Test
    public void searchDashboard() throws Exception {
        CompetitionSearchResult searchResult = new CompetitionSearchResult();
        String searchQuery = "search";
        int defaultPage = 0;

        when(competitionDashboardSearchService.searchCompetitions(searchQuery, defaultPage)).thenReturn(searchResult);

        mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/search?searchQuery=" + searchQuery))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/search"))
                .andExpect(model().attribute("results", is(searchResult)));
    }

    @Test
    public void searchDashboardWithoutSearchQuery() throws Exception {
        CompetitionSearchResult searchResult = new CompetitionSearchResult();
        String searchQuery = "";
        int defaultPage = 0;

        when(competitionDashboardSearchService.searchCompetitions(searchQuery, defaultPage)).thenReturn(searchResult);

        mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/search"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/search"))
                .andExpect(model().attribute("results", is(searchResult)));
    }

    @Test
    public void searchDashboardWithExtraWhitespace() throws Exception {
        CompetitionSearchResult searchResult = new CompetitionSearchResult();
        String searchQuery = "  search  term  ";
        String trimmedQuery = "search term";
        int defaultPage = 0;

        when(competitionDashboardSearchService.searchCompetitions(trimmedQuery, defaultPage)).thenReturn(searchResult);

        mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/search?searchQuery=" + searchQuery))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/search"))
                .andExpect(model().attribute("results", is(searchResult)));

        verify(competitionDashboardSearchService, times(1)).searchCompetitions(trimmedQuery, defaultPage);
    }

    @Test
    public void applicationSearchWhenSearchStringNotSpecified() throws Exception {
        int pageNumber = 0;
        int pageSize = 40;

        List<ApplicationResource> applicationResources = ApplicationResourceBuilder.newApplicationResource().build(4);

        ApplicationPageResource expectedApplicationPageResource = new ApplicationPageResource(applicationResources.size(), 5, applicationResources, pageNumber, pageSize);
        when(competitionDashboardSearchService.wildcardSearchByApplicationId("", pageNumber, pageSize)).thenReturn(expectedApplicationPageResource);

        mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/application/search"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/application-search"));

        verify(competitionDashboardSearchService).wildcardSearchByApplicationId("", pageNumber, pageSize);

    }

    @Test
    public void applicationSearchWhenSearchStringHasWhiteSpaces() throws Exception {
        String searchString = "           12           ";
        int pageNumber = 0;
        int pageSize = 40;

        List<ApplicationResource> applicationResources = ApplicationResourceBuilder.newApplicationResource().build(4);

        ApplicationPageResource expectedApplicationPageResource = new ApplicationPageResource(applicationResources.size(), 5, applicationResources, pageNumber, pageSize);
        when(competitionDashboardSearchService.wildcardSearchByApplicationId("12", pageNumber, pageSize)).thenReturn(expectedApplicationPageResource);

        mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/application/search?searchString=" + searchString))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/application-search"));

        verify(competitionDashboardSearchService).wildcardSearchByApplicationId("12", pageNumber, pageSize);

    }

    @Test
    public void applicationSearch() throws Exception {
        String searchString = "12";
        int pageNumber = 0;
        int pageSize = 40;

        List<ApplicationResource> applicationResources = ApplicationResourceBuilder.newApplicationResource().build(4);

        ApplicationPageResource expectedApplicationPageResource = new ApplicationPageResource(applicationResources.size(), 5, applicationResources, pageNumber, pageSize);
        when(competitionDashboardSearchService.wildcardSearchByApplicationId(searchString, pageNumber, pageSize)).thenReturn(expectedApplicationPageResource);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/application/search?searchString=" + searchString))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/application-search"))
                .andReturn();

        ApplicationSearchDashboardViewModel model = (ApplicationSearchDashboardViewModel) result.getModelAndView().getModelMap().get("model");

        assertEquals(applicationResources, model.getApplications());
        assertEquals(4L, model.getApplicationCount());
        assertEquals(searchString, model.getSearchString());
        assertEquals(5, model.getApplicationPagination().getTotalPages());
        assertEquals(0, model.getApplicationPagination().getCurrentPage());
        assertEquals(40, model.getApplicationPagination().getPageSize());
        assertEquals(false, model.isSupport());

        verify(competitionDashboardSearchService).wildcardSearchByApplicationId(searchString, pageNumber, pageSize);

    }

    @Test
    public void createCompetition() throws Exception {
        Long competitionId = 1L;

        when(competitionSetupRestService.create()).thenReturn(restSuccess(newCompetitionResource().withId(competitionId).build()));

        mockMvc.perform(get("/competition/create"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/setup/" + competitionId));
    }

    @Test
    public void testLiveDashBoardSupportView() throws Exception {

        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.SUPPORT)).build());

        when(competitionDashboardSearchService.getLiveCompetitions()).thenReturn(competitions);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/live"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/live"))
                .andReturn();

        Object model = result.getModelAndView().getModelMap().get("model");
        assertTrue(model.getClass().equals(LiveDashboardViewModel.class));

        LiveDashboardViewModel viewModel = (LiveDashboardViewModel) model;
        assertEquals(competitions, viewModel.getCompetitions());
        assertEquals(counts, viewModel.getCounts());
        assertTrue(viewModel.getTabs().live());
        assertTrue(viewModel.getTabs().projectSetup());
        assertTrue(viewModel.getTabs().previous());
        assertFalse(viewModel.getTabs().nonIFS());
        assertFalse(viewModel.getTabs().upcoming());
    }

    @Test
    public void testLiveDashBoardSupportViewInnovationLead() throws Exception {

        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.INNOVATION_LEAD)).build());

        when(competitionDashboardSearchService.getLiveCompetitions()).thenReturn(competitions);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/live"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/live"))
                .andReturn();

        Object model = result.getModelAndView().getModelMap().get("model");
        assertTrue(model.getClass().equals(LiveDashboardViewModel.class));

        LiveDashboardViewModel viewModel = (LiveDashboardViewModel) model;
        assertEquals(competitions, viewModel.getCompetitions());
        assertEquals(counts, viewModel.getCounts());
        assertEquals(true, viewModel.getTabs().live());
        assertEquals(false, viewModel.getTabs().nonIFS());
        assertEquals(false, viewModel.getTabs().upcoming());
        assertEquals(true, viewModel.getTabs().projectSetup());
        assertEquals(true, viewModel.getTabs().previous());
    }

    @Override
    protected CompetitionManagementDashboardController supplyControllerUnderTest() {
        return new CompetitionManagementDashboardController();
    }
}
