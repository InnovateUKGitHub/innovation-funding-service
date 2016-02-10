package com.worth.ifs.application.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.application.domain.Question;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.worth.ifs.application.builder.QuestionBuilder.newQuestion;
import static com.worth.ifs.application.service.Futures.settable;
import static com.worth.ifs.commons.service.ParameterizedTypeReferences.questionListType;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;

public class QuestionRestServiceMocksTest extends BaseRestServiceUnitTest<QuestionRestServiceImpl> {

    private static final String questionRestURL = "/question";


    @Override
    protected QuestionRestServiceImpl registerRestServiceUnderTest() {
        QuestionRestServiceImpl questionRestService = new QuestionRestServiceImpl();
        questionRestService.questionRestURL = questionRestURL;
        return questionRestService;
    }

    @Test
    public void assignTest() {

        setupPutWithRestResultExpectations(questionRestURL + "/assign/1/2/3/4", Void.class, null, null);

        // now run the method under test
        assertTrue(service.assign(1L, 2L, 3L, 4L).isSuccess());
    }

    @Test
    public void findByCompetitionTest() {

        List<Question> questions = newQuestion().build(3);
        setupGetWithRestResultExpectations(questionRestURL + "/findByCompetition/1", questionListType(), questions);

        // now run the method under test
        List<Question> returnedQuestions = service.findByCompetition(1L).getSuccessObject();

        // verify
        assertEquals(questions, returnedQuestions);
    }

    @Test
    public void findByIdTest() {

        Question question = newQuestion().build();
        setupGetWithRestResultExpectations(questionRestURL + "/id/1", Question.class, question);

        // now run the method under test
        Question returnedQuestion = service.findById(1L).getSuccessObject();

        // verify
        assertNotNull(returnedQuestion);
        assertEquals(question, returnedQuestion);
    }

    @Test
    public void getMarkedAsCompleteTest() throws Exception {
        String expectedUrl = dataServicesUrl + questionRestURL + "/getMarkedAsComplete/1/2";

        Long[] questionIds = new Long[]{3L, 4L, 5L};
        when(mockAsyncRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(""), Long[].class)).thenReturn(settable(new ResponseEntity<>(questionIds, HttpStatus.OK)));

        // now run the method under test
        Set<Long> returnedQuestionIds = service.getMarkedAsComplete(1L, 2L).get();

        // verify
        assertNotNull(questionIds);
        assertEquals(3, returnedQuestionIds.size());
        assertEquals(new HashSet<>(Arrays.asList(questionIds)), returnedQuestionIds);
    }

    @Test
    public void markAsCompleteTest() {
        setupPutWithRestResultExpectations(questionRestURL + "/markAsComplete/1/2/3", Void.class, null, null);
        assertTrue(service.markAsComplete(1L, 2L, 3L).isSuccess());
    }

    @Test
    public void markAsInCompleteTest() {
        setupPutWithRestResultExpectations(questionRestURL + "/markAsInComplete/1/2/3", Void.class, null, null);
        assertTrue(service.markAsInComplete(1L, 2L, 3L).isSuccess());
    }

    @Test
    public void updateNotificationTest() {
        setupPutWithRestResultExpectations(questionRestURL + "/updateNotification/1/true", Void.class, null, null);
        assertTrue(service.updateNotification(1L, true).isSuccess());
    }

    @Test
    public void getNextQuestionTest() {
        Question question = newQuestion().build();
        setupGetWithRestResultExpectations(questionRestURL + "/getNextQuestion/1", Question.class, question);

        Question nextQuestion = service.getNextQuestion(1L).getSuccessObject();
        assertEquals(question, nextQuestion);
    }

    @Test
    public void getPreviousQuestionTest() {
        Question question = newQuestion().build();
        setupGetWithRestResultExpectations(questionRestURL + "/getPreviousQuestion/2", Question.class, question);

        Question nextQuestion = service.getPreviousQuestion(2L).getSuccessObject();
        assertEquals(question, nextQuestion);
    }
}
