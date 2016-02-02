package com.worth.ifs.application.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.application.domain.QuestionStatus;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.List;

import static com.worth.ifs.application.builder.QuestionStatusBuilder.newQuestionStatus;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;

public class QuestionStatusRestServiceMocksTest extends BaseRestServiceUnitTest<QuestionStatusRestServiceImpl> {
    private static final String questionStatusRestURL = "/questionStatus";

    @Override
    protected QuestionStatusRestServiceImpl registerRestServiceUnderTest() {
        QuestionStatusRestServiceImpl questionStatusRestService = new QuestionStatusRestServiceImpl();
        questionStatusRestService.questionStatusRestURL = questionStatusRestURL;
        return questionStatusRestService;
    }

    @Test
    public void findQuestionStatusesByQuestionAndApplicationIdTest() throws Exception {
        String expectedUrl = dataServicesUrl + questionStatusRestURL + "/findByQuestionAndAplication/1/2";

        QuestionStatus[] questionStatuses = newQuestionStatus().buildArray(3, QuestionStatus.class);
        ResponseEntity<QuestionStatus[]> response = new ResponseEntity<>(questionStatuses, HttpStatus.OK);
        ListenableFuture<ResponseEntity<QuestionStatus[]>> prePopFuture = new PrePopulatedListenableFuture<>(response);
        when(mockAsyncRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), QuestionStatus[].class)).thenReturn(prePopFuture);



        List<QuestionStatus> returnedQuestionStatuses = asList(service.findQuestionStatusesByQuestionAndApplicationId(1L, 2L).get().getBody());

        assertNotNull(returnedQuestionStatuses);
        assertEquals(3, returnedQuestionStatuses.size());
        assertEquals(questionStatuses[0], returnedQuestionStatuses.get(0));
        assertEquals(questionStatuses[1], returnedQuestionStatuses.get(1));
        assertEquals(questionStatuses[2], returnedQuestionStatuses.get(2));
    }
}
