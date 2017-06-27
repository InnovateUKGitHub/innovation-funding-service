package org.innovateuk.ifs.management.controller;

import org.apache.commons.lang3.CharEncoding;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.form.FundingDecisionFilterForm;
import org.innovateuk.ifs.competition.form.FundingDecisionSelectionCookie;
import org.innovateuk.ifs.competition.form.FundingDecisionSelectionForm;
import org.innovateuk.ifs.competition.service.ApplicationSummarySortFieldService;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.innovateuk.ifs.util.JsonUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.servlet.http.Cookie;
import java.net.URLEncoder;
import java.util.*;

import static java.lang.String.format;
import static java.util.Optional.empty;
import static org.innovateuk.ifs.application.builder.ApplicationSummaryResourceBuilder.newApplicationSummaryResource;
import static org.innovateuk.ifs.application.builder.CompetitionSummaryResourceBuilder.newCompetitionSummaryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.FUNDERS_PANEL;
import static org.innovateuk.ifs.util.JsonUtil.getSerializedObject;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class CompetitionManagementFundingDecisionControllerTest extends BaseControllerMockMVCTest<CompetitionManagementFundingDecisionController> {

    public static final Long COMPETITION_ID = 123L;
    public static final String FILTER_STRING = "an appliction id";

    @InjectMocks
    private CompetitionManagementFundingDecisionController controller;

    @Mock
    private ApplicationSummarySortFieldService applicationSummarySortFieldService;

    private MockMvc mockMvc;

    private final FundingDecisionSelectionCookie cookieWithFilterAndSelectionParameters = createCookieWithFilterAndSelectionParameters();

    @Override
    protected CompetitionManagementFundingDecisionController supplyControllerUnderTest() {
        return new CompetitionManagementFundingDecisionController();
    }

    @Before
    public void setupMockMvc() {
        super.setUp();

        String password = "mysecretpassword";
        String salt = "109240124012412412";
        encryptor = Encryptors.text(password, salt);

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
        fundingDecisionSelectionForm.setApplicationIds(Arrays.asList(1L, 2L));

        cookieWithFilterAndSelectionParameters.setFundingDecisionSelectionForm(fundingDecisionSelectionForm);
        cookieWithFilterAndSelectionParameters.setFundingDecisionFilterForm(filterForm);

        return cookieWithFilterAndSelectionParameters;
    }

    @Test
    public void testGetApplications() throws Exception {
        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(FUNDERS_PANEL).build();
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(competitionSummaryResource));

        when(applicationSummarySortFieldService.sortFieldForSubmittedApplications(null)).thenReturn("id");

        List<ApplicationSummaryResource> expectedSummaries = newApplicationSummaryResource()
                .build(3);
        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50, 3, expectedSummaries, 1, 20);
        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "id", 0, 20, empty(), empty())).thenReturn(restSuccess(summary));

        mockMvc.perform(get("/competition/{competitionId}/funding", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-funders-panel"))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource))
                .andExpect(model().attribute("results", summary));

        verify(applicationSummaryRestService).getSubmittedApplications(COMPETITION_ID, "id", 0, 20, empty(), empty());
        verify(applicationSummaryRestService).getCompetitionSummary(COMPETITION_ID);
    }

    @Test
    public void testGetApplications_requestWithoutFilterCookieParametersAndWithoutGetFiltersWillCreateACookieWithEmptyFilter() throws Exception {
        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(FUNDERS_PANEL).build();
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(competitionSummaryResource));

        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50, 3, newApplicationSummaryResource().build(2), 1, 20);
        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "id", 0, 20, empty(), empty())).thenReturn(restSuccess(summary));

        mockMvc.perform(get("/competition/{competitionId}/funding", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-funders-panel"))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource))
                .andExpect(model().attribute("results", summary));


        FundingDecisionSelectionCookie expectedCookie = new FundingDecisionSelectionCookie();
        expectedCookie.setFundingDecisionFilterForm(new FundingDecisionFilterForm());

        verify(cookieUtil).saveToCompressedCookie(any(),eq("fundingDecisionSelectionForm_comp123"), eq(getSerializedObject(expectedCookie)));
    }

    @Test
    public void testGetApplications_requestWithoutFilterCookieParametersAndWithGetFiltersWillCreateACookieBasedOnGetParameters() throws Exception {
        String filterString = "an application id";

        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(FUNDERS_PANEL).build();
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(competitionSummaryResource));

        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50, 3, newApplicationSummaryResource().build(2), 1, 20);
        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "id", 0, 20, Optional.of(filterString), empty())).thenReturn(restSuccess(summary));

        mockMvc.perform(get("/competition/{competitionId}/funding", COMPETITION_ID)
                .param("stringFilter", filterString))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-funders-panel"))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource))
                .andExpect(model().attribute("results", summary));

        FundingDecisionSelectionCookie expectedCookie = new FundingDecisionSelectionCookie();
        FundingDecisionFilterForm expectedFilterFrom  = new FundingDecisionFilterForm();
        expectedFilterFrom.setStringFilter(Optional.of(filterString));

        expectedCookie.setFundingDecisionFilterForm(expectedFilterFrom);

        verify(cookieUtil).saveToCompressedCookie(any(),eq("fundingDecisionSelectionForm_comp123"), eq(getSerializedObject(expectedCookie)));
    }

    @Test
    public void testGetApplications_requestWithCookieWithFilterAndSelectionParametersWithoutGetFiltersWillSetFilterParametersToCookieValues() throws Exception {
        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(FUNDERS_PANEL).build();
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(competitionSummaryResource));

        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50, 3, newApplicationSummaryResource().build(2), 1, 20);
        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "id", 0, 20, Optional.of(FILTER_STRING), empty())).thenReturn(restSuccess(summary));

        when(cookieUtil.getCompressedCookieValue(any(),any())).thenReturn(getSerializedObject(cookieWithFilterAndSelectionParameters));

        FundingDecisionFilterForm filterForm = cookieWithFilterAndSelectionParameters.getFundingDecisionFilterForm();


        List<ApplicationSummaryResource> applicationSummaryResources = newApplicationSummaryResource().withId(1L,2L).build(2);
        when(applicationSummaryRestService.getAllSubmittedApplications(COMPETITION_ID, filterForm.getStringFilter(),filterForm.getFundingFilter())).thenReturn(restSuccess(applicationSummaryResources));

        mockMvc.perform(get("/competition/{competitionId}/funding", COMPETITION_ID)
        .cookie(createFormCookie(cookieWithFilterAndSelectionParameters)))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-funders-panel"))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource))
                .andExpect(model().attribute("results", summary));

        verify(cookieUtil).saveToCompressedCookie(any(),eq("fundingDecisionSelectionForm_comp123"), eq(getSerializedObject(cookieWithFilterAndSelectionParameters)));
    }

    @Test
    public void testGetApplications_requestWithCookieWithFilterAndSelectionParametersWithoutGetFiltersAndClearFiltersParameterWillResetFilterParameters() throws Exception {
        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(FUNDERS_PANEL).build();
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(competitionSummaryResource));

        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50, 3, newApplicationSummaryResource().build(2), 1, 20);
        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "id", 0, 20, empty(), empty())).thenReturn(restSuccess(summary));

        when(cookieUtil.getCompressedCookieValue(any(),any())).thenReturn(getSerializedObject(cookieWithFilterAndSelectionParameters));

        FundingDecisionFilterForm filterForm = cookieWithFilterAndSelectionParameters.getFundingDecisionFilterForm();

        List<ApplicationSummaryResource> applicationSummaryResources = newApplicationSummaryResource().withId(1L,2L).build(2);
        when(applicationSummaryRestService.getAllSubmittedApplications(COMPETITION_ID, filterForm.getStringFilter(),filterForm.getFundingFilter())).thenReturn(restSuccess(applicationSummaryResources));

        mockMvc.perform(get("/competition/{competitionId}/funding", COMPETITION_ID)
                .param("clearFilters", "true")
                .cookie(createFormCookie(cookieWithFilterAndSelectionParameters)))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-funders-panel"))
                .andExpect(model().attribute("competitionSummary", competitionSummaryResource))
                .andExpect(model().attribute("results", summary));

        FundingDecisionSelectionCookie expectedFundingDecisionSelectionCookie = cookieWithFilterAndSelectionParameters;
        expectedFundingDecisionSelectionCookie.setFundingDecisionFilterForm(new FundingDecisionFilterForm());

        verify(cookieUtil).saveToCompressedCookie(any(),eq("fundingDecisionSelectionForm_comp123"), eq(getSerializedObject(expectedFundingDecisionSelectionCookie)));
    }


    @Test
    public void applications_validSubmitFundingDecisionShouldResultInServiceCall() throws Exception {
        String fundingDecision = "ON_HOLD";
        List<Long> applicationIds = new ArrayList<>();
        applicationIds.add(1L);
        applicationIds.add(2L);

        CompetitionSummaryResource competitionSummaryResource = newCompetitionSummaryResource().withId(COMPETITION_ID).withCompetitionStatus(FUNDERS_PANEL).build();
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(competitionSummaryResource));
        when(applicationSummarySortFieldService.sortFieldForSubmittedApplications(null)).thenReturn("id");
        when(applicationFundingDecisionService.saveApplicationFundingDecisionData(COMPETITION_ID, FundingDecision.ON_HOLD, applicationIds)).thenReturn(ServiceResult.serviceSuccess());
        when(applicationFundingDecisionService.getFundingDecisionForString(fundingDecision)).thenReturn(Optional.of(FundingDecision.ON_HOLD));

        when(cookieUtil.getCompressedCookieValue(any(),any())).thenReturn(getSerializedObject(cookieWithFilterAndSelectionParameters));


        List<ApplicationSummaryResource> expectedSummaries = newApplicationSummaryResource()
                .build(3);
        ApplicationSummaryPageResource summary = new ApplicationSummaryPageResource(50, 3, expectedSummaries, 1, 20);
        when(applicationSummaryRestService.getSubmittedApplications(COMPETITION_ID, "id", 0, 20, empty(), empty())).thenReturn(restSuccess(summary));

        mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("fundingDecision", fundingDecision))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-funders-panel"));

        verify(cookieUtil).getCompressedCookieValue(any(),any());
        verify(applicationFundingDecisionService).saveApplicationFundingDecisionData(COMPETITION_ID, FundingDecision.ON_HOLD, applicationIds);
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
        when(applicationSummarySortFieldService.sortFieldForSubmittedApplications(null)).thenReturn("id");
        when(applicationFundingDecisionService.saveApplicationFundingDecisionData(COMPETITION_ID, FundingDecision.ON_HOLD, applicationIds)).thenReturn(ServiceResult.serviceSuccess());
        when(applicationFundingDecisionService.getFundingDecisionForString(fundingDecision)).thenReturn(Optional.of(FundingDecision.ON_HOLD));

        when(cookieUtil.getCompressedCookieValue(any(),any())).thenReturn(getSerializedObject(cookieWithFilterAndSelectionParameters));


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
        when(applicationSummarySortFieldService.sortFieldForSubmittedApplications(null)).thenReturn("id");
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
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(competitionSummaryResource));
        when(applicationSummarySortFieldService.sortFieldForSubmittedApplications(null)).thenReturn("id");
        when(applicationFundingDecisionService.saveApplicationFundingDecisionData(COMPETITION_ID, FundingDecision.ON_HOLD, applicationIds)).thenReturn(ServiceResult.serviceSuccess());
        when(applicationFundingDecisionService.getFundingDecisionForString(fundingDecisionString)).thenReturn(empty());


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
        when(applicationSummaryRestService.getCompetitionSummary(COMPETITION_ID)).thenReturn(restSuccess(competitionSummaryResource));
        when(applicationSummarySortFieldService.sortFieldForSubmittedApplications(null)).thenReturn("id");
        when(applicationFundingDecisionService.saveApplicationFundingDecisionData(COMPETITION_ID, FundingDecision.ON_HOLD, applicationIds)).thenReturn(ServiceResult.serviceSuccess());
        when(applicationFundingDecisionService.getFundingDecisionForString(fundingDecisionString)).thenReturn(empty());

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

        PaginationViewModel paginationViewModel = (PaginationViewModel) result.getModelAndView().getModel().get("pagination");

        assertEquals("?origin=FUNDING_APPLICATIONS&page=0",paginationViewModel.getPageNames().get(0).getPath());
    }

    @Test
    public void testAddAllApplicationsToSelection_ifCookieCannotBeParsedShouldReturnFailureResponse() throws Exception {
        when(cookieUtil.getCompressedCookieValue(any(),any())).thenThrow(Exception.class);

        mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("addAll","true")
        )
                .andExpect(status().isOk())
                .andExpect(content().string("{\"selectionCount\":-1,\"allSelected\":false}")).andReturn();

    }

    @Test
    public void testAddSelectedApplicationsToSelection_requestWithAddAllAsTrueWillAddAllFilteredApplicationIdsToCookie() throws Exception {
        List<ApplicationSummaryResource> applicationSummaryResources = newApplicationSummaryResource().withId(1L,2L).build(2);

        when(cookieUtil.getCompressedCookieValue(any(),any())).thenReturn(getSerializedObject(new FundingDecisionSelectionCookie()));
        when(applicationSummaryRestService.getAllSubmittedApplications(COMPETITION_ID, Optional.empty(), Optional.empty())).thenReturn(restSuccess(applicationSummaryResources));


        mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("addAll","true")
        )
                .andExpect(status().isOk())
                .andExpect(content().string("{\"selectionCount\":2,\"allSelected\":true}")).andReturn();

        FundingDecisionSelectionCookie expectedCookie = new FundingDecisionSelectionCookie();
        expectedCookie.getFundingDecisionSelectionForm().setApplicationIds(Arrays.asList(1L, 2L));
        expectedCookie.getFundingDecisionSelectionForm().setAllSelected(true);

        verify(cookieUtil).saveToCompressedCookie(any(), any(), eq(getSerializedObject(expectedCookie)));
    }

    @Test
    public void testAddSelectedApplicationsToSelection_requestWithAddAllAsFalseWillRemoveAllApplicationIdsFromCookie() throws Exception {
        when(cookieUtil.getCompressedCookieValue(any(),any())).thenReturn(getSerializedObject(cookieWithFilterAndSelectionParameters));

        mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("addAll","false")
        )
                .andExpect(status().isOk())
                .andExpect(content().string("{\"selectionCount\":0,\"allSelected\":false}")).andReturn();

        FundingDecisionSelectionCookie expectedCookie = cookieWithFilterAndSelectionParameters;
        expectedCookie.getFundingDecisionSelectionForm().setApplicationIds(Collections.EMPTY_LIST);
        expectedCookie.getFundingDecisionSelectionForm().setAllSelected(false);

        verify(cookieUtil).saveToCompressedCookie(any(), any(), eq(getSerializedObject(expectedCookie)));
    }

    @Test
    public void testAddSelectedApplicationsToSelection_ifCookieCannotBeParsedShouldReturnFailureResponse() throws Exception {
        when(cookieUtil.getCompressedCookieValue(any(),any())).thenThrow(Exception.class);

        mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("selectionId","1")
                .param("isSelected", "true")
        )
                        .andExpect(status().isOk())
                .andExpect(content().string("{\"selectionCount\":-1,\"allSelected\":false}")).andReturn();
    }

    @Test
    public void testAddSelectedApplicationsToSelection_requestWithAddSelectionIdWillAddItToCookieAndProvideCorrectResponse() throws Exception {
        List<ApplicationSummaryResource> applicationSummaryResources = newApplicationSummaryResource().withId(1L,2L).build(2);

        when(cookieUtil.getCompressedCookieValue(any(),any())).thenReturn(getSerializedObject(new FundingDecisionSelectionCookie()));
        when(applicationSummaryRestService.getAllSubmittedApplications(COMPETITION_ID, Optional.empty(), Optional.empty())).thenReturn(restSuccess(applicationSummaryResources));


        mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("selectionId","1")
                .param("isSelected", "true")
        )
                .andExpect(status().isOk())
                .andExpect(content().string("{\"selectionCount\":1,\"allSelected\":false}")).andReturn();

        FundingDecisionSelectionCookie expectedCookie = new FundingDecisionSelectionCookie();
        expectedCookie.getFundingDecisionSelectionForm().setApplicationIds(Arrays.asList(1L));
        expectedCookie.getFundingDecisionSelectionForm().setAllSelected(false);

        verify(cookieUtil).saveToCompressedCookie(any(), any(), eq(getSerializedObject(expectedCookie)));
    }

    @Test
    public void testAddSelectedApplicationsToSelection_requestWithAllSelectionIdsWillAddItToCookie() throws Exception {
        List<ApplicationSummaryResource> applicationSummaryResources = newApplicationSummaryResource().withId(1L,2L).build(2);

        FundingDecisionSelectionCookie fundingDecisionSelectionCookie = new FundingDecisionSelectionCookie();
        fundingDecisionSelectionCookie.getFundingDecisionSelectionForm().setApplicationIds(Arrays.asList(1L));

        when(cookieUtil.getCompressedCookieValue(any(),any())).thenReturn(getSerializedObject(fundingDecisionSelectionCookie));
        when(applicationSummaryRestService.getAllSubmittedApplications(COMPETITION_ID, Optional.empty(), Optional.empty())).thenReturn(restSuccess(applicationSummaryResources));


        mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("selectionId","2")
                .param("isSelected", "true")
        )
                .andExpect(status().isOk())
                .andExpect(content().string("{\"selectionCount\":2,\"allSelected\":true}")).andReturn();

        FundingDecisionSelectionCookie expectedCookie = new FundingDecisionSelectionCookie();
        expectedCookie.getFundingDecisionSelectionForm().setApplicationIds(Arrays.asList(1L, 2L));
        expectedCookie.getFundingDecisionSelectionForm().setAllSelected(true);

        verify(cookieUtil).saveToCompressedCookie(any(), any(), eq(getSerializedObject(expectedCookie)));
    }

    @Test
    public void testAddSelectedApplicationsToSelection_requestWithRemoveSelectionIdWillRemoveItFromCookie() throws Exception {
        when(cookieUtil.getCompressedCookieValue(any(),any())).thenReturn(getSerializedObject(cookieWithFilterAndSelectionParameters));

        mockMvc.perform(post("/competition/{competitionId}/funding", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("selectionId","2")
                .param("isSelected", "false")
        )
                .andExpect(status().isOk())
                .andExpect(content().string("{\"selectionCount\":1,\"allSelected\":false}")).andReturn();

        FundingDecisionSelectionCookie expectedCookie = cookieWithFilterAndSelectionParameters;
        expectedCookie.getFundingDecisionSelectionForm().setApplicationIds(Arrays.asList(1L));
        expectedCookie.getFundingDecisionSelectionForm().setAllSelected(false);

        verify(cookieUtil).saveToCompressedCookie(any(), any(), eq(getSerializedObject(expectedCookie)));
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
