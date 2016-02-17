package com.worth.ifs.transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseWebIntegrationTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.service.ApplicationRestService;
import com.worth.ifs.commons.rest.RestErrorResponse;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.security.UserAuthenticationService;
import com.worth.ifs.security.SecuritySetter;
import com.worth.ifs.user.domain.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.concurrent.Future;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.security.TokenAuthenticationService.AUTH_TOKEN;
import static com.worth.ifs.commons.service.RestTemplateAdaptor.getJSONHeaders;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.junit.Assert.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

/**
 * Tests for the {@link com.worth.ifs.rest.RestResultHandlingHttpMessageConverter}, to assert that it can take successful
 * RestResults from Controllers and convert them into the "body" of the RestResult, and that it can take failing RestResults
 * and convert them into {@link RestErrorResponse} objects.
 */
public class RestResultHandlingHttpMessageConverterIntegrationTest extends BaseWebIntegrationTest {

    @Value("${ifs.data.service.rest.baseURL}")
    private String dataUrl;

    @Autowired
    public ApplicationRestService applicationRestService;

    @Autowired
    public UserAuthenticationService userAuthenticationService;

    @Test
    public void testSuccessRestResultHandledAsTheBodyOfTheRestResult() {

        RestTemplate restTemplate = new RestTemplate();

        try {
            String url = dataUrl + "/response/saveQuestionResponse/25/assessorFeedback?assessorUserId=3&feedbackText=Nicework";
            ResponseEntity<String> response = restTemplate.exchange(url, PUT, headersEntity(), String.class);
            assertEquals(OK, response.getStatusCode());
            assertTrue(isBlank(response.getBody()));
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            fail("Should have handled the request and response ok, but got exception - " + e);
        }
    }

    @Test
    public void testFailureRestResultHandledAsARestErrorResponse() throws IOException {

        RestTemplate restTemplate = new RestTemplate();

        try {

            String url = dataUrl + "/application/normal/9999";
            restTemplate.exchange(url, GET, headersEntity(), String.class);
            fail("Should have had a Not Found on the server side, as a non-existent id was specified");

        } catch (HttpClientErrorException | HttpServerErrorException e) {

            assertEquals(NOT_FOUND, e.getStatusCode());
            RestErrorResponse restErrorResponse = new ObjectMapper().readValue(e.getResponseBodyAsString(), RestErrorResponse.class);
            assertTrue(restErrorResponse.is(notFoundError(Application.class, 9999L)));
        }
    }

    @Test
    public void testFailureRestResultHandledAsync() throws Exception {
        final User initial = SecuritySetter.swapOutForUser(new User("","","", "123abc", "", emptyList()));
        try {
            final long applicationIdThatDoesNotExist = -1L;
            final Future<RestResult<Double>> completeQuestionsPercentage = applicationRestService.getCompleteQuestionsPercentage(applicationIdThatDoesNotExist);
            // We have set the future going but now we need to call it. This call should not throw
            final RestResult<Double> doubleRestResult = completeQuestionsPercentage.get();
            assertTrue(doubleRestResult.isFailure());
            assertEquals(HttpStatus.NOT_FOUND, doubleRestResult.getStatusCode());
        }
        finally {
            SecuritySetter.swapOutForUser(initial);
        }
    }


    private <T> HttpEntity<T> headersEntity(){
        HttpHeaders headers = getJSONHeaders();
        headers.set(AUTH_TOKEN, "789ghi");
        return new HttpEntity<>(headers);
    }
}
