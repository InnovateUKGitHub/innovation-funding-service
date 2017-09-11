package org.innovateuk.ifs.management.service;

import com.google.common.collect.Lists;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.service.CompetitionServiceImpl;
import org.innovateuk.ifs.competition.builder.CompetitionSearchResultItemBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResult;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionsRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionSearchResultItemBuilder.newCompetitionSearchResultItem;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CompetitionDashboardSearchServiceImplTest extends BaseServiceUnitTest<CompetitionDashboardSearchServiceImpl> {

    @Mock
    private CompetitionsRestService competitionsRestService;

    @Test
    public void test_getLiveCompetitions() throws Exception {
        CompetitionSearchResultItem resource1 = newCompetitionSearchResultItem1().withCompetitionStatus(CompetitionStatus.OPEN).build();
        CompetitionSearchResultItem resource2 = newCompetitionSearchResultItem2().withCompetitionStatus(CompetitionStatus.OPEN).build();
        CompetitionSearchResultItem resource3 = newCompetitionSearchResultItem3().withCompetitionStatus(CompetitionStatus.IN_ASSESSMENT).build();

        when(competitionsRestService.findLiveCompetitions()).thenReturn(restSuccess(asList(resource1, resource2, resource3)));

        Map<CompetitionStatus, List<CompetitionSearchResultItem>> result = service.getLiveCompetitions();

        assertTrue(result.get(CompetitionStatus.OPEN).contains(resource1));
        assertTrue(result.get(CompetitionStatus.OPEN).contains(resource2));
        assertTrue(result.get(CompetitionStatus.IN_ASSESSMENT).contains(resource3));
        assertEquals(result.get(CompetitionStatus.ASSESSOR_FEEDBACK), null);
    }

    @Test
    public void test_getProjectSetupCompetitions() throws Exception {
        CompetitionSearchResultItem resource1 = new CompetitionSearchResultItem(1L, "i1", singleton("innovation area 1"), 123, "12/02/2016", CompetitionStatus.PROJECT_SETUP, "Special", 0, null, null, null);
        CompetitionSearchResultItem resource2 = new CompetitionSearchResultItem(2L, "21", singleton("innovation area 2"), 123, "12/02/2016", CompetitionStatus.PROJECT_SETUP, "Special", 0, null, null, null);
        when(competitionsRestService.findProjectSetupCompetitions()).thenReturn(restSuccess(Lists.newArrayList(resource1, resource2)));

        Map<CompetitionStatus, List<CompetitionSearchResultItem>> result = service.getProjectSetupCompetitions();

        assertTrue(result.get(CompetitionStatus.PROJECT_SETUP).contains(resource1));
        assertTrue(result.get(CompetitionStatus.PROJECT_SETUP).contains(resource2));
        assertEquals(result.get(CompetitionStatus.ASSESSOR_FEEDBACK), null);
    }

    @Test
    public void test_getUpcomingCompetitions() throws Exception {
        CompetitionSearchResultItem resource1 = newCompetitionSearchResultItem1().withCompetitionStatus(CompetitionStatus.COMPETITION_SETUP).build();
        CompetitionSearchResultItem resource2 = newCompetitionSearchResultItem2().withCompetitionStatus(CompetitionStatus.READY_TO_OPEN).build();
        when(competitionsRestService.findUpcomingCompetitions()).thenReturn(restSuccess(Lists.newArrayList(resource1, resource2)));

        Map<CompetitionStatus, List<CompetitionSearchResultItem>> result = service.getUpcomingCompetitions();

        assertTrue(result.get(CompetitionStatus.COMPETITION_SETUP).contains(resource1));
        assertTrue(result.get(CompetitionStatus.READY_TO_OPEN).contains(resource2));
        assertEquals(result.get(CompetitionStatus.ASSESSOR_FEEDBACK), null);
    }

    @Test
    public void test_getCompetitionCounts() throws Exception {
        CompetitionCountResource resource = new CompetitionCountResource();
        when(competitionsRestService.countCompetitions()).thenReturn(restSuccess(resource));

        CompetitionCountResource result = service.getCompetitionCounts();

        assertEquals(result, resource);
    }

    @Test
    public void test_searchCompetitions() throws Exception {
        CompetitionSearchResult results = new CompetitionSearchResult();
        results.setContent(new ArrayList<>());
        String searchQuery = "SearchQuery";
        int page = 1;
        when(competitionsRestService.searchCompetitions(searchQuery, page, CompetitionServiceImpl.COMPETITION_PAGE_SIZE)).thenReturn(restSuccess(results));

        CompetitionSearchResult actual = service.searchCompetitions(searchQuery, page);

        assertEquals(actual, results);
    }

    private CompetitionSearchResultItemBuilder newCompetitionSearchResultItem1() {
        return newCompetitionSearchResultItem()
                .withId(1L)
                .withName("i1")
                .withInnovationAreaNames(singleton("innovation area 1"));
    }

    private CompetitionSearchResultItemBuilder newCompetitionSearchResultItem2() {
        return newCompetitionSearchResultItem()
                .withId(2L)
                .withName("21")
                .withInnovationAreaNames(singleton("innovation area 2"));
    }

    private CompetitionSearchResultItemBuilder newCompetitionSearchResultItem3() {
        return newCompetitionSearchResultItem()
                .withId(3L)
                .withName("31")
                .withInnovationAreaNames(singleton("innovation area 3"));
    }

    @Override
    protected CompetitionDashboardSearchServiceImpl supplyServiceUnderTest() {
        return new CompetitionDashboardSearchServiceImpl();
    }
}
