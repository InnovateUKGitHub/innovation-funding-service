package com.worth.ifs.transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseWebIntegrationTest;
import com.worth.ifs.commons.rest.RestErrorEnvelope;
import com.worth.ifs.user.domain.ProcessRole;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static com.worth.ifs.commons.error.Errors.notFoundError;
import static com.worth.ifs.commons.security.TokenAuthenticationService.AUTH_TOKEN;
import static com.worth.ifs.commons.service.BaseRestService.getJSONHeaders;
import static com.worth.ifs.user.domain.UserRoleType.ASSESSOR;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.junit.Assert.*;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

/**
 *
 */
public class RestResultHandlingHttpMessageConverterIntegrationTest extends BaseWebIntegrationTest {

    @Value("${ifs.data.service.rest.baseURL}")
    private String dataUrl;

    @Test
    public void testSuccessRestResultHandled() {

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
    public void testFailureRestResultHandled() throws IOException {

        RestTemplate restTemplate = new RestTemplate();

        try {

            String url = dataUrl + "/response/saveQuestionResponse/25/assessorFeedback?assessorUserId=9999&feedbackText=Nicework";
            restTemplate.exchange(url, PUT, headersEntity(), String.class);
            fail("Should have had a Not Found on the server side, as a non-existent user id was specified");

        } catch (HttpClientErrorException | HttpServerErrorException e) {

            assertEquals(NOT_FOUND, e.getStatusCode());
            RestErrorEnvelope restErrorEnvelope = new ObjectMapper().readValue(e.getResponseBodyAsString(), RestErrorEnvelope.class);
            assertTrue(restErrorEnvelope.is(notFoundError(ProcessRole.class, ASSESSOR.getName())));
        }
    }


    private <T> HttpEntity<T> headersEntity(){
        HttpHeaders headers = getJSONHeaders();
        headers.set(AUTH_TOKEN, "789ghi");
        return new HttpEntity<>(headers);
    }

    private <T> HttpEntity<T> jsonEntity(T entity){
        HttpHeaders headers = getJSONHeaders();
        headers.set(AUTH_TOKEN, "123abc");
        return new HttpEntity<>(entity, headers);
    }
}
