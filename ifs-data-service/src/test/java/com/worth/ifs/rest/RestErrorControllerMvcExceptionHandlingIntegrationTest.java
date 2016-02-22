package com.worth.ifs.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseWebIntegrationTest;
import com.worth.ifs.commons.error.CommonErrors;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.RestErrorResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import static com.worth.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static com.worth.ifs.commons.security.TokenAuthenticationService.AUTH_TOKEN;
import static com.worth.ifs.commons.service.RestTemplateAdaptor.getJSONHeaders;
import static org.junit.Assert.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * This test tests that the {@link RestErrorController} is able to take low-level errors produced by Spring MVC and Spring Security
 * prior to any Controller code actually being called, and convert them into RestErrorResponses.
 */
public class RestErrorControllerMvcExceptionHandlingIntegrationTest extends BaseWebIntegrationTest {

    @Value("${ifs.data.service.rest.baseURL}")
    private String dataUrl;

    @Test
    public void testIncorrectUrl() throws Exception {

        RestTemplate restTemplate = new RestTemplate();

        try {

            String url = dataUrl + "/non/existent/url";
            restTemplate.exchange(url, GET, headersEntity(), String.class);
            fail("Should have had a Not Found on the server side, as a non-handled URL was specified");

        } catch (HttpClientErrorException | HttpServerErrorException e) {

            assertEquals(NOT_FOUND, e.getStatusCode());
            RestErrorResponse restErrorResponse = new ObjectMapper().readValue(e.getResponseBodyAsString(), RestErrorResponse.class);
            assertTrue(restErrorResponse.is(new Error(GENERAL_NOT_FOUND.getErrorKey(), "The requested URL could not be found.", NOT_FOUND)));
        }
    }

    @Test
    public void testAccessDenied() throws Exception {

        RestTemplate restTemplate = new RestTemplate();

        try {

            String url = dataUrl + "/application/normal/1";
            restTemplate.exchange(url, GET, new HttpEntity<>(new HttpHeaders()), String.class);
            fail("Should have had a Forbidden on the server side, as we are not specifying a user token to this restricted resource");

        } catch (HttpClientErrorException | HttpServerErrorException e) {

            assertEquals(FORBIDDEN, e.getStatusCode());
            RestErrorResponse restErrorResponse = new ObjectMapper().readValue(e.getResponseBodyAsString(), RestErrorResponse.class);
            assertTrue(restErrorResponse.is(CommonErrors.forbiddenError("You do not have permission to access the requested URL.")));
        }
    }

    private <T> HttpEntity<T> headersEntity(){
        HttpHeaders headers = getJSONHeaders();
        headers.set(AUTH_TOKEN, "789ghi");
        return new HttpEntity<>(headers);
    }
}
