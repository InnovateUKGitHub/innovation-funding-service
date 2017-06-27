package org.innovateuk.ifs.management.controller;

import org.apache.commons.lang3.CharEncoding;
import org.hamcrest.Matcher;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.LambdaMatcher;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.competition.form.FundingNotificationSelectionCookie;
import org.innovateuk.ifs.competition.form.ManageFundingApplicationsQueryForm;
import org.innovateuk.ifs.competition.form.SelectApplicationsForEmailForm;
import org.innovateuk.ifs.competition.resource.CompetitionFundedKeyStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.model.CompetitionInFlightModelPopulator;
import org.innovateuk.ifs.management.model.CompetitionInFlightStatsModelPopulator;
import org.innovateuk.ifs.management.model.ManageFundingApplicationsModelPopulator;
import org.innovateuk.ifs.management.model.SendNotificationsModelPopulator;
import org.innovateuk.ifs.management.viewmodel.CompetitionInFlightStatsViewModel;
import org.innovateuk.ifs.management.viewmodel.ManageFundingApplicationViewModel;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.innovateuk.ifs.management.viewmodel.SendNotificationsViewModel;
import org.innovateuk.ifs.util.JsonUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;
import java.net.URLDecoder;
import java.util.List;
import java.util.Optional;

import static com.google.common.primitives.Longs.asList;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static junit.framework.TestCase.assertFalse;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.hamcrest.CoreMatchers.is;
import static org.innovateuk.ifs.application.builder.ApplicationSummaryResourceBuilder.newApplicationSummaryResource;
import static org.innovateuk.ifs.application.resource.FundingDecision.FUNDED;
import static org.innovateuk.ifs.application.resource.FundingDecision.UNFUNDED;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionFundedKeyStatisticsResourceBuilder.newCompetitionFundedKeyStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.ASSESSOR_FEEDBACK;
import static org.innovateuk.ifs.util.CompressionUtil.getCompressedString;
import static org.innovateuk.ifs.util.CompressionUtil.getDecompressedString;
import static org.innovateuk.ifs.util.JsonUtil.getObjectFromJson;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class CompetitionManagementFundingNotificationsControllerTest extends BaseControllerMockMVCTest<CompetitionManagementFundingNotificationsController> {

    @InjectMocks
    @Spy
    private ManageFundingApplicationsModelPopulator manageFundingApplicationsModelPopulator;

    @InjectMocks
    @Spy
    private CompetitionInFlightStatsModelPopulator competitionInFlightStatsModelPopulator;

    @InjectMocks
    @Spy
    private CompetitionInFlightModelPopulator competitionInFlightModelPopulator;

    @Mock
    private SendNotificationsModelPopulator sendNotificationsModelPopulator;

    public static final Long COMPETITION_ID = 22L;
    public static final Long APPLICATION_ID_ONE = 1L;
    public static final Long APPLICATION_ID_TWO = 2L;


    @Override
    @Before
    public void setUp() {
        super.setUp();
        this.setupCookieUtil();
    }

    @Test
    public void testApplications() throws Exception {
        int pageNumber = 0;
        int pageSize = 20;
        String sortField = "id";
        int totalPages = pageNumber + 2;
        int totalElements = totalPages * (pageNumber + 1);
        String filter = "";
        Optional<Boolean> sendFilter = Optional.empty();
        Optional<FundingDecision> fundingFilter = Optional.empty();
        long changesSinceLastNotify = 10;
        String queryParams = "";
        // Mock setup
        CompetitionResource competitionResource = newCompetitionResource().withId(COMPETITION_ID).withCompetitionStatus(ASSESSOR_FEEDBACK).withName("A competition").build();
        when(competitionService.getById(COMPETITION_ID)).thenReturn(competitionResource);

        List<ApplicationSummaryResource> applications = newApplicationSummaryResource().with(uniqueIds()).build(pageSize);
        ApplicationSummaryPageResource applicationSummaryPageResource = new ApplicationSummaryPageResource(totalElements, totalPages, applications, pageNumber, pageSize);
        when(applicationSummaryRestService.getWithFundingDecisionApplications(COMPETITION_ID, sortField, pageNumber, pageSize, Optional.of(filter), sendFilter, fundingFilter)).thenReturn(restSuccess(applicationSummaryPageResource));

        CompetitionFundedKeyStatisticsResource keyStatistics = newCompetitionFundedKeyStatisticsResource().build();
        when(competitionKeyStatisticsRestServiceMock.getFundedKeyStatisticsByCompetition(COMPETITION_ID)).thenReturn(restSuccess(keyStatistics));
        when(assessmentRestService.countByStateAndCompetition(AssessmentStates.CREATED, COMPETITION_ID)).thenReturn(restSuccess(changesSinceLastNotify));

        // Expected values to match against
        CompetitionInFlightStatsViewModel keyStatisticsModel = competitionInFlightStatsModelPopulator.populateStatsViewModel(competitionResource);
        ManageFundingApplicationViewModel model = new ManageFundingApplicationViewModel(applicationSummaryPageResource, keyStatisticsModel, new PaginationViewModel(applicationSummaryPageResource, queryParams), sortField, COMPETITION_ID, competitionResource.getName());

        when(applicationSummaryRestService.getWithFundingDecisionApplications(
                COMPETITION_ID, empty(), sendFilter, fundingFilter)).thenReturn(restSuccess(applications));

        // Method under test
        mockMvc.perform(get("/competition/{competitionId}/manage-funding-applications", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-manage-funding-applications"))
                .andExpect(model().attribute("model", manageFundingApplicationViewModelMatcher(model)));

    }

    @Test
    public void testSelectApplications() throws Exception {
        long competitionId = 1L;
        mockMvc.perform(post("/competition/{competitionId}/manage-funding-applications", competitionId).
                contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("ids[0]", "18")
                .param("ids[1]", "21"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/competition/1/funding/send?application_ids=18,21"));
    }

    private Matcher<ManageFundingApplicationViewModel> manageFundingApplicationViewModelMatcher(ManageFundingApplicationViewModel toMatch) {
        return new LambdaMatcher<>(match -> {
            assertEquals(toMatch.getCompetitionName(), match.getCompetitionName());
            assertEquals(toMatch.getSortField(), match.getSortField());
            assertEquals(toMatch.getCompetitionId(), match.getCompetitionId());
            assertEquals(toMatch.getResults().getSize(), match.getResults().getSize());
            assertEquals(toMatch.getResults().getContent().get(0).getId(), match.getResults().getContent().get(0).getId());
            CompetitionInFlightStatsViewModel toMatchCompetitionInFlightViewModel = toMatch.getKeyStatistics();
            CompetitionInFlightStatsViewModel matchCompetitionInFlightViewModel = match.getKeyStatistics();
            assertEquals(toMatchCompetitionInFlightViewModel.getStatOne(), matchCompetitionInFlightViewModel.getStatOne());
            assertEquals(toMatchCompetitionInFlightViewModel.getStatTwo(), matchCompetitionInFlightViewModel.getStatTwo());
            assertEquals(toMatchCompetitionInFlightViewModel.getStatThree(), matchCompetitionInFlightViewModel.getStatThree());
            assertEquals(toMatchCompetitionInFlightViewModel.getStatFour(), matchCompetitionInFlightViewModel.getStatFour());
            assertEquals(toMatchCompetitionInFlightViewModel.getStatFive(), matchCompetitionInFlightViewModel.getStatFive());
            assertEquals(toMatchCompetitionInFlightViewModel.isCanManageFundingNotifications(), matchCompetitionInFlightViewModel.isCanManageFundingNotifications());
            return true;
        });
    }

    @Test
    public void addApplicationSelectionToCookie() throws Exception {
        long applicationId = 1L;
        Optional<Boolean> sendFilter = Optional.empty();
        Optional<FundingDecision> fundingFilter = Optional.empty();
        FundingNotificationSelectionCookie selectionCookie = new FundingNotificationSelectionCookie();
        selectionCookie.setSelectApplicationsForEmailForm(new SelectApplicationsForEmailForm());
        selectionCookie.setManageFundingApplicationsQueryForm(new ManageFundingApplicationsQueryForm());
        Cookie formCookie = createFormCookie(selectionCookie);

        List<ApplicationSummaryResource> applications = newApplicationSummaryResource().with(uniqueIds()).build(4);

        when(applicationSummaryRestService.getWithFundingDecisionApplications(
                COMPETITION_ID, empty(), sendFilter, fundingFilter)).thenReturn(restSuccess(applications));


        MvcResult result = mockMvc.perform(post("/competition/{competitionId}/manage-funding-applications", COMPETITION_ID)
                .param("selectionId", valueOf(applicationId))
                .param("isSelected", "true")
                .cookie(formCookie))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("selectionCount", is(1)))
                .andExpect(jsonPath("allSelected", is(false)))
                .andReturn();

        Optional<FundingNotificationSelectionCookie> resultForm = getAssessorSelectionFormFromCookie(result.getResponse(), format("applicationSelectionForm_comp_%s", COMPETITION_ID));
        assertTrue(resultForm.get().getSelectApplicationsForEmailForm().getIds().contains(applicationId));
    }

    @Test
    public void removeApplicationSelectionFromCookie() throws Exception {
        long applicationId = 1L;
        Optional<Boolean> sendFilter = Optional.empty();
        Optional<FundingDecision> fundingFilter = Optional.empty();
        FundingNotificationSelectionCookie selectionCookie = new FundingNotificationSelectionCookie();
        SelectApplicationsForEmailForm selectApplicationsForEmailForm = new SelectApplicationsForEmailForm();
        selectApplicationsForEmailForm.getIds().add(applicationId);
        selectionCookie.setSelectApplicationsForEmailForm(selectApplicationsForEmailForm);
        selectionCookie.setManageFundingApplicationsQueryForm(new ManageFundingApplicationsQueryForm());
        Cookie formCookie = createFormCookie(selectionCookie);

        List<ApplicationSummaryResource> applications = newApplicationSummaryResource()
                .withId(1L, 2L)
                .build(2);

        when(applicationSummaryRestService.getWithFundingDecisionApplications(
                COMPETITION_ID, empty(), sendFilter, fundingFilter)).thenReturn(restSuccess(applications));


        MvcResult result = mockMvc.perform(post("/competition/{competitionId}/manage-funding-applications", COMPETITION_ID)
                .param("selectionId", valueOf(applicationId))
                .param("isSelected", "false")
                .cookie(formCookie))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("selectionCount", is(0)))
                .andExpect(jsonPath("allSelected", is(false)))
                .andReturn();

        Optional<FundingNotificationSelectionCookie> resultForm = getAssessorSelectionFormFromCookie(result.getResponse(), format("applicationSelectionForm_comp_%s", COMPETITION_ID));
        assertFalse(resultForm.get().getSelectApplicationsForEmailForm().getIds().contains(applicationId));
    }

    @Test
    public void addAllApplicationSelectionsToCookie() throws Exception {
        Optional<Boolean> sendFilter = Optional.empty();
        Optional<FundingDecision> fundingFilter = Optional.empty();
        FundingNotificationSelectionCookie selectionCookie = new FundingNotificationSelectionCookie();
        selectionCookie.setSelectApplicationsForEmailForm(new SelectApplicationsForEmailForm());
        selectionCookie.setManageFundingApplicationsQueryForm(new ManageFundingApplicationsQueryForm());
        Cookie formCookie = createFormCookie(selectionCookie);

        List<ApplicationSummaryResource> applications = newApplicationSummaryResource().with(uniqueIds()).build(2);

        when(applicationSummaryRestService.getWithFundingDecisionApplications(
                COMPETITION_ID, empty(), sendFilter, fundingFilter)).thenReturn(restSuccess(applications));

        MvcResult result = mockMvc.perform(post("/competition/{competitionId}/manage-funding-applications", COMPETITION_ID)
                .param("addAll", "true")
                .cookie(formCookie))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("selectionCount", is(2)))
                .andExpect(jsonPath("allSelected", is(true)))
                .andReturn();

        Optional<FundingNotificationSelectionCookie> resultForm = getAssessorSelectionFormFromCookie(result.getResponse(), format("applicationSelectionForm_comp_%s", COMPETITION_ID));
        assertTrue(resultForm.get().getSelectApplicationsForEmailForm().getIds().containsAll(asList(applications.get(0).getId(), applications.get(1).getId())));
    }

    @Test
    public void getSendNotificationsPageTest() throws Exception {

        List<Long> applicationsIds = singletonList(APPLICATION_ID_ONE);
        List<ApplicationSummaryResource> resourceList = singletonList(new ApplicationSummaryResource());

        SendNotificationsViewModel viewModel = new SendNotificationsViewModel(resourceList, 0L, 0L, 0L, COMPETITION_ID, "compName");
        when(sendNotificationsModelPopulator.populate(COMPETITION_ID, applicationsIds)).thenReturn(viewModel);
        mockMvc.perform(get("/competition/{competitionId}/funding/send?application_ids={applicationId}", COMPETITION_ID, APPLICATION_ID_ONE))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-send-notifications"));
    }

    @Test
    public void sendNotificationsTest() throws Exception {

        when(applicationFundingDecisionService.sendFundingNotifications(any(FundingNotificationResource.class))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/{competitionId}/funding/send", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("subject", "a subject")
                .param("message", "a message")
                .param("fundingDecisions[" + APPLICATION_ID_ONE + "]", String.valueOf(FUNDED)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/" + COMPETITION_ID + "/manage-funding-applications"));

        verify(applicationFundingDecisionService).sendFundingNotifications(any(FundingNotificationResource.class));
    }

    @Test
    public void sendNotificationsTestMultipleApplications() throws Exception {

        when(applicationFundingDecisionService.sendFundingNotifications(any(FundingNotificationResource.class))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/{competitionId}/funding/send", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("message", "a message")
                .param("fundingDecisions[" + APPLICATION_ID_ONE + "]", String.valueOf(FUNDED))
                .param("fundingDecisions[" + APPLICATION_ID_TWO + "]", String.valueOf(UNFUNDED)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/" + COMPETITION_ID + "/manage-funding-applications"));

        verify(applicationFundingDecisionService).sendFundingNotifications(any(FundingNotificationResource.class));
    }

    @Test
    public void sendNotificationsTestWithInvalidMessage() throws Exception {
        when(applicationFundingDecisionService.sendFundingNotifications(any(FundingNotificationResource.class))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/{competitionId}/funding/send", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("fundingDecisions[" + APPLICATION_ID_ONE + "]", String.valueOf(FUNDED)))
                .andExpect(view().name("comp-mgt-send-notifications"))
                .andExpect(model().attributeHasFieldErrors("form", "message"))
                .andReturn();

        verify(applicationFundingDecisionService, never()).sendFundingNotifications(any(FundingNotificationResource.class));
    }

    @Test
    public void sendNotificationsWithInvalidFundingDecisions() throws Exception {

        when(applicationFundingDecisionService.sendFundingNotifications(any(FundingNotificationResource.class))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/{competitionId}/funding/send", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("message", "a message"))
                .andExpect(view().name("comp-mgt-send-notifications"))
                .andExpect(model().attributeHasFieldErrors("form", "fundingDecisions"))
                .andReturn();

        verify(applicationFundingDecisionService, never()).sendFundingNotifications(any(FundingNotificationResource.class));
    }

    @Override
    protected CompetitionManagementFundingNotificationsController supplyControllerUnderTest() {
        return new CompetitionManagementFundingNotificationsController();
    }

    private Cookie createFormCookie(FundingNotificationSelectionCookie form) throws Exception {
        String cookieContent = JsonUtil.getSerializedObject(form);
        return new Cookie(format("applicationSelectionForm_comp_%s", COMPETITION_ID), getCompressedString(cookieContent));
    }

    private Optional<FundingNotificationSelectionCookie> getAssessorSelectionFormFromCookie(MockHttpServletResponse response, String cookieName) throws Exception {
        String value = getDecompressedString(response.getCookie(cookieName).getValue());
        String decodedFormJson  = URLDecoder.decode(value, CharEncoding.UTF_8);

        if (isNotBlank(decodedFormJson)) {
            return Optional.ofNullable(getObjectFromJson(decodedFormJson, FundingNotificationSelectionCookie.class));
        } else {
            return Optional.empty();
        }
    }
}
