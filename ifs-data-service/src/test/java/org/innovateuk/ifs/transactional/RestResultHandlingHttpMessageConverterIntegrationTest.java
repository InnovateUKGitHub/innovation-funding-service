package org.innovateuk.ifs.transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
//import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestErrorResponse;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.SecuritySetter;
import org.innovateuk.ifs.commons.security.authentication.token.Authentication;
import org.innovateuk.ifs.commons.service.HttpHeadersUtils;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.concurrent.Future;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION;
import static org.junit.Assert.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;

/**
 * Tests for the {org.innovateuk.ifs.rest.RestResultHandlingHttpMessageConverter}, to assert that it can take successful
 * RestResults from Controllers and convert them into the "body" of the RestResult, and that it can take failing RestResults
 * and convert them into {@link RestErrorResponse} objects.
 */
public class RestResultHandlingHttpMessageConverterIntegrationTest extends BaseIntegrationTest {


    @Test
    public void testSuccessRestResultHandledAsTheBodyOfTheRestResult() {

        RestTemplate restTemplate = new RestTemplate();
        String dataUrl = "http://localhost:" + port;

        final long applicationId = 1L;
        try {
            final String url = String.format("%s/competition/%s", dataUrl, applicationId);
            ResponseEntity<CompetitionResource> response = restTemplate.exchange(url,
                    GET, leadApplicantHeadersEntity(), CompetitionResource.class);
            assertEquals(OK, response.getStatusCode());
            assertNotNull(response.getBody());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            fail("Should have handled the request and response ok, but got exception - " + e);
        }
    }

    @Test
    public void testFailureRestResultHandledAsARestErrorResponse() throws IOException {

        RestTemplate restTemplate = new RestTemplate();
        try {
            String url = "http://localhost:" + port + "/application/9999";
            restTemplate.exchange(url, GET, leadApplicantHeadersEntity(), String.class);
            fail("Should have had a Not Found on the server side, as a non-existent id was specified");
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            assertEquals(FORBIDDEN, e.getStatusCode());
            RestErrorResponse restErrorResponse = new ObjectMapper().readValue(e.getResponseBodyAsString(), RestErrorResponse.class);
            Error expectedError = new Error(GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION.name(), null);
            RestErrorResponse expectedResponse = new RestErrorResponse(expectedError);
            Assert.assertEquals(expectedResponse, restErrorResponse);
        }
    }

    @Test
    @Ignore
    public void testFailureRestResultHandledAsync() throws Exception {
        final UserResource initial = SecuritySetter.swapOutForUser(leadApplicantUser());
        try {
            final long applicationIdThatDoesNotExist = -1L;
            final Future<RestResult<Double>> completeQuestionsPercentage
                    = null;//applicationRestService.getCompleteQuestionsPercentage(applicationIdThatDoesNotExist);
            // We have set the future going but now we need to call it. This call should not throw
            final RestResult<Double> doubleRestResult = completeQuestionsPercentage.get();
            assertTrue(doubleRestResult.isFailure());
            Assert.assertEquals(FORBIDDEN, doubleRestResult.getStatusCode());
        } finally {
            SecuritySetter.swapOutForUser(initial);
        }
    }

    private <T> HttpEntity<T> leadApplicantHeadersEntity() {
        return getUserJSONHeaders(leadApplicantUser());
    }

    private <T> HttpEntity<T> getUserJSONHeaders(UserResource user) {
        HttpHeaders headers = HttpHeadersUtils.getJSONHeaders();
        headers.set(Authentication.TOKEN, user.getUid());
        return new HttpEntity<>(headers);
    }

    private UserResource leadApplicantUser() {
        return SecuritySetter.basicSecurityUser;
    }

}
