
package org.innovateuk.ifs.question.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupQuestionResource;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.CREATED;

public class QuestionSetupCompetitionRestServiceMocksTest extends
        BaseRestServiceUnitTest<QuestionSetupCompetitionRestServiceImpl> {

    private static final String questionRestURL = "/question-setup";

    @Override
    protected QuestionSetupCompetitionRestServiceImpl registerRestServiceUnderTest() {
        return new QuestionSetupCompetitionRestServiceImpl();
    }

    @Test
    public void getByQuestionId() {
        long questionId = 1L;
        CompetitionSetupQuestionResource expected = new CompetitionSetupQuestionResource();

        setupGetWithRestResultExpectations(questionRestURL + "/get-by-id/" + questionId,
                CompetitionSetupQuestionResource.class, expected);

        CompetitionSetupQuestionResource response = service.getByQuestionId(questionId).getSuccess();

        assertNotNull(response);
        Assert.assertEquals(expected, response);
    }

    @Test
    public void save() {
        long questionId = 1L;
        CompetitionSetupQuestionResource toSave = new CompetitionSetupQuestionResource();
        toSave.setQuestionId(questionId);

        setupPutWithRestResultExpectations(questionRestURL + "/save", toSave);

        RestResult<Void> response = service.save(toSave);

        assertTrue(response.isSuccess());
    }

    @Test
    public void addDefaultToCompetition() {
        long competitionId = 1L;

        CompetitionSetupQuestionResource expected = new CompetitionSetupQuestionResource();
        setupPostWithRestResultExpectations(questionRestURL + "/add-default-to-competition/" + competitionId,
                CompetitionSetupQuestionResource.class, null, expected, CREATED);

        RestResult<CompetitionSetupQuestionResource> response = service.addDefaultToCompetition(competitionId);

        assertTrue(response.isSuccess());
    }

    @Test
    public void addResearchCategoryQuestionToCompetition() {
        long competitionId = 1L;

        setupPostWithRestResultExpectations(questionRestURL + "/add-research-category-question-to-competition/" +
                competitionId, CREATED);

        RestResult<Void> response = service.addResearchCategoryQuestionToCompetition(competitionId);

        assertTrue(response.isSuccess());
    }

    @Test
    public void deleteById() {
        long questionId = 1L;
        setupDeleteWithRestResultExpectations(questionRestURL + "/delete-by-id/" + questionId);

        RestResult<Void> response = service.deleteById(questionId);

        assertTrue(response.isSuccess());
    }
}
