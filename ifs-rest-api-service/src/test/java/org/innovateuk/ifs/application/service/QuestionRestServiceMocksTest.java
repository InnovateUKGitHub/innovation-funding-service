package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.QuestionType;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.questionResourceListType;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.junit.Assert.*;

public class QuestionRestServiceMocksTest extends BaseRestServiceUnitTest<QuestionRestServiceImpl> {

    private static final String questionRestURL = "/question";


    @Override
    protected QuestionRestServiceImpl registerRestServiceUnderTest() {
        QuestionRestServiceImpl questionRestService = new QuestionRestServiceImpl();
        questionRestService.questionRestURL = questionRestURL;
        return questionRestService;
    }

    @Test
    public void findByCompetitionTest() {
        List<QuestionResource> questions = newQuestionResource().build(3);
        setupGetWithRestResultExpectations(questionRestURL + "/findByCompetition/1", questionResourceListType(), questions);
        // now run the method under test
        List<QuestionResource> returnedQuestions = service.findByCompetition(1L).getSuccess();
        // verify
        assertEquals(questions, returnedQuestions);
    }

    @Test
    public void findByIdTest() {

        QuestionResource question = new QuestionResource();
        setupGetWithRestResultExpectations(questionRestURL + "/id/1", QuestionResource.class, question);

        // now run the method under test
        QuestionResource returnedQuestion = service.findById(1L).getSuccess();

        // verify
        assertNotNull(returnedQuestion);
        Assert.assertEquals(question, returnedQuestion);
    }

    @Test
    public void getNextQuestionTest() {
        QuestionResource question = new QuestionResource();
        setupGetWithRestResultExpectations(questionRestURL + "/getNextQuestion/1", QuestionResource.class, question);

        QuestionResource nextQuestion = service.getNextQuestion(1L).getSuccess();
        Assert.assertEquals(question, nextQuestion);
    }

    @Test
    public void getPreviousQuestionTest() {
        QuestionResource question = new QuestionResource();
        setupGetWithRestResultExpectations(questionRestURL + "/getPreviousQuestion/2", QuestionResource.class, question);

        QuestionResource nextQuestion = service.getPreviousQuestion(2L).getSuccess();
        Assert.assertEquals(question, nextQuestion);
    }

    @Test
    public void getQuestionsBySectionIdAndTypeTest() {
        List<QuestionResource> questions = newQuestionResource().build(2);
        setupGetWithRestResultExpectations(questionRestURL + "/getQuestionsBySectionIdAndType/1/COST", new ParameterizedTypeReference<List<QuestionResource>>() {
        }, questions);

        List<QuestionResource> result = service.getQuestionsBySectionIdAndType(1L, QuestionType.COST).getSuccess();
        assertEquals(questions, result);
    }

    @Test
    public void save() {
        QuestionResource questionResource = new QuestionResource();
        setupPutWithRestResultExpectations(questionRestURL + "/", QuestionResource.class, questionResource, questionResource);

        QuestionResource result = service.save(questionResource).getSuccess();
        Assert.assertEquals(questionResource, result);
    }

    @Test
    public void getQuestionsByAssessment() {
        Long assessmentId = 1L;

        List<QuestionResource> questions = newQuestionResource().build(2);
        setupGetWithRestResultExpectations(questionRestURL + "/getQuestionsByAssessment/" + assessmentId, new ParameterizedTypeReference<List<QuestionResource>>() {
        }, questions);

        RestResult<List<QuestionResource>> result = service.getQuestionsByAssessment(assessmentId);
        assertTrue(result.isSuccess());
        Assert.assertEquals(questions, result.getSuccess());
    }

    @Test
    public void getQuestionByIdAndAssessmentId() {
        Long questionId = 1L;
        Long assessmentId = 2L;

        QuestionResource questionResource = newQuestionResource().build();
        setupGetWithRestResultExpectations(String.format("%s/getQuestionByIdAndAssessmentId/%s/%s", questionRestURL, questionId, assessmentId), QuestionResource.class, questionResource);

        QuestionResource result = service.getByIdAndAssessmentId(questionId, assessmentId).getSuccess();
        assertEquals(questionResource, result);
    }
}
