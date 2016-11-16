
package com.worth.ifs.competition.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.application.resource.CompetitionSetupQuestionResource;
import com.worth.ifs.commons.rest.RestResult;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static com.worth.ifs.competition.builder.CompetitionSetupQuestionResourceBuilder.newCompetitionSetupQuestionResource;
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
        CompetitionSetupQuestionResource expected = newCompetitionSetupQuestionResource().withQuestionId(questionId).build();

        setupGetWithRestResultExpectations(competitionsRestURL + "/" +questionId, CompetitionSetupQuestionResource.class, expected);

        CompetitionSetupQuestionResource response = service.getByQuestionId(questionId).getSuccessObject();
        assertNotNull(response);
        assertEquals(expected, response);
    }

    @Test
    public void test_save() {
        long questionId = 1L;
        CompetitionSetupQuestionResource toSave = newCompetitionSetupQuestionResource().withQuestionId(questionId).build();

        setupPutWithRestResultExpectations(competitionsRestURL + "/" +questionId, toSave, HttpStatus.OK);

        RestResult<Void> response = service.save(toSave);
        assertTrue(response.isSuccess());
    }
}
