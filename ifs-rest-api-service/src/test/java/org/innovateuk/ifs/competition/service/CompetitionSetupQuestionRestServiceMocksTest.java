
package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.springframework.http.HttpStatus.CREATED;

/**
 *
 */
public class CompetitionSetupQuestionRestServiceMocksTest extends BaseRestServiceUnitTest<CompetitionSetupQuestionRestServiceImpl> {

    private static final String competitionsRestURL = "/competition-setup-question";

    @Override
    protected CompetitionSetupQuestionRestServiceImpl registerRestServiceUnderTest() {
        return new CompetitionSetupQuestionRestServiceImpl();
    }

    @Test
    public void test_getByQuestionId() {
        long questionId = 1L;
        CompetitionSetupQuestionResource expected = new CompetitionSetupQuestionResource();

        setupGetWithRestResultExpectations(competitionsRestURL + "/getById/" + questionId, CompetitionSetupQuestionResource.class, expected);

        CompetitionSetupQuestionResource response = service.getByQuestionId(questionId).getSuccessObject();

        assertNotNull(response);
        Assert.assertEquals(expected, response);
    }

    @Test
    public void test_save() {
        long questionId = 1L;
        CompetitionSetupQuestionResource toSave = new CompetitionSetupQuestionResource();
        toSave.setQuestionId(questionId);

        setupPutWithRestResultExpectations(competitionsRestURL + "/save", toSave);

        RestResult<Void> response = service.save(toSave);

        assertTrue(response.isSuccess());
    }

    @Test
    public void test_deleteById() {
        long questionId = 1L;
        setupDeleteWithRestResultExpectations(competitionsRestURL + "/deleteById/" + questionId);

        RestResult<Void> response = service.deleteById(questionId);

        assertTrue(response.isSuccess());
    }

    @Test
    public void test_addDefaultToCompetition() {
        long competitionId = 1L;

        CompetitionSetupQuestionResource expected = new CompetitionSetupQuestionResource();
        setupPostWithRestResultExpectations(competitionsRestURL + "/addDefaultToCompetition/" + competitionId, CompetitionSetupQuestionResource.class, null, expected, CREATED);

        RestResult<CompetitionSetupQuestionResource> response = service.addDefaultToCompetition(competitionId);

        assertTrue(response.isSuccess());
    }
}
