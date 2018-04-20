
package org.innovateuk.ifs.question.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.springframework.http.HttpStatus.CREATED;

public class QuestionSetupRestServiceMocksTest extends BaseRestServiceUnitTest<QuestionSetupCompetitionRestServiceImpl> {

    private static final String questionRestURL = "/question-setup";

    @Override
    protected QuestionSetupCompetitionRestServiceImpl registerRestServiceUnderTest() {
        return new QuestionSetupCompetitionRestServiceImpl();
    }

    @Test
    public void test_getByQuestionId() {
        long questionId = 1L;
        CompetitionSetupQuestionResource expected = new CompetitionSetupQuestionResource();

        setupGetWithRestResultExpectations(questionRestURL + "/getById/" + questionId, CompetitionSetupQuestionResource.class, expected);

        CompetitionSetupQuestionResource response = service.getByQuestionId(questionId).getSuccess();

        assertNotNull(response);
        Assert.assertEquals(expected, response);
    }

    @Test
    public void test_save() {
        long questionId = 1L;
        CompetitionSetupQuestionResource toSave = new CompetitionSetupQuestionResource();
        toSave.setQuestionId(questionId);

        setupPutWithRestResultExpectations(questionRestURL + "/save", toSave);

        RestResult<Void> response = service.save(toSave);

        assertTrue(response.isSuccess());
    }

    @Test
    public void test_deleteById() {
        long questionId = 1L;
        setupDeleteWithRestResultExpectations(questionRestURL + "/deleteById/" + questionId);

        RestResult<Void> response = service.deleteById(questionId);

        assertTrue(response.isSuccess());
    }

    @Test
    public void test_addDefaultToCompetition() {
        long competitionId = 1L;

        CompetitionSetupQuestionResource expected = new CompetitionSetupQuestionResource();
        setupPostWithRestResultExpectations(questionRestURL + "/addDefaultToCompetition/" + competitionId, CompetitionSetupQuestionResource.class, null, expected, CREATED);

        RestResult<CompetitionSetupQuestionResource> response = service.addDefaultToCompetition(competitionId);

        assertTrue(response.isSuccess());
    }
}
