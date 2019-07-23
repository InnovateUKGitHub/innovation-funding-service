package org.innovateuk.ifs.management.service;

import com.google.common.collect.Lists;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationPageResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.resource.search.CompetitionSearchResult;
import org.innovateuk.ifs.competition.resource.search.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.search.LiveCompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.search.UpcomingCompetitionSearchResultItem;
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
import static org.innovateuk.ifs.competition.builder.UpcomingCompetitionSearchResultItemBuilder.newUpcomingCompetitionSearchResultItem;
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
    public void getLiveCompetitions() {
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
    public void getProjectSetupCompetitions() {
        CompetitionSearchResult expected = new CompetitionSearchResult();

        when(competitionRestService.findProjectSetupCompetitions(1)).thenReturn(restSuccess(expected));
        when(applicationRestService.getLatestEmailFundingDate(1L)).thenReturn(restSuccess(ZonedDateTime.now()));
        when(applicationRestService.getLatestEmailFundingDate(2L)).thenReturn(restSuccess(ZonedDateTime.now()));

        CompetitionSearchResult actual = service.getProjectSetupCompetitions(1);

        assertEquals(expected, actual);
    }

    @Test
    public void getUpcomingCompetitions() {
        UpcomingCompetitionSearchResultItem resource1 = newUpcomingCompetitionSearchResultItem().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();
        UpcomingCompetitionSearchResultItem resource2 = newUpcomingCompetitionSearchResultItem().withCompetitionStatus(CompetitionStatus.READY_TO_OPEN).build();
        when(competitionRestService.findUpcomingCompetitions()).thenReturn(restSuccess(Lists.newArrayList(resource1, resource2)));

        Map<CompetitionStatus, List<CompetitionSearchResultItem>> result = service.getUpcomingCompetitions();

        assertTrue(result.get(CompetitionStatus.COMPETITION_SETUP).contains(resource1));
        assertTrue(result.get(CompetitionStatus.READY_TO_OPEN).contains(resource2));
        assertEquals(result.get(CompetitionStatus.ASSESSOR_FEEDBACK), null);
    }

    @Test
    public void getNonIfsCompetitions() {
        CompetitionSearchResult expected = new CompetitionSearchResult();
        when(competitionRestService.findNonIfsCompetitions(1)).thenReturn(restSuccess(expected));

        CompetitionSearchResult actual = service.getNonIfsCompetitions(1);

        assertEquals(expected, actual);
    }

    @Test
    public void getPreviousCompetitions() {
        CompetitionSearchResult expected = new CompetitionSearchResult();
        when(competitionRestService.findFeedbackReleasedCompetitions(1)).thenReturn(restSuccess(expected));

        CompetitionSearchResult actual = service.getPreviousCompetitions(1);

        assertEquals(expected, actual);
    }

    @Test
    public void getCompetitionCounts() {
        CompetitionCountResource resource = new CompetitionCountResource();
        when(competitionRestService.countCompetitions()).thenReturn(restSuccess(resource));

        CompetitionCountResource result = service.getCompetitionCounts();

        assertEquals(result, resource);
    }

    @Test
    public void searchCompetitions() {
        CompetitionSearchResult results = new CompetitionSearchResult();
        results.setContent(new ArrayList<>());
        String searchQuery = "SearchQuery";
        int page = 1;
        when(competitionRestService.searchCompetitions(searchQuery, page)).thenReturn(restSuccess(results));

        CompetitionSearchResult actual = service.searchCompetitions(searchQuery, page);

        assertEquals(actual, results);
    }

    @Test
    public void wildcardSearchByApplicationId() {

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
