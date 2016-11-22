
package com.worth.ifs.competition.service;

import com.worth.ifs.*;
import com.worth.ifs.commons.rest.*;
import com.worth.ifs.competition.resource.*;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

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

        setupGetWithRestResultExpectations(competitionsRestURL + "/" + questionId, CompetitionSetupQuestionResource.class, expected);

        CompetitionSetupQuestionResource response = service.getByQuestionId(questionId).getSuccessObject();
        assertNotNull(response);
        Assert.assertEquals(expected, response);
    }

    @Test
    public void test_save() {
        long questionId = 1L;
        CompetitionSetupQuestionResource toSave = new CompetitionSetupQuestionResource();
        toSave.setQuestionId(questionId);
        setupPutWithRestResultExpectations(competitionsRestURL + "/" + questionId, toSave);

        RestResult<Void> response = service.save(toSave);

        setupPutWithRestResultVerifications(competitionsRestURL + "/" + questionId, Void.class, toSave);
        assertTrue(response.isSuccess());
    }
}
