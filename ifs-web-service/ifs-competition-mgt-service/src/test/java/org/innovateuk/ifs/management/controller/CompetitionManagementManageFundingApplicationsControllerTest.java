package org.innovateuk.ifs.management.controller;

import org.hamcrest.Matcher;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.LambdaMatcher;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.competition.resource.CompetitionFundedKeyStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.model.CompetitionInFlightModelPopulator;
import org.innovateuk.ifs.management.model.CompetitionInFlightStatsModelPopulator;
import org.innovateuk.ifs.management.model.ManageFundingApplicationsModelPopulator;
import org.innovateuk.ifs.management.viewmodel.CompetitionInFlightStatsViewModel;
import org.innovateuk.ifs.management.viewmodel.CompetitionInFlightViewModel;
import org.innovateuk.ifs.management.viewmodel.ManageFundingApplicationViewModel;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.application.builder.ApplicationSummaryResourceBuilder.newApplicationSummaryResource;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionFundedKeyStatisticsResourceBuilder.newCompetitionFundedKeyStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.ASSESSOR_FEEDBACK;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
public class CompetitionManagementManageFundingApplicationsControllerTest extends BaseControllerMockMVCTest<CompetitionManagementManageFundingApplicationsController> {


    @InjectMocks
    @Spy
    private ManageFundingApplicationsModelPopulator manageFundingApplicationsModelPopulator;

    @InjectMocks
    @Spy
    private CompetitionInFlightStatsModelPopulator competitionInFlightStatsModelPopulator;

    @InjectMocks
    @Spy
    private CompetitionInFlightModelPopulator competitionInFlightModelPopulator;

    @Test
    public void testApplications() throws Exception {
        long competitionId = 1l;
        int pageNumber = 0;
        int pageSize = 20;
        String sortField = "id";
        int totalPages = pageNumber + 2;
        int totalElements = totalPages * (pageNumber + 1);
        String filter = "";
        long changesSinceLastNotify = 10;
        String queryParams = "";
        // Mock setup
        CompetitionResource competitionResource = newCompetitionResource().withId(competitionId).withCompetitionStatus(ASSESSOR_FEEDBACK).withName("A competition").build();
        when(competitionService.getById(competitionId)).thenReturn(competitionResource);

        List<ApplicationSummaryResource> applications = newApplicationSummaryResource().with(uniqueIds()).build(pageSize);
        ApplicationSummaryPageResource applicationSummaryPageResource = new ApplicationSummaryPageResource(totalElements, totalPages, applications, pageNumber, pageSize);
        when(applicationSummaryService.getWithFundingDecisionApplications(competitionId, sortField, pageNumber, pageSize, filter)).thenReturn(applicationSummaryPageResource);

        CompetitionFundedKeyStatisticsResource keyStatistics = newCompetitionFundedKeyStatisticsResource().build();
        when(competitionKeyStatisticsRestServiceMock.getFundedKeyStatisticsByCompetition(competitionId)).thenReturn(restSuccess(keyStatistics));
        when(assessmentRestService.countByStateAndCompetition(AssessmentStates.CREATED, competitionId)).thenReturn(restSuccess(changesSinceLastNotify));

        // Expected values to match against
        CompetitionInFlightStatsViewModel keyStatisticsModel = new CompetitionInFlightStatsViewModel(keyStatistics);
        CompetitionInFlightViewModel competitionInFlightViewModel = new CompetitionInFlightViewModel(competitionResource, emptyList(), changesSinceLastNotify, keyStatisticsModel);
        ManageFundingApplicationViewModel model = new ManageFundingApplicationViewModel(applicationSummaryPageResource, competitionInFlightViewModel, new PaginationViewModel(applicationSummaryPageResource, queryParams), sortField, filter, competitionId, competitionResource.getName());


        // Method under test
        mockMvc.perform(get("/competition/{competitionId}/manage-funding-applications", competitionId))
                .andExpect(status().isOk())
                .andExpect(view().name("comp-mgt-manage-funding-applications"))
                .andExpect(model().attribute("model", manageFundingApplicationViewModelMatcher(model)));

    }

    @Test
    public void testSelectApplications() throws Exception {
        long competitionId = 1l;
        mockMvc.perform(post("/competition/{competitionId}/manage-funding-applications", competitionId).
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                param("ids[0]", "18").
        param("ids[3]", "21"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/competition/1/funding/send?application_ids=18,21"));
    }

    private Matcher<ManageFundingApplicationViewModel> manageFundingApplicationViewModelMatcher(ManageFundingApplicationViewModel toMatch) {
        return new LambdaMatcher<>(match -> {
            assertEquals(toMatch.getFilter(), match.getFilter());
            assertEquals(toMatch.getCompetitionName(), match.getCompetitionName());
            assertEquals(toMatch.getSortField(), match.getSortField());
            assertEquals(toMatch.getCompetitionId(), match.getCompetitionId());
            assertEquals(toMatch.getResults().getSize(), match.getResults().getSize());
            assertEquals(toMatch.getResults().getContent().get(0).getId(), match.getResults().getContent().get(0).getId());
            CompetitionInFlightViewModel toMatchCompetitionInFlightViewModel = toMatch.getKeyStatistics();
            CompetitionInFlightViewModel matchCompetitionInFlightViewModel = match.getKeyStatistics();
            assertEquals(toMatchCompetitionInFlightViewModel.getKeyStatistics().getStatOne(), matchCompetitionInFlightViewModel.getKeyStatistics().getStatOne());
            assertEquals(toMatchCompetitionInFlightViewModel.getKeyStatistics().getStatTwo(), matchCompetitionInFlightViewModel.getKeyStatistics().getStatTwo());
            assertEquals(toMatchCompetitionInFlightViewModel.getKeyStatistics().getStatThree(), matchCompetitionInFlightViewModel.getKeyStatistics().getStatThree());
            assertEquals(toMatchCompetitionInFlightViewModel.getKeyStatistics().getStatFour(), matchCompetitionInFlightViewModel.getKeyStatistics().getStatFour());
            assertEquals(toMatchCompetitionInFlightViewModel.getKeyStatistics().getStatFive(), matchCompetitionInFlightViewModel.getKeyStatistics().getStatFive());
            assertEquals(toMatchCompetitionInFlightViewModel.getKeyStatistics().isCanManageFundingNotifications(), matchCompetitionInFlightViewModel.getKeyStatistics().isCanManageFundingNotifications());
            return true;
        });
    }

    @Override
    protected CompetitionManagementManageFundingApplicationsController supplyControllerUnderTest() {
        return new CompetitionManagementManageFundingApplicationsController();
    }
}
