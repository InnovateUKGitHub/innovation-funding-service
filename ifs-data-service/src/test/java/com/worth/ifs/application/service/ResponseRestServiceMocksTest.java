package com.worth.ifs.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.application.resource.ResponseResource;
import com.worth.ifs.commons.error.CommonErrors;
import com.worth.ifs.commons.rest.RestErrorResponse;
import com.worth.ifs.commons.rest.RestResult;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;
import java.util.Optional;

import static com.worth.ifs.application.builder.ResponseResourceBuilder.newResponseResource;
import static com.worth.ifs.commons.service.ParameterizedTypeReferences.responseResourceListType;
import static java.nio.charset.Charset.defaultCharset;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

public class ResponseRestServiceMocksTest extends BaseRestServiceUnitTest<ResponseRestServiceImpl> {

    private static final String responseRestURL = "/response";


    @Override
    protected ResponseRestServiceImpl registerRestServiceUnderTest() {
        ResponseRestServiceImpl responseRestService = new ResponseRestServiceImpl();
        responseRestService.responseRestURL = responseRestURL;
        return responseRestService;
    }

    @Test
    public void test_getResponsesByApplicationId() {

        List<ResponseResource> responses = newResponseResource().build(3);
        setupGetWithRestResultExpectations(responseRestURL + "/findResponsesByApplication/1", responseResourceListType(), responses);

        // now run the method under test
        List<ResponseResource> returnedResponses = service.getResponsesByApplicationId(1L).getSuccessObject();

        // verify
        assertEquals(responses, returnedResponses);

    }

    @Test
    public void testSaveQuestionResponseAssessorFeedback() throws JsonProcessingException {
        String expectedUrl = dataServicesUrl + responseRestURL +
                "/saveQuestionResponse/1/assessorFeedback?assessorUserId=2&feedbackValue=value&feedbackText=text";

        ResponseEntity<Void> response = new ResponseEntity<>(OK);
        when(mockRestTemplate.exchange(expectedUrl, PUT, httpEntityForRestCall(), Void.class)).thenReturn(response);

        // now run the method under test
        RestResult<Void> success = service.saveQuestionResponseAssessorFeedback(2L, 1L, Optional.of("value"), Optional.of("text"));

        // verify
        assertTrue(success.isSuccess());
    }

    @Test
    public void testSaveQuestionResponseAssessorFeedbackButRestErrorEnvelopeReturned() throws JsonProcessingException {

        String expectedUrl = dataServicesUrl + responseRestURL +
                "/saveQuestionResponse/1/assessorFeedback?assessorUserId=2&feedbackValue=value&feedbackText=text";

        RestErrorResponse restErrorResponse = new RestErrorResponse(asList(CommonErrors.badRequestError("Bad!"), CommonErrors.internalServerErrorError("Bang!")));
        when(mockRestTemplate.exchange(expectedUrl, PUT, httpEntityForRestCall(), Void.class)).thenThrow(new HttpServerErrorException(BAD_REQUEST, "Bad!", toJsonBytes(restErrorResponse), defaultCharset()));

        // now run the method under test
        RestResult<Void> failure = service.saveQuestionResponseAssessorFeedback(2L, 1L, Optional.of("value"), Optional.of("text"));

        // verify
        assertTrue(failure.isFailure());
        assertEquals(BAD_REQUEST, failure.getStatusCode());
    }

    private String toJson(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

    private byte[] toJsonBytes(Object object) throws JsonProcessingException {
        return toJson(object).getBytes();
    }
//
//    @Test
//    public void test_findById() {
//        String expectedUrl = dataServicesUrl + questionRestURL + "/id/1";
//
//        Question question = newQuestion().build();
//        ResponseEntity<Question> response = new ResponseEntity<>(question, HttpStatus.OK);
//        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), Question.class)).thenReturn(response);
//
//        // now run the method under test
//        Question returnedQuestion = service.findById(1L);
//
//        // verify
//        assertNotNull(returnedQuestion);
//        assertEquals(question, returnedQuestion);
//    }
//
//    @Test
//    public void test_getMarkedAsComplete() {
//        String expectedUrl = dataServicesUrl + questionRestURL + "/getMarkedAsComplete/1/2";
//
//        Long[] questionIds = new Long[]{3L, 4L, 5L};
//        ResponseEntity<Long[]> response = new ResponseEntity<>(questionIds, HttpStatus.OK);
//        when(mockRestTemplate.exchange(expectedUrl, GET, httpEntityForRestCall(), Long[].class)).thenReturn(response);
//
//        // now run the method under test
//        Set<Long> returnedQuestionIds = service.getMarkedAsComplete(1L, 2L);
//
//        // verify
//        assertNotNull(questionIds);
//        assertEquals(3, returnedQuestionIds.size());
//        assertEquals(new HashSet<>(Arrays.asList(questionIds)), returnedQuestionIds);
//    }
//
//    @Test
//    public void test_markAsComplete() {
//        String expectedUrl = dataServicesUrl + questionRestURL + "/markAsComplete/1/2/3";
//
//        service.markAsComplete(1L, 2L, 3L);
//        verify(mockRestTemplate).exchange(expectedUrl, PUT, httpEntityForRestCall(), Void.class);
//    }
//
//    @Test
//    public void test_markAsInComplete() {
//        String expectedUrl = dataServicesUrl + questionRestURL + "/markAsInComplete/1/2/3";
//
//        service.markAsInComplete(1L, 2L, 3L);
//        verify(mockRestTemplate).exchange(expectedUrl, PUT, httpEntityForRestCall(), Void.class);
//    }
//
//    @Test
//    public void test_updateNotification() {
//        String expectedUrl = dataServicesUrl + questionRestURL + "/updateNotification/1/true";
//        service.updateNotification(1L, true);
//        verify(mockRestTemplate).exchange(expectedUrl, PUT, httpEntityForRestCall(), Void.class);
//    }
}
