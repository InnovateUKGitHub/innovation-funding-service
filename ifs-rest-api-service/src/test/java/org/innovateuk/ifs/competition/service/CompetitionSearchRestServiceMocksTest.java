
package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.competition.resource.CompetitionCountResource;
import org.innovateuk.ifs.competition.resource.search.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.*;
import static org.innovateuk.ifs.competition.builder.LiveCompetitionSearchResultItemBuilder.newLiveCompetitionSearchResultItem;
import static org.innovateuk.ifs.competition.builder.NonIfsCompetitionSearchResultItemBuilder.newNonIfsCompetitionSearchResultItem;
import static org.innovateuk.ifs.competition.builder.PreviousCompetitionSearchResultItemBuilder.newPreviousCompetitionSearchResultItem;
import static org.innovateuk.ifs.competition.builder.ProjectSetupCompetitionSearchResultItemBuilder.newProjectSetupCompetitionSearchResultItem;
import static org.innovateuk.ifs.competition.builder.UpcomingCompetitionSearchResultItemBuilder.newUpcomingCompetitionSearchResultItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
        List<ProjectSetupCompetitionSearchResultItem> expected = newProjectSetupCompetitionSearchResultItem().build(1);

        setupGetWithRestResultExpectations(competitionsRestURL + "/project-setup", projectSetupCompetitionSearchResultItemListType(), expected);

        List<ProjectSetupCompetitionSearchResultItem> actual = service.findProjectSetupCompetitions().getSuccess();
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
        List<NonIfsCompetitionSearchResultItem> expected = newNonIfsCompetitionSearchResultItem().build(1);

        setupGetWithRestResultExpectations(competitionsRestURL + "/non-ifs", nonIfsCompetitionSearchReultItemListType(), expected);

        List<NonIfsCompetitionSearchResultItem> actual = service.findNonIfsCompetitions().getSuccess();
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    public void findFeedbackReleasedCompetitions() {
        List<PreviousCompetitionSearchResultItem> expected = newPreviousCompetitionSearchResultItem().build(1);

        setupGetWithRestResultExpectations(competitionsRestURL + "/post-submission/feedback-released", previousCompetitionSearchResultItemListType(), expected);

        List<PreviousCompetitionSearchResultItem> actual = service.findFeedbackReleasedCompetitions().getSuccess();
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
        int size = 20;

        setupGetWithRestResultExpectations(competitionsRestURL + "/search/" + page + "/" + size + "?searchQuery=" + searchQuery, CompetitionSearchResult.class, expected);

        CompetitionSearchResult actual = service.searchCompetitions(searchQuery, page, size).getSuccess();
        assertNotNull(actual);
        Assert.assertEquals(expected, actual);
    }
}
