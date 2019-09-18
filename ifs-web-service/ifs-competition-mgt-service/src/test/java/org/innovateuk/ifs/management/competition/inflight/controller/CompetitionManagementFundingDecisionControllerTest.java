package org.innovateuk.ifs.management.competition.inflight.controller;

import org.apache.commons.lang3.CharEncoding;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.funding.controller.CompetitionManagementFundingDecisionController;
import org.innovateuk.ifs.management.funding.form.FundingDecisionFilterForm;
import org.innovateuk.ifs.management.funding.form.FundingDecisionSelectionCookie;
import org.innovateuk.ifs.management.funding.form.FundingDecisionSelectionForm;
import org.innovateuk.ifs.management.funding.populator.CompetitionManagementFundingDecisionModelPopulator;
import org.innovateuk.ifs.management.funding.service.ApplicationFundingDecisionService;
import org.innovateuk.ifs.management.funding.viewmodel.ManageFundingApplicationsViewModel;
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
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CompetitionManagementFundingDecisionControllerTest extends BaseControllerMockMVCTest<CompetitionManagementFundingDecisionController> {

    public static final Long COMPETITION_ID = 123L;
    public static final String FILTER_STRING = "an appliction id";

    @InjectMocks
    private CompetitionManagementFundingDecisionController controller;

    @Spy
    @InjectMocks
    private CompetitionManagementFundingDecisionModelPopulator competitionManagementFundingDecisionModelPopulator;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private ApplicationSummaryRestService applicationSummaryRestService;

    @Mock
    private CompressedCookieService cookieUtil;

    @Mock
    private ApplicationFundingDecisionService applicationFundingDecisionService;

    private MockMvc mockMvc;

    private final FundingDecisionSelectionCookie cookieWithFilterAndSelectionParameters = createCookieWithFilterAndSelectionParameters();

    @Override
    protected CompetitionManagementFundingDecisionController supplyControllerUnderTest() {
        return new CompetitionManagementFundingDecisionController();
    }

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        ReflectionTestUtils.setField(controller, "validator", validator);

    }

    private FundingDecisionSelectionCookie createCookieWithFilterAndSelectionParameters() {
        FundingDecisionSelectionCookie cookieWithFilterAndSelectionParameters = new FundingDecisionSelectionCookie();

        FundingDecisionFilterForm filterForm  = new FundingDecisionFilterForm();
        filterForm.setStringFilter(Optional.of(FILTER_STRING));

        FundingDecisionSelectionForm fundingDecisionSelectionForm = new FundingDecisionSelectionForm();
        fundingDecisionSelectionForm.setAllSelected(true);
        fundingDecisionSelectionForm.setApplicationIds(asList(1L, 2L));

        cookieWithFilterAndSelectionParameters.setFundingDecisionSelectionForm(fundingDecisionSelectionForm);
        cookieWithFilterAndSelectionParameters.setFundingDecisionFilterForm(filterForm);

        return cookieWithFilterAndSelectionParameters;
    }

    @Test
    public void testGetApplications() throws Exception {
        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(FUNDERS_PANEL).build();
        CompetitionResource competitionResource = newCompetitionResource().withId(COMPETITION_ID).build();
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(competitionSummaryResource));
        when(applicationSummaryRestService.getAllSubmittedApplicationIds(COMPETITION_ID, empty(), empty())).thenReturn(restSuccess(asList(1L, 2L)));

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

        FundingDecisionSelectionCookie expectedCookie = new FundingDecisionSelectionCookie();
        expectedCookie.setFundingDecisionFilterForm(new FundingDecisionFilterForm());

        verify(cookieUtil).saveToCookie(any(),eq("fundingDecisionSelectionForm_comp_123"), eq(getSerializedObject(expectedCookie)));
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

        FundingDecisionSelectionCookie expectedCookie = new FundingDecisionSelectionCookie();
        FundingDecisionFilterForm expectedFilterFrom  = new FundingDecisionFilterForm();
        expectedFilterFrom.setStringFilter(Optional.of(filterString));

        expectedCookie.setFundingDecisionFilterForm(expectedFilterFrom);

        verify(cookieUtil).saveToCookie(any(),eq("fundingDecisionSelectionForm_comp_123"), eq(getSerializedObject(expectedCookie)));
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

        FundingDecisionFilterForm filterForm = cookieWithFilterAndSelectionParameters.getFundingDecisionFilterForm();
        when(applicationSummaryRestService.getAllSubmittedApplicationIds(COMPETITION_ID, filterForm.getStringFilter(),filterForm.getFundingFilter())).thenReturn(restSuccess(asList(1L, 2L)));

        Map<String, Object> model = mockMvc.perform(get("/competition/{competitionId}/funding", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-funders-panel"))
                .andReturn().getModelAndView().getModel();

        ManageFundingApplicationsViewModel viewModel = (ManageFundingApplicationsViewModel) model.get("model");

        assertEquals(viewModel.getCompetitionSummary(), competitionSummaryResource);
        assertEquals(viewModel.getResults(), summary);

        verify(cookieUtil).saveToCookie(any(),eq("fundingDecisionSelectionForm_comp_123"), eq(getSerializedObject(cookieWithFilterAndSelectionParameters)));
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

        FundingDecisionSelectionCookie expectedFundingDecisionSelectionCookie = cookieWithFilterAndSelectionParameters;
        expectedFundingDecisionSelectionCookie.setFundingDecisionFilterForm(new FundingDecisionFilterForm());

        verify(cookieUtil).saveToCookie(any(),eq("fundingDecisionSelectionForm_comp_123"), eq(getSerializedObject(expectedFundingDecisionSelectionCookie)));
    }

    @Test
    public void applications_validSubmitFundingDecisionShouldResultInServiceCall() throws Exception {
        String fundingDecision = "ON_HOLD";
        List<Long> applicationIds = asList(1L, 2L);

        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(FUNDERS_PANEL).build();
        CompetitionResource competitionResource = newCompetitionResource().withId(COMPETITION_ID).build();
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(competitionSummaryResource));
        when(applicationFundingDecisionService.saveApplicationFundingDecisionData(COMPETITION_ID, FundingDecision.ON_HOLD, applicationIds)).thenReturn(ServiceResult.serviceSuccess());
        when(applicationFundingDecisionService.getFundingDecisionForString(fundingDecision)).thenReturn(Optional.of(FundingDecision.ON_HOLD));
        when(applicationSummaryRestService.getAllSubmittedApplicationIds(COMPETITION_ID, empty(), empty())).thenReturn(restSuccess(applicationIds));
        when(cookieUtil.getCookieValue(any(),any())).thenReturn(getSerializedObject(cookieWithFilterAndSelectionParameters));

        List<ApplicationSummaryResource> expectedSummaries = newApplicationSummaryResource()
                .build(3);
        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50, 3, expectedSummaries, 1, 20);
        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "id", 0, 20, empty(), empty())).thenReturn(restSuccess(summary));

        Map<String, Object> model = mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("fundingDecision", fundingDecision))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-funders-panel"))
                .andReturn().getModelAndView().getModel();

        ManageFundingApplicationsViewModel viewModel = (ManageFundingApplicationsViewModel) model.get("model");

        assertEquals(viewModel.getCompetitionSummary(), competitionSummaryResource);
        assertEquals(viewModel.getResults(), summary);

        verify(cookieUtil).getCookieValue(any(),any());
    }

    @Test
    public void applications_invalidSubmitFundingDecisionShouldNotResultInServiceCall() throws Exception {
        String fundingDecision = "ON_HOLD";
        List<Long> applicationIds = new ArrayList<>();
        applicationIds.add(8L);
        applicationIds.add(9L);
        applicationIds.add(10L);

        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(FUNDERS_PANEL).build();
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(competitionSummaryResource));
        when(applicationFundingDecisionService.saveApplicationFundingDecisionData(COMPETITION_ID, FundingDecision.ON_HOLD, applicationIds)).thenReturn(ServiceResult.serviceSuccess());
        when(applicationFundingDecisionService.getFundingDecisionForString(fundingDecision)).thenReturn(Optional.of(FundingDecision.ON_HOLD));
        when(cookieUtil.getCookieValue(any(),any())).thenReturn(getSerializedObject(cookieWithFilterAndSelectionParameters));

        List<ApplicationSummaryResource> expectedSummaries = newApplicationSummaryResource()
                .build(3);
        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50, 3, expectedSummaries, 1, 20);
        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "id", 0, 20, empty(), empty())).thenReturn(restSuccess(summary));

        mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("fundingDecision", ""))
                .andExpect(status().is4xxClientError());

        verifyNoMoreInteractions(cookieUtil);
        verifyNoMoreInteractions(applicationFundingDecisionService);
    }

    @Test
    public void applications_missingSubmitFundingDecisionShouldNotResultInServiceCall() throws Exception {
        String fundingDecision = "ON_HOLD";
        List<Long> applicationIds = new ArrayList<>();
        applicationIds.add(8L);
        applicationIds.add(9L);
        applicationIds.add(10L);

        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(FUNDERS_PANEL).build();
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(competitionSummaryResource));
        when(applicationFundingDecisionService.saveApplicationFundingDecisionData(COMPETITION_ID, FundingDecision.ON_HOLD, applicationIds)).thenReturn(ServiceResult.serviceSuccess());
        when(applicationFundingDecisionService.getFundingDecisionForString(fundingDecision)).thenReturn(Optional.of(FundingDecision.ON_HOLD));

        List<ApplicationSummaryResource> expectedSummaries = newApplicationSummaryResource()
                .build(3);
        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50, 3, expectedSummaries, 1, 20);
        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "id", 0, 20, empty(), empty())).thenReturn(restSuccess(summary));

        mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is4xxClientError());


        verifyNoMoreInteractions(cookieUtil);
        verifyNoMoreInteractions(applicationFundingDecisionService);
    }

    @Test
    public void applications_unlistedFundingChoiceStringShouldNotResultInServiceCall() throws Exception {
        String fundingDecisionString = "abc";
        List<Long> applicationIds = new ArrayList<>();
        applicationIds.add(8L);
        applicationIds.add(9L);
        applicationIds.add(10L);

        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(FUNDERS_PANEL).build();
        CompetitionResource competitionResource = newCompetitionResource().withId(COMPETITION_ID).build();
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(competitionSummaryResource));
        when(applicationFundingDecisionService.saveApplicationFundingDecisionData(COMPETITION_ID, FundingDecision.ON_HOLD, applicationIds)).thenReturn(ServiceResult.serviceSuccess());
        when(applicationFundingDecisionService.getFundingDecisionForString(fundingDecisionString)).thenReturn(empty());
        when(applicationSummaryRestService.getAllSubmittedApplicationIds(COMPETITION_ID, empty(), empty())).thenReturn(restSuccess(asList(1L, 2L)));

        List<ApplicationSummaryResource> expectedSummaries = newApplicationSummaryResource()
                .build(3);
        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50, 3, expectedSummaries, 1, 20);
        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "id", 0, 20, empty(), empty())).thenReturn(restSuccess(summary));

        mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("fundingDecision", fundingDecisionString))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-funders-panel"));

        verify(applicationFundingDecisionService, times(0)).saveApplicationFundingDecisionData(any(), any(), any());
    }


    @Test
    public void applications_filteredParameterNameShouldNotBeReflectedInPaginationViewModel() throws Exception {
        String fundingDecisionString = "abc";
        List<Long> applicationIds = new ArrayList<>();
        applicationIds.add(8L);
        applicationIds.add(9L);
        applicationIds.add(10L);

        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(FUNDERS_PANEL).build();
        CompetitionResource competitionResource = newCompetitionResource().withId(COMPETITION_ID).build();
        when(competitionRestService.getCompetitionById(COMPETITION_ID)).thenReturn(restSuccess(competitionResource));
        
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(competitionSummaryResource));
        when(applicationFundingDecisionService.saveApplicationFundingDecisionData(COMPETITION_ID, FundingDecision.ON_HOLD, applicationIds)).thenReturn(ServiceResult.serviceSuccess());
        when(applicationFundingDecisionService.getFundingDecisionForString(fundingDecisionString)).thenReturn(empty());
        when(applicationSummaryRestService.getAllSubmittedApplicationIds(COMPETITION_ID, empty(), empty())).thenReturn(restSuccess(asList(1L, 2L)));

        List<ApplicationSummaryResource> expectedSummaries = newApplicationSummaryResource()
                .build(3);
        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50, 3, expectedSummaries, 1, 20);
        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "id", 0, 20, empty(), empty())).thenReturn(restSuccess(summary));

        MvcResult result = mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("fundingDecision", fundingDecisionString)
                .param("_csrf", "hash")
        )
                .andExpect(status().isOk()).andReturn();

        Map<String, Object> model = mockMvc.perform(get("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("fundingDecision", fundingDecisionString)
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
        when(cookieUtil.getCookieValue(any(),any())).thenReturn(getSerializedObject(new FundingDecisionSelectionCookie()));
        when(applicationSummaryRestService.getAllSubmittedApplicationIds(COMPETITION_ID, Optional.empty(), Optional.empty())).thenReturn(restSuccess(asList(1L, 2L)));


        mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("addAll","true")
        )
                .andExpect(status().isOk())
                .andExpect(content().string("{\"selectionCount\":2,\"allSelected\":true,\"limitExceeded\":false}")).andReturn();

        FundingDecisionSelectionCookie expectedCookie = new FundingDecisionSelectionCookie();
        expectedCookie.getFundingDecisionSelectionForm().setApplicationIds(asList(1L, 2L));
        expectedCookie.getFundingDecisionSelectionForm().setAllSelected(true);

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

        FundingDecisionSelectionCookie expectedCookie = cookieWithFilterAndSelectionParameters;
        expectedCookie.getFundingDecisionSelectionForm().setApplicationIds(Collections.EMPTY_LIST);
        expectedCookie.getFundingDecisionSelectionForm().setAllSelected(false);

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
        when(cookieUtil.getCookieValue(any(),any())).thenReturn(getSerializedObject(new FundingDecisionSelectionCookie()));
        when(applicationSummaryRestService.getAllSubmittedApplicationIds(COMPETITION_ID, Optional.empty(), Optional.empty())).thenReturn(restSuccess(asList(1L, 2L)));


        mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("selectionId","1")
                .param("isSelected", "true")
        )
                .andExpect(status().isOk())
                .andExpect(content().string("{\"selectionCount\":1,\"allSelected\":false,\"limitExceeded\":false}")).andReturn();

        FundingDecisionSelectionCookie expectedCookie = new FundingDecisionSelectionCookie();
        expectedCookie.getFundingDecisionSelectionForm().setApplicationIds(singletonList(1L));
        expectedCookie.getFundingDecisionSelectionForm().setAllSelected(false);

        verify(cookieUtil).saveToCookie(any(), any(), eq(getSerializedObject(expectedCookie)));
    }

    @Test
    public void testAddSelectedApplicationsToSelection_requestWithAllSelectionIdsWillAddItToCookie() throws Exception {
        FundingDecisionSelectionCookie fundingDecisionSelectionCookie = new FundingDecisionSelectionCookie();
        fundingDecisionSelectionCookie.getFundingDecisionSelectionForm().setApplicationIds(singletonList(1L));

        when(cookieUtil.getCookieValue(any(),any())).thenReturn(getSerializedObject(fundingDecisionSelectionCookie));
        when(applicationSummaryRestService.getAllSubmittedApplicationIds(COMPETITION_ID, Optional.empty(), Optional.empty())).thenReturn(restSuccess(asList(1L, 2L)));


        mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("selectionId","2")
                .param("isSelected", "true")
        )
                .andExpect(status().isOk())
                .andExpect(content().string("{\"selectionCount\":2,\"allSelected\":true,\"limitExceeded\":false}")).andReturn();

        FundingDecisionSelectionCookie expectedCookie = new FundingDecisionSelectionCookie();
        expectedCookie.getFundingDecisionSelectionForm().setApplicationIds(asList(1L, 2L));
        expectedCookie.getFundingDecisionSelectionForm().setAllSelected(true);

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

        FundingDecisionSelectionCookie expectedCookie = cookieWithFilterAndSelectionParameters;
        expectedCookie.getFundingDecisionSelectionForm().setApplicationIds(singletonList(1L));
        expectedCookie.getFundingDecisionSelectionForm().setAllSelected(false);

        verify(cookieUtil).saveToCookie(any(), any(), eq(getSerializedObject(expectedCookie)));
    }


    private Cookie createFormCookie(FundingDecisionSelectionCookie form) throws Exception {
        String cookieContent = JsonUtil.getSerializedObject(form);
        String encryptedData = encryptor.encrypt(URLEncoder.encode(cookieContent, CharEncoding.UTF_8));
        return new Cookie(format("fundingDecisionSelectionForm_comp%s", COMPETITION_ID), encryptedData);
    }

    private ApplicationSummaryResource app(Long id) {
        ApplicationSummaryResource app = new ApplicationSummaryResource();
        app.setId(id);
        return app;
    }
}
