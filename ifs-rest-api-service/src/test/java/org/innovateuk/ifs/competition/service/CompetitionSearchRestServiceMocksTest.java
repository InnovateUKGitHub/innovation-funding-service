
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
        List<LiveCompetitionSearchResultItem> returnedResponse = newLiveCompetitionSearchResultItem().build(1);

        setupGetWithRestResultExpectations(competitionsRestURL + "/live", liveCompetitionSearchResultItemListType(), returnedResponse);

        List<LiveCompetitionSearchResultItem> responses = service.findLiveCompetitions().getSuccess();
        assertNotNull(responses);
        assertEquals(returnedResponse, responses);
    }

    @Test
    public void findProjectSetupCompetitions() {
        List<ProjectSetupCompetitionSearchResultItem> returnedResponse = newProjectSetupCompetitionSearchResultItem().build(1);

        setupGetWithRestResultExpectations(competitionsRestURL + "/project-setup", projectSetupCompetitionSearchResultItemListType(), returnedResponse);

        List<ProjectSetupCompetitionSearchResultItem> responses = service.findProjectSetupCompetitions().getSuccess();
        assertNotNull(responses);
        assertEquals(returnedResponse, responses);
    }

    @Test
    public void findUpcomingCompetitions() {
        List<UpcomingCompetitionSearchResultItem> returnedResponse = newUpcomingCompetitionSearchResultItem().build(1);

        setupGetWithRestResultExpectations(competitionsRestURL + "/upcoming", upcomingCompetitionSearchResultItemListType(), returnedResponse);

        List<UpcomingCompetitionSearchResultItem> responses = service.findUpcomingCompetitions().getSuccess();
        assertNotNull(responses);
        assertEquals(returnedResponse, responses);
    }

    @Test
    public void findNonIfsCompetitions() {
        List<NonIfsCompetitionSearchResultItem> returnedResponse = newNonIfsCompetitionSearchResultItem().build(1);

        setupGetWithRestResultExpectations(competitionsRestURL + "/non-ifs", nonIfsCompetitionSearchReultItemListType(), returnedResponse);

        List<NonIfsCompetitionSearchResultItem> responses = service.findNonIfsCompetitions().getSuccess();
        assertNotNull(responses);
        assertEquals(returnedResponse, responses);
    }

    @Test
    public void findFeedbackReleasedCompetitions() {
        List<PreviousCompetitionSearchResultItem> returnedResponse = newPreviousCompetitionSearchResultItem().build(1);

        setupGetWithRestResultExpectations(competitionsRestURL + "/post-submission/feedback-released", previousCompetitionSearchResultItemListType(), returnedResponse);

        List<PreviousCompetitionSearchResultItem> responses = service.findFeedbackReleasedCompetitions().getSuccess();
        assertNotNull(responses);
        assertEquals(returnedResponse, responses);
    }

    @Test
    public void countCompetitions() {
        CompetitionCountResource returnedResponse = new CompetitionCountResource();

        setupGetWithRestResultExpectations(competitionsRestURL + "/count", CompetitionCountResource.class, returnedResponse);

        CompetitionCountResource responses = service.countCompetitions().getSuccess();
        assertNotNull(responses);
        Assert.assertEquals(returnedResponse, responses);
    }

    @Test
    public void searchCompetitions() {
        CompetitionSearchResult returnedResponse = new CompetitionSearchResult();
        String searchQuery = "SearchQuery";
        int page = 1;
        int size = 20;

        setupGetWithRestResultExpectations(competitionsRestURL + "/search/" + page + "/" + size + "?searchQuery=" + searchQuery, CompetitionSearchResult.class, returnedResponse);

        CompetitionSearchResult responses = service.searchCompetitions(searchQuery, page, size).getSuccess();
        assertNotNull(responses);
        Assert.assertEquals(returnedResponse, responses);
    }
}
