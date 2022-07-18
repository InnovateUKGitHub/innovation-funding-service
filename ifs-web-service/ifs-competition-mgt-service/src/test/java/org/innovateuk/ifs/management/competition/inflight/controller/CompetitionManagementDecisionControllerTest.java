package org.innovateuk.ifs.management.competition.inflight.controller;

import org.apache.commons.lang3.CharEncoding;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.resource.Decision;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.competition.inflight.populator.CompetitionInFlightStatsModelPopulator;
import org.innovateuk.ifs.management.competition.inflight.viewmodel.CompetitionInFlightStatsViewModel;
import org.innovateuk.ifs.management.decision.controller.CompetitionManagementDecisionController;
import org.innovateuk.ifs.management.decision.form.DecisionFilterForm;
import org.innovateuk.ifs.management.decision.form.DecisionSelectionCookie;
import org.innovateuk.ifs.management.decision.form.DecisionSelectionForm;
import org.innovateuk.ifs.management.decision.populator.CompetitionManagementApplicationDecisionModelPopulator;
import org.innovateuk.ifs.management.decision.service.ApplicationDecisionService;
import org.innovateuk.ifs.management.decision.viewmodel.ManageFundingApplicationsViewModel;
import org.innovateuk.ifs.util.CompressedCookieService;
import org.innovateuk.ifs.util.JsonUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.servlet.http.Cookie;
import java.net.URLEncoder;
import java.util.*;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static org.innovateuk.ifs.util.CookieTestUtil.encryptor;
import static org.innovateuk.ifs.application.builder.ApplicationSummaryResourceBuilder.newApplicationSummaryResource;
import static org.innovateuk.ifs.application.builder.CompetitionSummaryResourceBuilder.newCompetitionSummaryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.FUNDERS_PANEL;
import static org.innovateuk.ifs.util.JsonUtil.getSerializedObject;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CompetitionManagementDecisionControllerTest extends BaseControllerMockMVCTest<CompetitionManagementDecisionController> {

    public static final Long COMPETITION_ID = 123L;
    public static final String FILTER_STRING = "an application id";

    @InjectMocks
    private CompetitionManagementDecisionController controller;

    @Spy
    @InjectMocks
    private CompetitionManagementApplicationDecisionModelPopulator competitionManagementDecisionModelPopulator;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private ApplicationSummaryRestService applicationSummaryRestService;

    @Mock
    private CompressedCookieService cookieUtil;

    @Mock
    private ApplicationDecisionService applicationDecisionService;

    @Mock
    private CompetitionInFlightStatsModelPopulator competitionInFlightStatsModelPopulator;

    private MockMvc mockMvc;

    private final DecisionSelectionCookie cookieWithFilterAndSelectionParameters = createCookieWithFilterAndSelectionParameters();

    @Override
    protected CompetitionManagementDecisionController supplyControllerUnderTest() {
        return new CompetitionManagementDecisionController();
    }

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        ReflectionTestUtils.setField(controller, "validator", validator);

        when(competitionInFlightStatsModelPopulator.populateEoiStatsViewModel(any(CompetitionResource.class)))
                .thenReturn(new CompetitionInFlightStatsViewModel());
    }

    private DecisionSelectionCookie createCookieWithFilterAndSelectionParameters() {
        DecisionSelectionCookie cookieWithFilterAndSelectionParameters = new DecisionSelectionCookie();

        DecisionFilterForm filterForm  = new DecisionFilterForm();
        filterForm.setStringFilter(Optional.of(FILTER_STRING));

        DecisionSelectionForm decisionSelectionForm = new DecisionSelectionForm();
        decisionSelectionForm.setAllSelected(true);
        decisionSelectionForm.setApplicationIds(asList(1L, 2L));

        cookieWithFilterAndSelectionParameters.setDecisionSelectionForm(decisionSelectionForm);
        cookieWithFilterAndSelectionParameters.setDecisionFilterForm(filterForm);

        return cookieWithFilterAndSelectionParameters;
    }

    @Test
    public void testGetApplications() throws Exception {
        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(FUNDERS_PANEL).build();
        CompetitionResource competitionResource = newCompetitionResource().withId(COMPETITION_ID).build();
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(competitionSummaryResource));
        when(applicationSummaryRestService.getAllSubmittedApplicationIds(COMPETITION_ID, empty(), empty())).thenReturn(restSuccess(asList(1L, 2L)));

        when(competitionInFlightStatsModelPopulator.populateEoiStatsViewModel(competitionResource)).thenReturn(new CompetitionInFlightStatsViewModel());

        List<ApplicationSummaryResource> expectedSummaries = newApplicationSummaryResource()
                .build(3);
        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50, 3, expectedSummaries, 1, 20);
        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "id", 0, 20, empty(), empty())).thenReturn(restSuccess(summary));

        Map<String, Object> model = mockMvc.perform(get("/competition/{competitionId}/funding", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-funders-panel"))
                .andReturn().getModelAndView().getModel();

        ManageFundingApplicationsViewModel viewModel = (ManageFundingApplicationsViewModel) model.get("model");

        assertEquals(viewModel.getCompetitionSummary(), competitionSummaryResource);
        assertEquals(viewModel.getResults(), summary);
        assertFalse(viewModel.isExpressionOfInterestEnabled());

        verify(applicationSummaryRestService).getSubmittedApplications(COMPETITION_ID, "id", 0, 20, empty(), empty());
        verify(applicationSummaryRestService).getCompetitionSummary(COMPETITION_ID);
    }

    @Test
    public void testGetApplications_requestWithoutFilterCookieParametersAndWithoutGetFiltersWillCreateACookieWithEmptyFilter() throws Exception {
        CompetitionResource competitionResource = newCompetitionResource().withId(COMPETITION_ID).build();
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));

        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(FUNDERS_PANEL).build();
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(competitionSummaryResource));

        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50, 3, newApplicationSummaryResource().build(2), 1, 20);
        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "id", 0, 20, empty(), empty())).thenReturn(restSuccess(summary));
        when(applicationSummaryRestService.getAllSubmittedApplicationIds(COMPETITION_ID, empty(), empty())).thenReturn(restSuccess(asList(1L, 2L)));

        Map<String, Object> model = mockMvc.perform(get("/competition/{competitionId}/funding", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-funders-panel"))
                .andReturn().getModelAndView().getModel();

        ManageFundingApplicationsViewModel viewModel = (ManageFundingApplicationsViewModel) model.get("model");

        assertEquals(viewModel.getCompetitionSummary(), competitionSummaryResource);
        assertEquals(viewModel.getResults(), summary);

        DecisionSelectionCookie expectedCookie = new DecisionSelectionCookie();
        expectedCookie.setDecisionFilterForm(new DecisionFilterForm());

        verify(cookieUtil).saveToCookie(any(),eq("decisionSelectionForm_comp_123"), eq(getSerializedObject(expectedCookie)));
    }

    @Test
    public void testGetApplications_requestWithoutFilterCookieParametersAndWithGetFiltersWillCreateACookieBasedOnGetParameters() throws Exception {
        String filterString = "an application id";

        CompetitionResource competitionResource = newCompetitionResource().withId(COMPETITION_ID).build();
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));

        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(FUNDERS_PANEL).build();
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(competitionSummaryResource));

        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50, 3, newApplicationSummaryResource().build(2), 1, 20);
        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "id", 0, 20, Optional.of(filterString), empty())).thenReturn(restSuccess(summary));
        when(applicationSummaryRestService.getAllSubmittedApplicationIds(COMPETITION_ID, Optional.of(filterString), empty())).thenReturn(restSuccess(asList(1L, 2L)));

        Map<String, Object> model = mockMvc.perform(get("/competition/{competitionId}/funding", COMPETITION_ID)
                .param("stringFilter", filterString))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-funders-panel"))
                .andReturn().getModelAndView().getModel();

        ManageFundingApplicationsViewModel viewModel = (ManageFundingApplicationsViewModel) model.get("model");

        assertEquals(viewModel.getCompetitionSummary(), competitionSummaryResource);
        assertEquals(viewModel.getResults(), summary);

        DecisionSelectionCookie expectedCookie = new DecisionSelectionCookie();
        DecisionFilterForm expectedFilterFrom  = new DecisionFilterForm();
        expectedFilterFrom.setStringFilter(Optional.of(filterString));

        expectedCookie.setDecisionFilterForm(expectedFilterFrom);

        verify(cookieUtil).saveToCookie(any(),eq("decisionSelectionForm_comp_123"), eq(getSerializedObject(expectedCookie)));
    }

    @Test
    public void testGetApplications_requestWithCookieWithFilterAndSelectionParametersWithoutGetFiltersWillSetFilterParametersToCookieValues() throws Exception {
        CompetitionResource competitionResource = newCompetitionResource().withId(COMPETITION_ID).build();
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));

        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(FUNDERS_PANEL).build();
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(competitionSummaryResource));

        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50, 3, newApplicationSummaryResource().build(2), 1, 20);
        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "id", 0, 20, Optional.of(FILTER_STRING), empty())).thenReturn(restSuccess(summary));
        when(cookieUtil.getCookieValue(any(),any())).thenReturn(getSerializedObject(cookieWithFilterAndSelectionParameters));

        DecisionFilterForm filterForm = cookieWithFilterAndSelectionParameters.getDecisionFilterForm();
        when(applicationSummaryRestService.getAllSubmittedApplicationIds(COMPETITION_ID, filterForm.getStringFilter(),filterForm.getFundingFilter())).thenReturn(restSuccess(asList(1L, 2L)));

        Map<String, Object> model = mockMvc.perform(get("/competition/{competitionId}/funding", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-funders-panel"))
                .andReturn().getModelAndView().getModel();

        ManageFundingApplicationsViewModel viewModel = (ManageFundingApplicationsViewModel) model.get("model");

        assertEquals(viewModel.getCompetitionSummary(), competitionSummaryResource);
        assertEquals(viewModel.getResults(), summary);

        verify(cookieUtil).saveToCookie(any(),eq("decisionSelectionForm_comp_123"), eq(getSerializedObject(cookieWithFilterAndSelectionParameters)));
    }

    @Test
    public void testGetApplications_requestWithCookieWithFilterAndSelectionParametersWithoutGetFiltersAndClearFiltersParameterWillResetFilterParameters() throws Exception {
        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(FUNDERS_PANEL).build();
        CompetitionResource competitionResource = newCompetitionResource().withId(COMPETITION_ID).build();
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(competitionSummaryResource));
        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50, 3, newApplicationSummaryResource().build(2), 1, 20);
        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "id", 0, 20, empty(), empty())).thenReturn(restSuccess(summary));
        when(cookieUtil.getCookieValue(any(),any())).thenReturn(getSerializedObject(cookieWithFilterAndSelectionParameters));
        when(applicationSummaryRestService.getAllSubmittedApplicationIds(COMPETITION_ID, empty(), empty())).thenReturn(restSuccess(asList(1L, 2L)));

        Map<String, Object> model = mockMvc.perform(get("/competition/{competitionId}/funding", COMPETITION_ID)
                .param("filterChanged", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-funders-panel"))
                .andReturn().getModelAndView().getModel();

        ManageFundingApplicationsViewModel viewModel = (ManageFundingApplicationsViewModel) model.get("model");

        assertEquals(viewModel.getCompetitionSummary(), competitionSummaryResource);
        assertEquals(viewModel.getResults(), summary);

        DecisionSelectionCookie expectedDecisionSelectionCookie = cookieWithFilterAndSelectionParameters;
        expectedDecisionSelectionCookie.setDecisionFilterForm(new DecisionFilterForm());

        verify(cookieUtil).saveToCookie(any(),eq("decisionSelectionForm_comp_123"), eq(getSerializedObject(expectedDecisionSelectionCookie)));
    }

    @Test
    public void applications_validSubmitDecisionShouldResultInServiceCall() throws Exception {
        String decision = "ON_HOLD";
        List<Long> applicationIds = asList(1L, 2L);

        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(FUNDERS_PANEL).build();
        CompetitionResource competitionResource = newCompetitionResource().withId(COMPETITION_ID).build();
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(competitionSummaryResource));
        when(applicationDecisionService.saveApplicationDecisionData(COMPETITION_ID, Decision.ON_HOLD, applicationIds)).thenReturn(ServiceResult.serviceSuccess());
        when(applicationDecisionService.getDecisionForString(decision)).thenReturn(Optional.of(Decision.ON_HOLD));
        when(applicationSummaryRestService.getAllSubmittedApplicationIds(COMPETITION_ID, empty(), empty())).thenReturn(restSuccess(applicationIds));
        when(cookieUtil.getCookieValue(any(),any())).thenReturn(getSerializedObject(cookieWithFilterAndSelectionParameters));

        List<ApplicationSummaryResource> expectedSummaries = newApplicationSummaryResource()
                .build(3);
        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50, 3, expectedSummaries, 1, 20);
        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "id", 0, 20, empty(), empty())).thenReturn(restSuccess(summary));

        Map<String, Object> model = mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("decision", decision))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-funders-panel"))
                .andReturn().getModelAndView().getModel();

        ManageFundingApplicationsViewModel viewModel = (ManageFundingApplicationsViewModel) model.get("model");

        assertEquals(viewModel.getCompetitionSummary(), competitionSummaryResource);
        assertEquals(viewModel.getResults(), summary);

        verify(cookieUtil).getCookieValue(any(),any());
    }

    @Test
    public void applications_invalidSubmitDecisionShouldNotResultInServiceCall() throws Exception {
        String decision = "ON_HOLD";
        List<Long> applicationIds = new ArrayList<>();
        applicationIds.add(8L);
        applicationIds.add(9L);
        applicationIds.add(10L);

        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(FUNDERS_PANEL).build();
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(competitionSummaryResource));
        when(applicationDecisionService.saveApplicationDecisionData(COMPETITION_ID, Decision.ON_HOLD, applicationIds)).thenReturn(ServiceResult.serviceSuccess());
        when(applicationDecisionService.getDecisionForString(decision)).thenReturn(Optional.of(Decision.ON_HOLD));
        when(cookieUtil.getCookieValue(any(),any())).thenReturn(getSerializedObject(cookieWithFilterAndSelectionParameters));

        List<ApplicationSummaryResource> expectedSummaries = newApplicationSummaryResource()
                .build(3);
        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50, 3, expectedSummaries, 1, 20);
        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "id", 0, 20, empty(), empty())).thenReturn(restSuccess(summary));

        mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("decision", ""))
                .andExpect(status().is4xxClientError());

        verifyNoMoreInteractions(cookieUtil);
        verifyNoMoreInteractions(applicationDecisionService);
    }

    @Test
    public void applications_missingSubmitDecisionShouldNotResultInServiceCall() throws Exception {
        String decision = "ON_HOLD";
        List<Long> applicationIds = new ArrayList<>();
        applicationIds.add(8L);
        applicationIds.add(9L);
        applicationIds.add(10L);

        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(FUNDERS_PANEL).build();
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(competitionSummaryResource));
        when(applicationDecisionService.saveApplicationDecisionData(COMPETITION_ID, Decision.ON_HOLD, applicationIds)).thenReturn(ServiceResult.serviceSuccess());
        when(applicationDecisionService.getDecisionForString(decision)).thenReturn(Optional.of(Decision.ON_HOLD));

        List<ApplicationSummaryResource> expectedSummaries = newApplicationSummaryResource()
                .build(3);
        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50, 3, expectedSummaries, 1, 20);
        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "id", 0, 20, empty(), empty())).thenReturn(restSuccess(summary));

        mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is4xxClientError());


        verifyNoMoreInteractions(cookieUtil);
        verifyNoMoreInteractions(applicationDecisionService);
    }

    @Test
    public void applications_unlistedFundingChoiceStringShouldNotResultInServiceCall() throws Exception {
        String decisionString = "abc";
        List<Long> applicationIds = new ArrayList<>();
        applicationIds.add(8L);
        applicationIds.add(9L);
        applicationIds.add(10L);

        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(FUNDERS_PANEL).build();
        CompetitionResource competitionResource = newCompetitionResource().withId(COMPETITION_ID).build();
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(competitionSummaryResource));
        when(applicationDecisionService.saveApplicationDecisionData(COMPETITION_ID, Decision.ON_HOLD, applicationIds)).thenReturn(ServiceResult.serviceSuccess());
        when(applicationDecisionService.getDecisionForString(decisionString)).thenReturn(empty());
        when(applicationSummaryRestService.getAllSubmittedApplicationIds(COMPETITION_ID, empty(), empty())).thenReturn(restSuccess(asList(1L, 2L)));

        List<ApplicationSummaryResource> expectedSummaries = newApplicationSummaryResource()
                .build(3);
        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50, 3, expectedSummaries, 1, 20);
        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "id", 0, 20, empty(), empty())).thenReturn(restSuccess(summary));

        mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("decision", decisionString))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-funders-panel"));

        verify(applicationDecisionService, times(0)).saveApplicationDecisionData(any(), any(), any());
    }

    @Test
    public void applications_filteredParameterNameShouldNotBeReflectedInPaginationViewModel() throws Exception {
        String decisionString = "abc";
        List<Long> applicationIds = new ArrayList<>();
        applicationIds.add(8L);
        applicationIds.add(9L);
        applicationIds.add(10L);

        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(FUNDERS_PANEL).build();
        CompetitionResource competitionResource = newCompetitionResource().withId(COMPETITION_ID).build();
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(competitionSummaryResource));
        when(applicationDecisionService.saveApplicationDecisionData(COMPETITION_ID, Decision.ON_HOLD, applicationIds)).thenReturn(ServiceResult.serviceSuccess());
        when(applicationDecisionService.getDecisionForString(decisionString)).thenReturn(empty());
        when(applicationSummaryRestService.getAllSubmittedApplicationIds(COMPETITION_ID, empty(), empty())).thenReturn(restSuccess(asList(1L, 2L)));

        List<ApplicationSummaryResource> expectedSummaries = newApplicationSummaryResource()
                .build(3);
        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50, 3, expectedSummaries, 1, 20);
        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "id", 0, 20, empty(), empty())).thenReturn(restSuccess(summary));

        MvcResult result = mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("decision", decisionString)
                .param("_csrf", "hash")
        )
                .andExpect(status().isOk()).andReturn();

        Map<String, Object> model = mockMvc.perform(get("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("decision", decisionString)
                .param("_csrf", "hash"))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-funders-panel"))
                .andReturn().getModelAndView().getModel();

        ManageFundingApplicationsViewModel viewModel = (ManageFundingApplicationsViewModel) model.get("model");

        assertEquals("?page=0", viewModel.getPagination().getPageNames().get(0).getPath());
    }

    @Test
    public void testAddAllApplicationsToSelection_ifCookieCannotBeParsedShouldReturnFailureResponse() throws Exception {
        when(cookieUtil.getCookieValue(any(),any())).thenThrow(RuntimeException.class);

        mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("addAll","true")
        )
                .andExpect(status().isOk())
                .andExpect(content().string("{\"selectionCount\":-1,\"allSelected\":false,\"limitExceeded\":false}")).andReturn();

    }

    @Test
    public void testAddSelectedApplicationsToSelection_requestWithAddAllAsTrueWillAddAllFilteredApplicationIdsToCookie() throws Exception {
        when(cookieUtil.getCookieValue(any(),any())).thenReturn(getSerializedObject(new DecisionSelectionCookie()));
        when(applicationSummaryRestService.getAllSubmittedApplicationIds(COMPETITION_ID, Optional.empty(), Optional.empty())).thenReturn(restSuccess(asList(1L, 2L)));


        mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("addAll","true")
        )
                .andExpect(status().isOk())
                .andExpect(content().string("{\"selectionCount\":2,\"allSelected\":true,\"limitExceeded\":false}")).andReturn();

        DecisionSelectionCookie expectedCookie = new DecisionSelectionCookie();
        expectedCookie.getDecisionSelectionForm().setApplicationIds(asList(1L, 2L));
        expectedCookie.getDecisionSelectionForm().setAllSelected(true);

        verify(cookieUtil).saveToCookie(any(), any(), eq(getSerializedObject(expectedCookie)));
    }

    @Test
    public void testAddSelectedApplicationsToSelection_requestWithAddAllAsFalseWillRemoveAllApplicationIdsFromCookie() throws Exception {
        when(cookieUtil.getCookieValue(any(),any())).thenReturn(getSerializedObject(cookieWithFilterAndSelectionParameters));

        mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("addAll","false")
        )
                .andExpect(status().isOk())
                .andExpect(content().string("{\"selectionCount\":0,\"allSelected\":false,\"limitExceeded\":false}")).andReturn();

        DecisionSelectionCookie expectedCookie = cookieWithFilterAndSelectionParameters;
        expectedCookie.getDecisionSelectionForm().setApplicationIds(Collections.EMPTY_LIST);
        expectedCookie.getDecisionSelectionForm().setAllSelected(false);

        verify(cookieUtil).saveToCookie(any(), any(), eq(getSerializedObject(expectedCookie)));
    }

    @Test
    public void testAddSelectedApplicationsToSelection_ifCookieCannotBeParsedShouldReturnFailureResponse() throws Exception {
        when(cookieUtil.getCookieValue(any(),any())).thenThrow(RuntimeException.class);

        mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("selectionId","1")
                .param("isSelected", "true")
        )
                        .andExpect(status().isOk())
                .andExpect(content().string("{\"selectionCount\":-1,\"allSelected\":false,\"limitExceeded\":false}")).andReturn();
    }

    @Test
    public void testAddSelectedApplicationsToSelection_requestWithAddSelectionIdWillAddItToCookieAndProvideCorrectResponse() throws Exception {
        when(cookieUtil.getCookieValue(any(),any())).thenReturn(getSerializedObject(new DecisionSelectionCookie()));
        when(applicationSummaryRestService.getAllSubmittedApplicationIds(COMPETITION_ID, Optional.empty(), Optional.empty())).thenReturn(restSuccess(asList(1L, 2L)));


        mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("selectionId","1")
                .param("isSelected", "true")
        )
                .andExpect(status().isOk())
                .andExpect(content().string("{\"selectionCount\":1,\"allSelected\":false,\"limitExceeded\":false}")).andReturn();

        DecisionSelectionCookie expectedCookie = new DecisionSelectionCookie();
        expectedCookie.getDecisionSelectionForm().setApplicationIds(singletonList(1L));
        expectedCookie.getDecisionSelectionForm().setAllSelected(false);

        verify(cookieUtil).saveToCookie(any(), any(), eq(getSerializedObject(expectedCookie)));
    }

    @Test
    public void testAddSelectedApplicationsToSelection_requestWithAllSelectionIdsWillAddItToCookie() throws Exception {
        DecisionSelectionCookie decisionSelectionCookie = new DecisionSelectionCookie();
        decisionSelectionCookie.getDecisionSelectionForm().setApplicationIds(singletonList(1L));

        when(cookieUtil.getCookieValue(any(),any())).thenReturn(getSerializedObject(decisionSelectionCookie));
        when(applicationSummaryRestService.getAllSubmittedApplicationIds(COMPETITION_ID, Optional.empty(), Optional.empty())).thenReturn(restSuccess(asList(1L, 2L)));


        mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("selectionId","2")
                .param("isSelected", "true")
        )
                .andExpect(status().isOk())
                .andExpect(content().string("{\"selectionCount\":2,\"allSelected\":true,\"limitExceeded\":false}")).andReturn();

        DecisionSelectionCookie expectedCookie = new DecisionSelectionCookie();
        expectedCookie.getDecisionSelectionForm().setApplicationIds(asList(1L, 2L));
        expectedCookie.getDecisionSelectionForm().setAllSelected(true);

        verify(cookieUtil).saveToCookie(any(), any(), eq(getSerializedObject(expectedCookie)));
    }

    @Test
    public void testAddSelectedApplicationsToSelection_requestWithRemoveSelectionIdWillRemoveItFromCookie() throws Exception {
        when(cookieUtil.getCookieValue(any(),any())).thenReturn(getSerializedObject(cookieWithFilterAndSelectionParameters));

        mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("selectionId","2")
                .param("isSelected", "false")
        )
                .andExpect(status().isOk())
                .andExpect(content().string("{\"selectionCount\":1,\"allSelected\":false,\"limitExceeded\":false}")).andReturn();

        DecisionSelectionCookie expectedCookie = cookieWithFilterAndSelectionParameters;
        expectedCookie.getDecisionSelectionForm().setApplicationIds(singletonList(1L));
        expectedCookie.getDecisionSelectionForm().setAllSelected(false);

        verify(cookieUtil).saveToCookie(any(), any(), eq(getSerializedObject(expectedCookie)));
    }

    @Test
    public void testGetAssessedApplications() throws Exception {
        ReflectionTestUtils.setField(controller, "alwaysOpenCompetitionEnabled", true);
        ReflectionTestUtils.setField(competitionManagementDecisionModelPopulator, "alwaysOpenCompetitionEnabled", true);

        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(FUNDERS_PANEL).build();
        CompetitionResource competitionResource = newCompetitionResource().withId(COMPETITION_ID).withAlwaysOpen(true).withHasAssessmentStage(true).build();
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(competitionSummaryResource));
        when(applicationSummaryRestService.getAllAssessedApplicationIds(COMPETITION_ID, empty(), empty())).thenReturn(restSuccess(asList(1L, 2L)));

        List<ApplicationSummaryResource> expectedSummaries = newApplicationSummaryResource()
                .build(3);
        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50, 3, expectedSummaries, 1, 20);
        when(applicationSummaryRestService.getAssessedApplications(COMPETITION_ID, "id", 0, 20, empty(), empty())).thenReturn(restSuccess(summary));

        Map<String, Object> model = mockMvc.perform(get("/competition/{competitionId}/funding", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-funders-panel"))
                .andReturn().getModelAndView().getModel();

        ManageFundingApplicationsViewModel viewModel = (ManageFundingApplicationsViewModel) model.get("model");

        assertEquals(viewModel.getCompetitionSummary(), competitionSummaryResource);
        assertEquals(viewModel.getResults(), summary);

        verify(applicationSummaryRestService).getAssessedApplications(COMPETITION_ID, "id", 0, 20, empty(), empty());
        verify(applicationSummaryRestService).getCompetitionSummary(COMPETITION_ID);
    }

    private Cookie createFormCookie(DecisionSelectionCookie form) throws Exception {
        String cookieContent = JsonUtil.getSerializedObject(form);
        String encryptedData = encryptor.encrypt(URLEncoder.encode(cookieContent, CharEncoding.UTF_8));
        return new Cookie(format("decisionSelectionForm_comp%s", COMPETITION_ID), encryptedData);
    }

    private ApplicationSummaryResource app(Long id) {
        ApplicationSummaryResource app = new ApplicationSummaryResource();
        app.setId(id);
        return app;
    }
}
