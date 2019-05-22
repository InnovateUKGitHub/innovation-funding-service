package org.innovateuk.ifs.management.service;

import com.google.common.collect.Lists;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationPageResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.resource.search.*;
import org.innovateuk.ifs.competition.service.CompetitionSearchRestService;
import org.innovateuk.ifs.management.dashboard.service.CompetitionDashboardSearchServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.LiveCompetitionSearchResultItemBuilder.newLiveCompetitionSearchResultItem;
import static org.innovateuk.ifs.competition.builder.NonIfsCompetitionSearchResultItemBuilder.newNonIfsCompetitionSearchResultItem;
import static org.innovateuk.ifs.competition.builder.PreviousCompetitionSearchResultItemBuilder.newPreviousCompetitionSearchResultItem;
import static org.innovateuk.ifs.competition.builder.ProjectSetupCompetitionSearchResultItemBuilder.newProjectSetupCompetitionSearchResultItem;
import static org.innovateuk.ifs.competition.builder.UpcomingCompetitionSearchResultItemBuilder.newUpcomingCompetitionSearchResultItem;
import static org.innovateuk.ifs.management.dashboard.service.CompetitionDashboardSearchServiceImpl.COMPETITION_PAGE_SIZE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CompetitionDashboardSearchServiceImplTest extends BaseServiceUnitTest<CompetitionDashboardSearchServiceImpl> {

    @Mock
    private CompetitionSearchRestService competitionRestService;

    @Mock
    private ApplicationRestService applicationRestService;

    @Test
    public void getLiveCompetitions() throws Exception {
        LiveCompetitionSearchResultItem resource1 = newLiveCompetitionSearchResultItem().withCompetitionStatus(CompetitionStatus.OPEN).build();
        LiveCompetitionSearchResultItem resource2 = newLiveCompetitionSearchResultItem().withCompetitionStatus(CompetitionStatus.OPEN).build();
        LiveCompetitionSearchResultItem resource3 = newLiveCompetitionSearchResultItem().withCompetitionStatus(CompetitionStatus.IN_ASSESSMENT).build();

        when(competitionRestService.findLiveCompetitions()).thenReturn(restSuccess(asList(resource1, resource2, resource3)));

        Map<CompetitionStatus, List<CompetitionSearchResultItem>> result = service.getLiveCompetitions();

        assertTrue(result.get(CompetitionStatus.OPEN).contains(resource1));
        assertTrue(result.get(CompetitionStatus.OPEN).contains(resource2));
        assertTrue(result.get(CompetitionStatus.IN_ASSESSMENT).contains(resource3));
        assertEquals(result.get(CompetitionStatus.ASSESSOR_FEEDBACK), null);
    }

    @Test
    public void getProjectSetupCompetitions() throws Exception {
        ProjectSetupCompetitionSearchResultItem resource1 = newProjectSetupCompetitionSearchResultItem().withCompetitionStatus(CompetitionStatus.PROJECT_SETUP).build();
        ProjectSetupCompetitionSearchResultItem resource2 = newProjectSetupCompetitionSearchResultItem().withCompetitionStatus(CompetitionStatus.PROJECT_SETUP).build();

        when(competitionRestService.findProjectSetupCompetitions()).thenReturn(restSuccess(Lists.newArrayList(resource1, resource2)));
        when(applicationRestService.getLatestEmailFundingDate(1L)).thenReturn(restSuccess(ZonedDateTime.now()));
        when(applicationRestService.getLatestEmailFundingDate(2L)).thenReturn(restSuccess(ZonedDateTime.now()));

        Map<CompetitionStatus, List<CompetitionSearchResultItem>> result = service.getProjectSetupCompetitions();

        assertTrue(result.get(CompetitionStatus.PROJECT_SETUP).contains(resource1));
        assertTrue(result.get(CompetitionStatus.PROJECT_SETUP).contains(resource2));
    }

    @Test
    public void getUpcomingCompetitions() throws Exception {
        UpcomingCompetitionSearchResultItem resource1 = newUpcomingCompetitionSearchResultItem().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();
        UpcomingCompetitionSearchResultItem resource2 = newUpcomingCompetitionSearchResultItem().withCompetitionStatus(CompetitionStatus.READY_TO_OPEN).build();
        when(competitionRestService.findUpcomingCompetitions()).thenReturn(restSuccess(Lists.newArrayList(resource1, resource2)));

        Map<CompetitionStatus, List<CompetitionSearchResultItem>> result = service.getUpcomingCompetitions();

        assertTrue(result.get(CompetitionStatus.COMPETITION_SETUP).contains(resource1));
        assertTrue(result.get(CompetitionStatus.READY_TO_OPEN).contains(resource2));
        assertEquals(result.get(CompetitionStatus.ASSESSOR_FEEDBACK), null);
    }

    @Test
    public void getNonIfsCompetitions() throws Exception {
        NonIfsCompetitionSearchResultItem resource1 = newNonIfsCompetitionSearchResultItem().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();
        NonIfsCompetitionSearchResultItem resource2 = newNonIfsCompetitionSearchResultItem().withCompetitionStatus(CompetitionStatus.OPEN).build();
        when(competitionRestService.findNonIfsCompetitions()).thenReturn(restSuccess(Lists.newArrayList(resource1, resource2)));

        Map<CompetitionStatus, List<CompetitionSearchResultItem>> result = service.getNonIfsCompetitions();

        assertTrue(result.get(CompetitionStatus.COMPETITION_SETUP).contains(resource1));
        assertTrue(result.get(CompetitionStatus.OPEN).contains(resource2));
        assertEquals(result.get(CompetitionStatus.ASSESSOR_FEEDBACK), null);
    }

    @Test
    public void getPreviousCompetitions() throws Exception {
        PreviousCompetitionSearchResultItem resource1 = newPreviousCompetitionSearchResultItem().withCompetitionStatus(CompetitionStatus.PROJECT_SETUP).build();
        PreviousCompetitionSearchResultItem resource2 = newPreviousCompetitionSearchResultItem().withCompetitionStatus(CompetitionStatus.CLOSED).build();
        PreviousCompetitionSearchResultItem resource3 = newPreviousCompetitionSearchResultItem().withCompetitionStatus(CompetitionStatus.PREVIOUS).build();
        when(competitionRestService.findFeedbackReleasedCompetitions()).thenReturn(restSuccess(Lists.newArrayList(resource1, resource2, resource3)));

        Map<CompetitionStatus, List<CompetitionSearchResultItem>> result = service.getPreviousCompetitions();

        assertTrue(result.get(CompetitionStatus.PREVIOUS).contains(resource1));
        assertTrue(result.get(CompetitionStatus.PREVIOUS).contains(resource2));
        assertTrue(result.get(CompetitionStatus.PREVIOUS).contains(resource3));
        assertEquals(result.get(CompetitionStatus.OPEN), null);
    }

    @Test
    public void getCompetitionCounts() throws Exception {
        CompetitionCountResource resource = new CompetitionCountResource();
        when(competitionRestService.countCompetitions()).thenReturn(restSuccess(resource));

        CompetitionCountResource result = service.getCompetitionCounts();

        assertEquals(result, resource);
    }

    @Test
    public void searchCompetitions() throws Exception {
        CompetitionSearchResult results = new CompetitionSearchResult();
        results.setContent(new ArrayList<>());
        String searchQuery = "SearchQuery";
        int page = 1;
        when(competitionRestService.searchCompetitions(searchQuery, page, COMPETITION_PAGE_SIZE)).thenReturn(restSuccess(results));

        CompetitionSearchResult actual = service.searchCompetitions(searchQuery, page);

        assertEquals(actual, results);
    }

    @Test
    public void wildcardSearchByApplicationId() throws Exception {

        String searchString = "12";
        int pageNumber = 0;
        int pageSize = 5;

        ApplicationPageResource expectedPageResource = new ApplicationPageResource();
        when(applicationRestService.wildcardSearchById(searchString, pageNumber, pageSize)).thenReturn(restSuccess(expectedPageResource));

        ApplicationPageResource applicationPageResource = service.wildcardSearchByApplicationId(searchString, pageNumber, pageSize);

        assertEquals(expectedPageResource, applicationPageResource);

        verify(applicationRestService).wildcardSearchById(searchString, pageNumber, pageSize);
    }

    @Override
    protected CompetitionDashboardSearchServiceImpl supplyServiceUnderTest() {
        return new CompetitionDashboardSearchServiceImpl();
    }
}
