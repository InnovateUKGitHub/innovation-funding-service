package com.worth.ifs.application.service;

import com.worth.ifs.BaseRestServiceMocksTest;
import com.worth.ifs.application.domain.Question;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static com.worth.ifs.application.builder.QuestionBuilder.newQuestion;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;

public class QuestionRestServiceMocksTest extends BaseRestServiceMocksTest<QuestionRestServiceImpl> {

    private static final String questionRestURL = "/question";

    @Override
    protected QuestionRestServiceImpl registerRestServiceUnderTest(Consumer<QuestionRestServiceImpl> registrar) {
        QuestionRestServiceImpl questionRestService = new QuestionRestServiceImpl();
        questionRestService.questionRestURL = questionRestURL;
        registrar.accept(questionRestService);
        return questionRestService;
    }

    @Test
    public void test_assign() {
        String expectedUrl = dataServicesUrl + questionRestURL + "/assign/1/2/3/4";

        // now run the method under test
        service.assign(1L, 2L, 3L, 4L);

        // verify
        verify(mockRestTemplate).exchange(expectedUrl, PUT, httpEntityForRestCall(), Void.class);
    }

    @Test
    public void test_findByCompetition() {
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
    public void test_findById() {
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
    public void test_getMarkedAsComplete() {
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
    public void test_markAsComplete() {
        String expectedUrl = dataServicesUrl + questionRestURL + "/markAsComplete/1/2/3";

        service.markAsComplete(1L, 2L, 3L);
        verify(mockRestTemplate).exchange(expectedUrl, PUT, httpEntityForRestCall(), Void.class);
    }

    @Test
    public void test_markAsInComplete() {
        String expectedUrl = dataServicesUrl + questionRestURL + "/markAsInComplete/1/2/3";

        service.markAsInComplete(1L, 2L, 3L);
        verify(mockRestTemplate).exchange(expectedUrl, PUT, httpEntityForRestCall(), Void.class);
    }

    @Test
    public void test_updateNotification() {
        String expectedUrl = dataServicesUrl + questionRestURL + "/updateNotification/1/true";
        service.updateNotification(1L, true);
        verify(mockRestTemplate).exchange(expectedUrl, PUT, httpEntityForRestCall(), Void.class);
    }
}
