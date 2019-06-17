
package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.search.CompetitionSearchResult;
import org.innovateuk.ifs.competition.resource.search.LiveCompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.search.UpcomingCompetitionSearchResultItem;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.liveCompetitionSearchResultItemListType;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.upcomingCompetitionSearchResultItemListType;
import static org.innovateuk.ifs.competition.builder.LiveCompetitionSearchResultItemBuilder.newLiveCompetitionSearchResultItem;
import static org.innovateuk.ifs.competition.builder.UpcomingCompetitionSearchResultItemBuilder.newUpcomingCompetitionSearchResultItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class CompetitionSearchRestServiceMocksTest extends BaseRestServiceUnitTest<CompetitionSearchRestServiceImpl> {

    private static final String competitionsRestURL = "/competition";

    @Override
    protected CompetitionSearchRestServiceImpl registerRestServiceUnderTest() {
        return new CompetitionSearchRestServiceImpl();
    }

    @Test
    public void findLiveCompetitions() {
        List<LiveCompetitionSearchResultItem> expected = newLiveCompetitionSearchResultItem().build(1);

        setupGetWithRestResultExpectations(competitionsRestURL + "/live", liveCompetitionSearchResultItemListType(), expected);

        List<LiveCompetitionSearchResultItem> actual = service.findLiveCompetitions().getSuccess();
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    public void findProjectSetupCompetitions() {
        int page = 0;
        CompetitionSearchResult expected = mock(CompetitionSearchResult.class);

        setupGetWithRestResultExpectations(competitionsRestURL + "/project-setup?page=" + page, CompetitionSearchResult.class, expected);

        CompetitionSearchResult actual = service.findProjectSetupCompetitions(page).getSuccess();
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    public void findUpcomingCompetitions() {
        List<UpcomingCompetitionSearchResultItem> expected = newUpcomingCompetitionSearchResultItem().build(1);

        setupGetWithRestResultExpectations(competitionsRestURL + "/upcoming", upcomingCompetitionSearchResultItemListType(), expected);

        List<UpcomingCompetitionSearchResultItem> actual = service.findUpcomingCompetitions().getSuccess();
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    public void findNonIfsCompetitions() {
        int page = 0;
        CompetitionSearchResult expected = mock(CompetitionSearchResult.class);

        setupGetWithRestResultExpectations(competitionsRestURL + "/non-ifs?page=" + page, CompetitionSearchResult.class, expected);

        CompetitionSearchResult actual = service.findNonIfsCompetitions(page).getSuccess();
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    public void findFeedbackReleasedCompetitions() {
        int page = 0;
        CompetitionSearchResult expected = mock(CompetitionSearchResult.class);

        setupGetWithRestResultExpectations(competitionsRestURL + "/post-submission/feedback-released?page=" + page, CompetitionSearchResult.class, expected);

        CompetitionSearchResult actual = service.findFeedbackReleasedCompetitions(page).getSuccess();
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    public void countCompetitions() {
        CompetitionCountResource expected = new CompetitionCountResource();

        setupGetWithRestResultExpectations(competitionsRestURL + "/count", CompetitionCountResource.class, expected);

        CompetitionCountResource actual = service.countCompetitions().getSuccess();
        assertNotNull(actual);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void searchCompetitions() {
        CompetitionSearchResult expected = new CompetitionSearchResult();
        String searchQuery = "SearchQuery";
        int page = 1;

        setupGetWithRestResultExpectations(competitionsRestURL + "/search?page=" + page + "&searchQuery=" + searchQuery, CompetitionSearchResult.class, expected);

        CompetitionSearchResult actual = service.searchCompetitions(searchQuery, page).getSuccess();
        assertNotNull(actual);
        Assert.assertEquals(expected, actual);
    }
}
