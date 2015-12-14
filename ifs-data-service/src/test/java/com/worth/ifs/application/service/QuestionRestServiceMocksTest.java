package com.worth.ifs.application.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.domain.Section;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.worth.ifs.application.builder.QuestionBuilder.newQuestion;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;

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
        String expectedUrl = dataServicesUrl + questionRestURL + "/assign/1/2/3/4";

        // now run the method under test
        service.assign(1L, 2L, 3L, 4L);

        // verify
        verify(mockRestTemplate).exchange(expectedUrl, PUT, httpEntityForRestCall(), Void.class);
    }

    @Test
    public void findByCompetitionTest() {
        String expectedUrl = dataServicesUrl + questionRestURL + "/findByCompetition/1";

        Question[] questions = newQuestion().buildArray(3, Question.class);
        ResponseEntity<Question[]> response = new ResponseEntity<>(questions, HttpStatus.OK);
        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), Question[].class)).thenReturn(response);

        // now run the method under test
        List<Question> returnedQuestions = service.findByCompetition(1L);

        // verify
        assertNotNull(returnedQuestions);
        assertEquals(3, returnedQuestions.size());
        assertEquals(questions[0], returnedQuestions.get(0));
        assertEquals(questions[1], returnedQuestions.get(1));
        assertEquals(questions[2], returnedQuestions.get(2));
    }

    @Test
    public void indByIdTest() {
        String expectedUrl = dataServicesUrl + questionRestURL + "/id/1";

        Question question = newQuestion().build();
        ResponseEntity<Question> response = new ResponseEntity<>(question, HttpStatus.OK);
        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), Question.class)).thenReturn(response);

        // now run the method under test
        Question returnedQuestion = service.findById(1L);

        // verify
        assertNotNull(returnedQuestion);
        assertEquals(question, returnedQuestion);
    }

    @Test
    public void getMarkedAsCompleteTest() {
        String expectedUrl = dataServicesUrl + questionRestURL + "/getMarkedAsComplete/1/2";

        Long[] questionIds = new Long[]{3L, 4L, 5L};
        ResponseEntity<Long[]> response = new ResponseEntity<>(questionIds, HttpStatus.OK);
        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), Long[].class)).thenReturn(response);

        // now run the method under test
        Set<Long> returnedQuestionIds = service.getMarkedAsComplete(1L, 2L);

        // verify
        assertNotNull(questionIds);
        assertEquals(3, returnedQuestionIds.size());
        assertEquals(new HashSet<>(Arrays.asList(questionIds)), returnedQuestionIds);
    }

    @Test
    public void markAsCompleteTest() {
        String expectedUrl = dataServicesUrl + questionRestURL + "/markAsComplete/1/2/3";

        service.markAsComplete(1L, 2L, 3L);
        verify(mockRestTemplate).exchange(expectedUrl, PUT, httpEntityForRestCall(), Void.class);
    }

    @Test
    public void markAsInCompleteTest() {
        String expectedUrl = dataServicesUrl + questionRestURL + "/markAsInComplete/1/2/3";

        service.markAsInComplete(1L, 2L, 3L);
        verify(mockRestTemplate).exchange(expectedUrl, PUT, httpEntityForRestCall(), Void.class);
    }

    @Test
    public void updateNotificationTest() {
        String expectedUrl = dataServicesUrl + questionRestURL + "/updateNotification/1/true";
        service.updateNotification(1L, true);
        verify(mockRestTemplate).exchange(expectedUrl, PUT, httpEntityForRestCall(), Void.class);
    }

    @Test
    public void getNextQuestionTest() {
        String expectedUrl = dataServicesUrl + questionRestURL + "/getNextQuestion/1";
        Question question = newQuestion().build();

        ResponseEntity<Question> response = new ResponseEntity<>(question, HttpStatus.OK);
        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), Question.class)).thenReturn(response);

        Question nextQuestion = service.getNextQuestion(1L);
        assertEquals(question, nextQuestion);
    }

    @Test
    public void getPreviousQuestionTest() {
        String expectedUrl = dataServicesUrl + questionRestURL + "/getPreviousQuestion/2";
        Question question = newQuestion().build();

        ResponseEntity<Question> response = new ResponseEntity<>(question, HttpStatus.OK);
        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), Question.class)).thenReturn(response);

        Question nextQuestion = service.getPreviousQuestion(2L);
        assertEquals(question, nextQuestion);
    }
}
