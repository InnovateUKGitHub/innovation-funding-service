package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.longStatusMap;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.APPLICATION_FORM;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class QuestionSetupRestServiceTest extends BaseRestServiceUnitTest<QuestionSetupRestServiceImpl> {

    private static final String questionSetupRestURL = "/question/setup";

    @Override
    protected QuestionSetupRestServiceImpl registerRestServiceUnderTest() {
        return new QuestionSetupRestServiceImpl();
    }

    @Test
    public void testMarkQuestionSetupComplete() {
        final Long competitionId = 2L;
        final CompetitionSetupSection parentSection = APPLICATION_FORM;
        final Long questionId = 4L;
        setupPutWithRestResultExpectations(String.format("%s/markAsComplete/%d/%s/%d", questionSetupRestURL, competitionId, parentSection, questionId), Void.class, null, null);

        RestResult<Void> result = service.markQuestionSetupComplete(competitionId, parentSection, questionId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testMarkQuestionSetupIncomplete() {
        final Long competitionId = 2L;
        final CompetitionSetupSection parentSection = APPLICATION_FORM;
        final Long questionId = 4L;
        setupPutWithRestResultExpectations(String.format("%s/markAsIncomplete/%d/%s/%d", questionSetupRestURL, competitionId, parentSection, questionId), Void.class, null, null);

        RestResult<Void> result = service.markQuestionSetupIncomplete(competitionId, parentSection, questionId);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testGetQuestionStatuses() {
        Map<Long, Boolean> resultToReturn = asMap(232L, TRUE, 487L, FALSE);
        setupGetWithRestResultExpectations(questionSetupRestURL + "/getStatuses/1/APPLICATION_FORM", longStatusMap(), resultToReturn);

        Map<Long, Boolean> resultStatuses = service.getQuestionStatuses(1L, APPLICATION_FORM).getSuccessObject();

        assertNotNull(resultStatuses);
        Assert.assertEquals(resultToReturn, resultStatuses);
    }
}
