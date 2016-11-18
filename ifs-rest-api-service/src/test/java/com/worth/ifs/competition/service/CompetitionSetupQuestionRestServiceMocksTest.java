
package com.worth.ifs.competition.service;

import com.worth.ifs.*;
import com.worth.ifs.commons.rest.*;
import com.worth.ifs.competition.resource.*;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static org.junit.Assert.*;

/**
 *
 */
public class CompetitionSetupQuestionRestServiceMocksTest extends BaseRestServiceUnitTest<CompetitionSetupQuestionRestServiceImpl> {

    private static final String competitionsRestURL = "/competitionSetupQuestion";

    @Override
    protected CompetitionSetupQuestionRestServiceImpl registerRestServiceUnderTest() {
        return new CompetitionSetupQuestionRestServiceImpl();
    }

    @Test
    public void test_getByQuestionId() {
        long questionId = 1L;
        CompetitionSetupQuestionResource expected = new CompetitionSetupQuestionResource();

        setupGetWithRestResultExpectations(competitionsRestURL + "/" +questionId, CompetitionSetupQuestionResource.class, expected);

        CompetitionSetupQuestionResource response = service.getByQuestionId(questionId).getSuccessObject();
        assertNotNull(response);
        Assert.assertEquals(expected, response);
    }

    @Test
    public void test_save() {
        long questionId = 1L;
        CompetitionSetupQuestionResource toSave = new CompetitionSetupQuestionResource();

        setupPutWithRestResultExpectations(competitionsRestURL + "/" +questionId, toSave, HttpStatus.OK);

        RestResult<Void> response = service.save(toSave);
        assertTrue(response.isSuccess());
    }
}
