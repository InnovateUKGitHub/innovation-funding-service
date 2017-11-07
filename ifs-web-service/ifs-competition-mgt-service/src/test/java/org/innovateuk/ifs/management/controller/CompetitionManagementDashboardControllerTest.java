package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResult;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionSetupRestService;
import org.innovateuk.ifs.management.controller.dashboard.CompetitionManagementDashboardController;
import org.innovateuk.ifs.management.service.CompetitionDashboardSearchService;
import org.innovateuk.ifs.management.viewmodel.dashboard.*;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.ZonedDateTime;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionSearchResultItemBuilder.newCompetitionSearchResultItem;
import static org.innovateuk.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    private CompetitionCountResource counts;

    private Map<CompetitionStatus, List<CompetitionSearchResultItem>> competitions;

    @Override
    @Before
    public void setUp() {
        super.setUp();

        competitions = new HashMap<>();
        addInnovationAreaNamesToCompetitions(competitions);
        counts = new CompetitionCountResource();

        Mockito.when(competitionDashboardSearchService.getCompetitionCounts()).thenReturn(counts);
    }

    @Test
    public void showingDashboard() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/dashboard"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }

    @Test
    public void liveDashboard() throws Exception {

        Mockito.when(competitionDashboardSearchService.getLiveCompetitions()).thenReturn(competitions);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/live"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("dashboard/live"))
                .andReturn();

        Object model = result.getModelAndView().getModelMap().get("model");
        assertTrue(model.getClass().equals(LiveDashboardViewModel.class));

        LiveDashboardViewModel viewModel = (LiveDashboardViewModel) model;
        assertEquals(competitions, viewModel.getCompetitions());
        assertEquals(counts, viewModel.getCounts());
        assertEquals(false, viewModel.isSupportView());
    }

    @Test
    public void projectSetupDashboard() throws Exception {

        Mockito.when(competitionDashboardSearchService.getProjectSetupCompetitions()).thenReturn(competitions.get(INNOVATION_AREA_NAME_ONE));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/project-setup"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("dashboard/projectSetup"))
                .andReturn();

        Object model = result.getModelAndView().getModelMap().get("model");
        assertTrue(model.getClass().equals(ProjectSetupDashboardViewModel.class));

        ProjectSetupDashboardViewModel viewModel = (ProjectSetupDashboardViewModel) model;
        assertEquals(competitions.get(INNOVATION_AREA_NAME_ONE), viewModel.getCompetitions());
        assertEquals(counts, viewModel.getCounts());
    }

    private void addInnovationAreaNamesToCompetitions(Map<CompetitionStatus, List<CompetitionSearchResultItem>> competitions ) {
        CompetitionSearchResultItem openItem = newCompetitionSearchResultItem().withInnovationAreaNames(new HashSet<>(asList(INNOVATION_AREA_NAME_ONE, INNOVATION_AREA_NAME_TWO))).build();
        competitions.put(CompetitionStatus.OPEN, asList(openItem));
    }

    @Test
    public void upcomingDashboard() throws Exception {

        Mockito.when(competitionDashboardSearchService.getUpcomingCompetitions()).thenReturn(competitions);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/upcoming"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("dashboard/upcoming"))
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

        Mockito.when(competitionDashboardSearchService.getNonIfsCompetitions()).thenReturn(competitions);
        Mockito.when(competitionDashboardSearchService.getCompetitionCounts()).thenReturn(counts);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/non-ifs"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("dashboard/non-ifs"))
                .andReturn();

        Object model = result.getModelAndView().getModelMap().get("model");
        assertTrue(model.getClass().equals(NonIFSDashboardViewModel.class));

        NonIFSDashboardViewModel viewModel = (NonIFSDashboardViewModel) model;
        assertEquals(competitions, viewModel.getCompetitions());
        assertEquals(counts, viewModel.getCounts());
    }

    @Test
    public void previousDashboard() throws Exception {

        List<CompetitionSearchResultItem> competitions = new ArrayList<>();
        competitions.add(newCompetitionSearchResultItem().withId(111L).withOpenDate(ZonedDateTime.now()).build());
        competitions.add(newCompetitionSearchResultItem().withId(222L).withOpenDate(ZonedDateTime.now().plusMinutes(10L)).build());
        CompetitionCountResource counts = new CompetitionCountResource();

        Mockito.when(competitionDashboardSearchService.getPreviousCompetitions()).thenReturn(competitions);
        Mockito.when(competitionDashboardSearchService.getCompetitionCounts()).thenReturn(counts);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/previous"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("dashboard/previous"))
                .andReturn();

        Object model = result.getModelAndView().getModelMap().get("model");
        assertTrue(model.getClass().equals(PreviousDashboardViewModel.class));

        PreviousDashboardViewModel viewModel = (PreviousDashboardViewModel) model;
        assertEquals(competitions.get(1), viewModel.getCompetitions().get(0));
        assertEquals(competitions.get(0), viewModel.getCompetitions().get(1));
        assertEquals(counts, viewModel.getCounts());
    }

    @Test
    public void searchDashboard() throws Exception {
        CompetitionSearchResult searchResult = new CompetitionSearchResult();
        String searchQuery = "search";
        int defaultPage = 0;

        Mockito.when(competitionDashboardSearchService.searchCompetitions(searchQuery, defaultPage)).thenReturn(searchResult);

        mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/search?searchQuery=" + searchQuery))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("dashboard/search"))
                .andExpect(MockMvcResultMatchers.model().attribute("results", is(searchResult)));
    }

    @Test
    public void searchDashboardWithoutSearchQuery() throws Exception {
        CompetitionSearchResult searchResult = new CompetitionSearchResult();
        String searchQuery = "";
        int defaultPage = 0;

        Mockito.when(competitionDashboardSearchService.searchCompetitions(searchQuery, defaultPage)).thenReturn(searchResult);

        mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/search"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("dashboard/search"))
                .andExpect(MockMvcResultMatchers.model().attribute("results", is(searchResult)));
    }

    @Test
    public void searchDashboardWithExtraWhitespace() throws Exception {
        CompetitionSearchResult searchResult = new CompetitionSearchResult();
        String searchQuery = "  search  term  ";
        String trimmedQuery = "search term";
        int defaultPage = 0;

        Mockito.when(competitionDashboardSearchService.searchCompetitions(trimmedQuery, defaultPage)).thenReturn(searchResult);

        mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/search?searchQuery=" + searchQuery))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("dashboard/search"))
                .andExpect(MockMvcResultMatchers.model().attribute("results", is(searchResult)));

        verify(competitionDashboardSearchService, times(1)).searchCompetitions(trimmedQuery, defaultPage);
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

        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(UserRoleType.SUPPORT).build())).build());

        Mockito.when(competitionDashboardSearchService.getLiveCompetitions()).thenReturn(competitions);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/live"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("dashboard/live"))
                .andReturn();

        Object model = result.getModelAndView().getModelMap().get("model");
        assertTrue(model.getClass().equals(LiveDashboardViewModel.class));

        LiveDashboardViewModel viewModel = (LiveDashboardViewModel) model;
        assertEquals(competitions, viewModel.getCompetitions());
        assertEquals(counts, viewModel.getCounts());
        assertEquals(true, viewModel.isSupportView());
    }

    @Test
    public void testLiveDashBoardSupportViewInnovationLead() throws Exception {

        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(UserRoleType.INNOVATION_LEAD).build())).build());

        Mockito.when(competitionDashboardSearchService.getLiveCompetitions()).thenReturn(competitions);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/dashboard/live"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("dashboard/live"))
                .andReturn();

        Object model = result.getModelAndView().getModelMap().get("model");
        assertTrue(model.getClass().equals(LiveDashboardViewModel.class));

        LiveDashboardViewModel viewModel = (LiveDashboardViewModel) model;
        assertEquals(competitions, viewModel.getCompetitions());
        assertEquals(counts, viewModel.getCounts());
        assertEquals(true, viewModel.isSupportView());
    }

    @Override
    protected CompetitionManagementDashboardController supplyControllerUnderTest() {
        return new CompetitionManagementDashboardController();
    }
}
