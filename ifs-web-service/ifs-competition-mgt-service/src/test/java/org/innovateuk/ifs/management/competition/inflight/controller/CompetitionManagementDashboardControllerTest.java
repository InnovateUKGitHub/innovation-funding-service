package org.innovateuk.ifs.management.competition.inflight.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.builder.ApplicationResourceBuilder;
import org.innovateuk.ifs.application.resource.ApplicationPageResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.resource.search.CompetitionSearchResult;
import org.innovateuk.ifs.competition.resource.search.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.search.UpcomingCompetitionSearchResultItem;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.competition.service.CompetitionSetupStakeholderRestService;
import org.innovateuk.ifs.management.competition.inflight.controller.CompetitionManagementDashboardController;
import org.innovateuk.ifs.management.dashboard.service.CompetitionDashboardSearchService;
import org.innovateuk.ifs.management.dashboard.viewmodel.*;
import org.innovateuk.ifs.project.bankdetails.service.BankDetailsRestService;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static java.util.Arrays.asList;
import static junit.framework.TestCase.assertFalse;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.LiveCompetitionSearchResultItemBuilder.newLiveCompetitionSearchResultItem;
import static org.innovateuk.ifs.competition.builder.PreviousCompetitionSearchResultItemBuilder.newPreviousCompetitionSearchResultItem;
import static org.innovateuk.ifs.competition.builder.UpcomingCompetitionSearchResultItemBuilder.newUpcomingCompetitionSearchResultItem;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.*;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Class for testing public functions of {@link CompetitionManagementDashboardController}
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class CompetitionManagementDashboardControllerTest extends BaseControllerMockMVCTest<CompetitionManagementDashboardController> {

    private static final String INNOVATION_AREA_NAME_ONE = "one";
    private static final String INNOVATION_AREA_NAME_TWO = "two";
    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 40;

    @InjectMocks
    private CompetitionManagementDashboardController controller;

    @Mock
    private CompetitionDashboardSearchService competitionDashboardSearchService;

    @Mock
    private CompetitionSetupRestService competitionSetupRestService;

    @Mock
    private BankDetailsRestService bankDetailsRestService;

    @Mock
    private CompetitionSetupStakeholderRestService competitionSetupStakeholderRestService;

    private CompetitionCountResource counts;

    private Map<CompetitionStatus, List<CompetitionSearchResultItem>> competitions;

    @Before
    public void setUp() {
        counts = new CompetitionCountResource();
        competitions = new HashMap<>();
        CompetitionSearchResultItem openItem = newLiveCompetitionSearchResultItem().withInnovationAreaNames(new HashSet<>(asList(INNOVATION_AREA_NAME_ONE, INNOVATION_AREA_NAME_TWO))).build();
        competitions.put(CompetitionStatus.OPEN, asList(openItem));
        when(competitionDashboardSearchService.getCompetitionCounts()).thenReturn(counts);
    }

    @Test
    public void showingDashboard() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/dashboard"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void liveDashboard() throws Exception {

        setLoggedInUser(newUserResource().withRoleGlobal(Role.COMP_ADMIN).build());

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
    public void stakeholderLiveDashboard() throws Exception {

        setLoggedInUser(newUserResource().withRoleGlobal(Role.STAKEHOLDER).build());

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
        assertFalse(viewModel.getTabs().upcoming());
        assertFalse(viewModel.getTabs().nonIFS());
        assertTrue(viewModel.getTabs().projectSetup());
        assertTrue(viewModel.getTabs().previous());
        assertFalse(viewModel.getTabs().support());
    }

    @Test
    public void projectSetupDashboardWithNonProjectFinanceUser() throws Exception {
        int page = 1;
        Long countBankDetails = 0L;
        setLoggedInUser(newUserResource().withRoleGlobal(Role.COMP_ADMIN).build());

        List<UpcomingCompetitionSearchResultItem> competitions = newUpcomingCompetitionSearchResultItem()
                .withCompetitionStatus(OPEN)
                .build(1);
        CompetitionSearchResult searchResult = new CompetitionSearchResult();
        searchResult.setContent(new ArrayList<>(competitions));
        when(competitionDashboardSearchService.getProjectSetupCompetitions(page)).thenReturn(searchResult);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/project-setup")
                .param("page", String.valueOf(page)))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/projectSetup"))
                .andReturn();

        Object model = result.getModelAndView().getModelMap().get("model");
        assertTrue(model.getClass().equals(ProjectSetupDashboardViewModel.class));

        ProjectSetupDashboardViewModel viewModel = (ProjectSetupDashboardViewModel) model;
        assertEquals(searchResult, viewModel.getResult());
        assertEquals(counts, viewModel.getCounts());
        assertEquals(countBankDetails, viewModel.getCountBankDetails());
        assertFalse(viewModel.isProjectFinanceUser());

        verify(bankDetailsRestService, never()).countPendingBankDetailsApprovals();
    }

    @Test
    public void projectSetupDashboardWithProjectFinanceUser() throws Exception {
        int page = 1;
        Long countBankDetails = 8L;
        setLoggedInUser(newUserResource().withRoleGlobal(Role.PROJECT_FINANCE).build());

        List<UpcomingCompetitionSearchResultItem> competitions = newUpcomingCompetitionSearchResultItem()
                .withCompetitionStatus(PROJECT_SETUP)
                .build(1);
        CompetitionSearchResult searchResult = new CompetitionSearchResult();
        searchResult.setContent(new ArrayList<>(competitions));

        when(competitionDashboardSearchService.getProjectSetupCompetitions(page)).thenReturn(searchResult);
        when(bankDetailsRestService.countPendingBankDetailsApprovals()).thenReturn(restSuccess(countBankDetails));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/project-setup")
                .param("page", String.valueOf(page)))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/projectSetup"))
                .andReturn();

        Object model = result.getModelAndView().getModelMap().get("model");
        assertTrue(model.getClass().equals(ProjectSetupDashboardViewModel.class));

        ProjectSetupDashboardViewModel viewModel = (ProjectSetupDashboardViewModel) model;

        assertEquals(searchResult, viewModel.getResult());
        assertEquals(counts, viewModel.getCounts());
        assertEquals(countBankDetails, viewModel.getCountBankDetails());
        assertTrue(viewModel.isProjectFinanceUser());

        verify(bankDetailsRestService, only()).countPendingBankDetailsApprovals();
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
    }

    @Test
    public void nonIfsDashboard() throws Exception {
        int page = 1;

        List<UpcomingCompetitionSearchResultItem> competitions = newUpcomingCompetitionSearchResultItem()
                .withCompetitionStatus(COMPETITION_SETUP)
                .build(1);
        CompetitionCountResource counts = new CompetitionCountResource();
        CompetitionSearchResult searchResult = new CompetitionSearchResult();
        searchResult.setContent(new ArrayList<>(competitions));


        when(competitionDashboardSearchService.getNonIfsCompetitions(page)).thenReturn(searchResult);
        when(competitionDashboardSearchService.getCompetitionCounts()).thenReturn(counts);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/non-ifs")
                .param("page", String.valueOf(page)))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/non-ifs"))
                .andReturn();

        Object model = result.getModelAndView().getModelMap().get("model");
        assertTrue(model.getClass().equals(NonIfsDashboardViewModel.class));

        NonIfsDashboardViewModel viewModel = (NonIfsDashboardViewModel) model;
        assertEquals(searchResult, viewModel.getResult());
        assertEquals(counts, viewModel.getCounts());
    }

    @Test
    public void previousDashboard() throws Exception {
        int page = 1;

        List<CompetitionSearchResultItem> competitions = new ArrayList<>();
        competitions.add(newPreviousCompetitionSearchResultItem().withCompetitionStatus(PROJECT_SETUP).withId(111L).build());
        competitions.add(newPreviousCompetitionSearchResultItem().withCompetitionStatus(PROJECT_SETUP).withId(222L).build());
        CompetitionCountResource counts = new CompetitionCountResource();
        CompetitionSearchResult searchResult = new CompetitionSearchResult();
        searchResult.setContent(competitions);

        Map<CompetitionStatus, List<CompetitionSearchResultItem>> competitionMap = new HashMap<>();
        competitionMap.put(PROJECT_SETUP, competitions);

        when(competitionDashboardSearchService.getPreviousCompetitions(page)).thenReturn(searchResult);
        when(competitionDashboardSearchService.getCompetitionCounts()).thenReturn(counts);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/previous")
                .param("page", String.valueOf(page)))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/previous"))
                .andReturn();

        Object model = result.getModelAndView().getModelMap().get("model");
        assertTrue(model.getClass().equals(PreviousDashboardViewModel.class));

        PreviousDashboardViewModel viewModel = (PreviousDashboardViewModel) model;
        assertEquals(searchResult, viewModel.getResult());
        assertEquals(counts, viewModel.getCounts());
    }

    @Test
    public void searchDashboard() throws Exception {
        CompetitionSearchResult searchResult = new CompetitionSearchResult();
        String searchQuery = "search";
        int defaultPage = 0;

        when(competitionDashboardSearchService.searchCompetitions(searchQuery, defaultPage)).thenReturn(searchResult);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/internal/search?searchQuery=" + searchQuery))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/search"))
                .andReturn();

        CompetitionSearchDashboardViewModel competitionSearchDashboardViewModel = (CompetitionSearchDashboardViewModel) result.getModelAndView().getModel().get("model");
        CompetitionSearchResult actualCompetitionSearchResult = competitionSearchDashboardViewModel.getCompetitions();
        String actualSearchQuery = competitionSearchDashboardViewModel.getSearchQuery();

        assertEquals(searchResult, actualCompetitionSearchResult);
        assertEquals(searchQuery, actualSearchQuery);
        verify(competitionDashboardSearchService).searchCompetitions(searchQuery, defaultPage);
    }

    @Test
    public void searchDashboardWithoutSearchQuery() throws Exception {
        CompetitionSearchResult searchResult = new CompetitionSearchResult();
        String searchQuery = "";
        int defaultPage = 0;

        when(competitionDashboardSearchService.searchCompetitions(searchQuery, defaultPage)).thenReturn(searchResult);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/internal/search"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/search"))
                .andReturn();

        CompetitionSearchDashboardViewModel competitionSearchDashboardViewModel = (CompetitionSearchDashboardViewModel) result.getModelAndView().getModel().get("model");
        CompetitionSearchResult actualCompetitionSearchResult = competitionSearchDashboardViewModel.getCompetitions();
        String actualSearchQuery = competitionSearchDashboardViewModel.getSearchQuery();

        assertEquals(searchResult, actualCompetitionSearchResult);
        assertEquals(searchQuery, actualSearchQuery);
        verify(competitionDashboardSearchService).searchCompetitions(searchQuery, defaultPage);

    }

    @Test
    public void searchDashboardWithExtraWhitespace() throws Exception {

        CompetitionSearchResult searchResult = new CompetitionSearchResult();
        String searchQuery = "  search  term  ";
        String trimmedQuery = "search term";
        int defaultPage = 0;

        when(competitionDashboardSearchService.searchCompetitions(trimmedQuery, defaultPage)).thenReturn(searchResult);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/internal/search?searchQuery=" + searchQuery))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/search"))
                .andReturn();

        CompetitionSearchDashboardViewModel competitionSearchDashboardViewModel = (CompetitionSearchDashboardViewModel) result.getModelAndView().getModel().get("model");
        CompetitionSearchResult actualCompetitionSearchResult = competitionSearchDashboardViewModel.getCompetitions();
        String actualSearchQuery = competitionSearchDashboardViewModel.getSearchQuery();

        assertEquals(searchResult, actualCompetitionSearchResult);
        assertEquals(trimmedQuery, actualSearchQuery);
        verify(competitionDashboardSearchService, times(1)).searchCompetitions(trimmedQuery, defaultPage);
    }

    @Test
    public void internalUserNumericInputSearchReturnsApplication() throws Exception {
        String searchQuery = "12";

        List<ApplicationResource> applicationResources = ApplicationResourceBuilder.newApplicationResource().build(4);

        ApplicationPageResource expectedApplicationPageResource = new ApplicationPageResource(applicationResources.size(), 5, applicationResources, PAGE_NUMBER, PAGE_SIZE);
        when(competitionDashboardSearchService.wildcardSearchByApplicationId(searchQuery, PAGE_NUMBER, PAGE_SIZE)).thenReturn(expectedApplicationPageResource);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/internal/search?searchQuery=" + searchQuery))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/application-search"))
                .andReturn();

        ApplicationSearchDashboardViewModel model = (ApplicationSearchDashboardViewModel) result.getModelAndView().getModelMap().get("model");

        assertEquals(applicationResources, model.getApplications());
        assertEquals(4L, model.getApplicationCount());
        assertEquals(searchQuery, model.getSearchString());
        assertEquals(5, model.getApplicationPagination().getTotalPages());
        assertEquals(0, model.getApplicationPagination().getCurrentPage());
        assertEquals(40, model.getApplicationPagination().getPageSize());

        verify(competitionDashboardSearchService).wildcardSearchByApplicationId(searchQuery, PAGE_NUMBER, PAGE_SIZE);
    }

    @Test
    public void internalAlphabeticalInputSearchReturnsCompetition() throws Exception {
        List<UpcomingCompetitionSearchResultItem> competitions = newUpcomingCompetitionSearchResultItem()
                .withCompetitionStatus(PROJECT_SETUP)
                .build(1);
        CompetitionSearchResult searchResult = new CompetitionSearchResult();
        searchResult.setContent(new ArrayList<>(competitions));
        String searchQuery = "search";
        int defaultPage = 0;

        when(competitionDashboardSearchService.searchCompetitions(searchQuery, defaultPage)).thenReturn(searchResult);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/internal/search?searchQuery=" + searchQuery))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard/search"))
                .andReturn();

        CompetitionSearchDashboardViewModel competitionSearchDashboardViewModel = (CompetitionSearchDashboardViewModel) result.getModelAndView().getModel().get("model");
        CompetitionSearchResult actualCompetitionSearchResult = competitionSearchDashboardViewModel.getCompetitions();
        String actualSearchQuery = competitionSearchDashboardViewModel.getSearchQuery();

        assertEquals(searchQuery, actualSearchQuery);
        assertNotNull(actualCompetitionSearchResult);
        assertEquals(searchResult.getMappedCompetitions(), actualCompetitionSearchResult.getMappedCompetitions());
        assertEquals(searchResult, actualCompetitionSearchResult);
        verify(competitionDashboardSearchService).searchCompetitions(searchQuery, defaultPage);
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
    public void liveDashBoardSupportView() throws Exception {

        setLoggedInUser(newUserResource().withRoleGlobal(Role.SUPPORT).build());

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
    public void liveDashBoardSupportViewInnovationLead() throws Exception {

        setLoggedInUser(newUserResource().withRoleGlobal(Role.INNOVATION_LEAD).build());

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
        assertFalse(viewModel.getTabs().nonIFS());
        assertFalse(viewModel.getTabs().upcoming());
        assertTrue(viewModel.getTabs().projectSetup());
        assertTrue(viewModel.getTabs().previous());
    }

    @Override
    protected CompetitionManagementDashboardController supplyControllerUnderTest() {
        return new CompetitionManagementDashboardController(competitionDashboardSearchService, competitionSetupRestService, bankDetailsRestService, competitionSetupStakeholderRestService);
    }
}
