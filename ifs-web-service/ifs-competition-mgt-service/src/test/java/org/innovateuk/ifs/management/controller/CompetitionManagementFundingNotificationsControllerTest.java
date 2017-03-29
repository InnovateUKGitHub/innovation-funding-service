package org.innovateuk.ifs.management.controller;

import org.hamcrest.Matcher;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.LambdaMatcher;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.NotificationResource;
import org.innovateuk.ifs.application.service.ApplicationFundingDecisionService;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationSummaryResourceBuilder.newApplicationSummaryResource;
import static org.innovateuk.ifs.application.resource.FundingDecision.FUNDED;
import static org.innovateuk.ifs.application.resource.FundingDecision.UNFUNDED;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionFundedKeyStatisticsResourceBuilder.newCompetitionFundedKeyStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.ASSESSOR_FEEDBACK;
import static org.junit.Assert.assertEquals;
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

    @Mock
    private ApplicationFundingDecisionService applicationFundingServiceMock;

    public static final Long COMPETITION_ID = 22L;
    public static final Long APPLICATION_ID_ONE = 1L;
    public static final Long APPLICATION_ID_TWO = 2L;

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
        when(applicationSummaryService.getWithFundingDecisionApplications(COMPETITION_ID, sortField, pageNumber, pageSize, filter, sendFilter, fundingFilter)).thenReturn(applicationSummaryPageResource);

        CompetitionFundedKeyStatisticsResource keyStatistics = newCompetitionFundedKeyStatisticsResource().build();
        when(competitionKeyStatisticsRestServiceMock.getFundedKeyStatisticsByCompetition(COMPETITION_ID)).thenReturn(restSuccess(keyStatistics));
        when(assessmentRestService.countByStateAndCompetition(AssessmentStates.CREATED, COMPETITION_ID)).thenReturn(restSuccess(changesSinceLastNotify));

        // Expected values to match against
        CompetitionInFlightStatsViewModel keyStatisticsModel = competitionInFlightStatsModelPopulator.populateStatsViewModel(competitionResource);
        ManageFundingApplicationViewModel model = new ManageFundingApplicationViewModel(applicationSummaryPageResource, keyStatisticsModel, new PaginationViewModel(applicationSummaryPageResource, queryParams), sortField, COMPETITION_ID, competitionResource.getName());


        // Method under test
        mockMvc.perform(get("/competition/{competitionId}/manage-funding-applications", COMPETITION_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-manage-funding-applications"))
                .andExpect(model().attribute("model", manageFundingApplicationViewModelMatcher(model)));

    }

    @Test
    public void testSelectApplications() throws Exception {
        long competitionId = 1l;
        mockMvc.perform(post("/competition/{competitionId}/manage-funding-applications", competitionId).
                contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("ids[0]", "18")
                .param("ids[3]", "21"))
                .andExpect(status().is3xxRedirection())
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
    public void getSendNotificationsPageTest() throws Exception {

        List<Long> applicationsIds = Arrays.asList(APPLICATION_ID_ONE);
        List<ApplicationSummaryResource> resourceList = Arrays.asList(new ApplicationSummaryResource());

        SendNotificationsViewModel viewModel = new SendNotificationsViewModel(resourceList, 0L, 0L, 0L, COMPETITION_ID, "compName");
        when(sendNotificationsModelPopulator.populate(COMPETITION_ID, applicationsIds)).thenReturn(viewModel);
        mockMvc.perform(get("/competition/{competitionId}/funding/send?application_ids={applicationId}", COMPETITION_ID, APPLICATION_ID_ONE))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-send-notifications"));
    }

    @Test
    public void sendNotificationsTest() throws Exception {

        when(applicationFundingServiceMock.sendFundingNotifications(any(NotificationResource.class))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/{competitionId}/funding/send", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("subject", "a subject")
                .param("message", "a message")
                .param("fundingDecisions[" + APPLICATION_ID_ONE + "]", String.valueOf(FUNDED)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/" + COMPETITION_ID + "/manage-funding-applications"));

        verify(applicationFundingServiceMock).sendFundingNotifications(any(NotificationResource.class));
    }

    @Test
    public void sendNotificationsTestMultipleApplications() throws Exception {

        when(applicationFundingServiceMock.sendFundingNotifications(any(NotificationResource.class))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/{competitionId}/funding/send", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("subject", "a subject")
                .param("message", "a message")
                .param("fundingDecisions[" + APPLICATION_ID_ONE + "]", String.valueOf(FUNDED))
                .param("fundingDecisions[" + APPLICATION_ID_TWO + "]", String.valueOf(UNFUNDED)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/competition/" + COMPETITION_ID + "/manage-funding-applications"));

        verify(applicationFundingServiceMock).sendFundingNotifications(any(NotificationResource.class));
    }

    @Test
    public void sendNotificationsWithInvalidSubject() throws Exception {
        when(applicationFundingServiceMock.sendFundingNotifications(any(NotificationResource.class))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/{competitionId}/funding/send", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("message", "a message")
                .param("fundingDecisions[" + APPLICATION_ID_ONE + "]", String.valueOf(FUNDED)))
                .andExpect(view().name("comp-mgt-send-notifications"))
                .andExpect(model().attributeHasFieldErrors("form", "subject"))
                .andReturn();

        verify(applicationFundingServiceMock, never()).sendFundingNotifications(any(NotificationResource.class));
    }

    @Test
    public void sendNotificationsTestWithInvalidMessage() throws Exception {
        when(applicationFundingServiceMock.sendFundingNotifications(any(NotificationResource.class))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/{competitionId}/funding/send", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("subject", "a subject")
                .param("fundingDecisions[" + APPLICATION_ID_ONE + "]", String.valueOf(FUNDED)))
                .andExpect(view().name("comp-mgt-send-notifications"))
                .andExpect(model().attributeHasFieldErrors("form", "message"))
                .andReturn();

        verify(applicationFundingServiceMock, never()).sendFundingNotifications(any(NotificationResource.class));
    }


    @Test
    public void sendNotificationsWithInvalidFundingDecisions() throws Exception {

        when(applicationFundingServiceMock.sendFundingNotifications(any(NotificationResource.class))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/{competitionId}/funding/send", COMPETITION_ID)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("subject", "a subject")
                .param("message", "a message"))
                .andExpect(view().name("comp-mgt-send-notifications"))
                .andExpect(model().attributeHasFieldErrors("form", "fundingDecisions"))
                .andReturn();

        verify(applicationFundingServiceMock, never()).sendFundingNotifications(any(NotificationResource.class));
    }

    @Override
    protected CompetitionManagementFundingNotificationsController supplyControllerUnderTest() {
        return new CompetitionManagementFundingNotificationsController();
    }
}
