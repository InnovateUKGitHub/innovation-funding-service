
package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionOpenQueryResource;
import org.innovateuk.ifs.competition.resource.CompetitionSearchResultItem;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.resource.SpendProfileStatusResource;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.*;
import static org.junit.Assert.*;

public class CompetitionPostSubmissionRestServiceMocksTest extends BaseRestServiceUnitTest<CompetitionPostSubmissionRestServiceImpl> {

    private static final String competitionsRestURL = "/competition/postSubmission";

    @Override
    protected CompetitionPostSubmissionRestServiceImpl registerRestServiceUnderTest() {
        return new CompetitionPostSubmissionRestServiceImpl();
    }

    @Test
    public void releaseFeedback() {
        long competitionId = 1L;
        setupPutWithRestResultExpectations(competitionsRestURL + "/" + competitionId + "/release-feedback", HttpStatus.OK);

        RestResult<Void> result = service.releaseFeedback(competitionId);
        assertTrue(result.isSuccess());
    }

    @Test
    public void findFeedbackReleasedCompetitions() {

        List<CompetitionSearchResultItem> returnedResponse =
                singletonList(new CompetitionSearchResultItem(1L, "Name", Collections.EMPTY_SET, 0, "", CompetitionStatus.OPEN, "Comp Type", 0, null, null, null));

        setupGetWithRestResultExpectations(competitionsRestURL + "/feedback-released", competitionSearchResultItemListType(), returnedResponse);

        List<CompetitionSearchResultItem> responses = service.findFeedbackReleasedCompetitions().getSuccess();
        assertNotNull(responses);
        assertEquals(returnedResponse, responses);
    }

    @Test
    public void findOpenQueryCount() {
        setupGetWithRestResultExpectations(competitionsRestURL + "/" + 123L+ "/queries/open/count", Long.class, 13L);

        Long responses = service.getCompetitionOpenQueriesCount(123L).getSuccess();
        assertNotNull(responses);
        assertEquals(13L, responses.longValue());
    }

    @Test
    public void findOpenQueries() {

        List<CompetitionOpenQueryResource> returnedResponse =
                singletonList(new CompetitionOpenQueryResource(1L, 2L, "org", 3L, "proj"));

        setupGetWithRestResultExpectations(competitionsRestURL + "/" + 123L+ "/queries/open", competitionOpenQueryListType(), returnedResponse);

        List<CompetitionOpenQueryResource> responses = service.getCompetitionOpenQueries(123L).getSuccess();
        assertNotNull(responses);
        assertEquals(returnedResponse, responses);
    }

    @Test
    public void getPendingSpendProfiles() {

        List<SpendProfileStatusResource> returnedResponse = singletonList(new SpendProfileStatusResource());

        setupGetWithRestResultExpectations(competitionsRestURL + "/123" + "/pending-spend-profiles", spendProfileStatusResourceListType(), returnedResponse);

        List<SpendProfileStatusResource> response = service.getPendingSpendProfiles(123L).getSuccess();
        assertNotNull(response);
        Assert.assertEquals(returnedResponse, response);
    }

    @Test
    public void countPendingSpendProfiles() {

        Long returnedResponse = 3L;

        setupGetWithRestResultExpectations(competitionsRestURL + "/123" + "/count-pending-spend-profiles", Long.class, returnedResponse);

        Long response = service.countPendingSpendProfiles(123L).getSuccess();
        assertNotNull(response);
        Assert.assertEquals(returnedResponse, response);
    }

    @Test
    public void closeAssessment() {
        long competitionId = 1L;
        setupPutWithRestResultExpectations(competitionsRestURL + "/" + competitionId + "/close-assessment", HttpStatus.OK);

        RestResult<Void> result = service.closeAssessment(competitionId);
        assertTrue(result.isSuccess());
    }
}
