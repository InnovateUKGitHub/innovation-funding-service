package com.worth.ifs;

import com.worth.ifs.commons.service.BaseRestService;
import org.junit.Before;
import org.mockito.Mock;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import static com.worth.ifs.commons.security.TokenAuthenticationService.AUTH_TOKEN;
import static com.worth.ifs.commons.service.BaseRestService.getJSONHeaders;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.OK;

/**
 * This is the base class for testing REST services with mock components.  In addition to the standard mocks provided,
 * this base class also provides a dummy dataServiceUrl and a mock restTemplate for testing and stubbing the routes
 * that the REST services use to exchange data with the "data" layer.
 *
 * Created by dwatson on 02/10/15.
 */
public abstract class BaseRestServiceUnitTest<ServiceType extends BaseRestService> extends BaseUnitTestMocksTest {

    @Mock
    protected RestTemplate mockRestTemplate;

    @Mock
    protected AsyncRestTemplate mockAsyncRestTemplate;

    protected ServiceType service;

    protected abstract ServiceType registerRestServiceUnderTest();

    protected static String dataServicesUrl = "http://localhost/dummy";

    private static final String VALID_AUTH_TOKEN = "VALID_AUTH_TOKEN";

    @Before
    public void setupServiceWithMockTemplateAndSpringSecurity() {

        service = registerRestServiceUnderTest();
        service.setDataRestServiceUrl(dataServicesUrl);
        service.setRestTemplateSupplier(() -> mockRestTemplate);
        service.setAsyncRestTemplate(() -> mockAsyncRestTemplate);

        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new TestingAuthenticationToken("A_PRINCIPAL", VALID_AUTH_TOKEN));
        SecurityContextHolder.setContext(securityContext);
    }

    protected <T> HttpEntity<T> httpEntityForRestCall(T body) {
        HttpHeaders headers = getJSONHeaders();
        headers.set(AUTH_TOKEN, VALID_AUTH_TOKEN);
        return new HttpEntity<>(body, headers);
    }

    protected <T> HttpEntity<T> httpEntityForRestCallWithoutAuthToken(T body) {
        HttpHeaders headers = getJSONHeaders();
        return new HttpEntity<>(body, headers);
    }

    protected HttpEntity<String> httpEntityForRestCall() {
        return httpEntityForRestCall(null);
    }

    protected HttpEntity<String> httpEntityForRestGetWithoutAuthToken() {
        return httpEntityForRestCallWithoutAuthToken("");
    }

    protected <T> ResponseEntity<T> setupGetWithRestResultExpectations(String nonBaseUrl, Class<T> responseType, T responseBody) {
        return setupGetWithRestResultExpectations(nonBaseUrl, responseType, responseBody, OK);
    }

    protected <T> ResponseEntity<T> setupGetWithRestResultExpectations(String nonBaseUrl, Class<T> responseType, T responseBody, HttpStatus responseCode) {
        ResponseEntity<T> response = new ResponseEntity<>(responseBody, responseCode);
        when(mockRestTemplate.exchange(dataServicesUrl + nonBaseUrl, GET, httpEntityForRestCall(), responseType)).thenReturn(response);
        return response;
    }

    protected <T> ResponseEntity<T> setupGetWithRestResultExpectations(String nonBaseUrl, ParameterizedTypeReference<T> responseType, T responseBody) {
        return setupGetWithRestResultExpectations(nonBaseUrl, responseType, responseBody, OK);
    }

    protected <T> ResponseEntity<T> setupGetWithRestResultExpectations(String nonBaseUrl, ParameterizedTypeReference<T> responseType, T responseBody, HttpStatus responseCode) {
        ResponseEntity<T> response = new ResponseEntity<>(responseBody, responseCode);
        when(mockRestTemplate.exchange(dataServicesUrl + nonBaseUrl, GET, httpEntityForRestCall(), responseType)).thenReturn(response);
        return response;
    }

    protected <T> ResponseEntity<T> setupPostWithRestResultExpectations(String nonBaseUrl, Class<T> responseType, Object requestBody, T responseBody, HttpStatus responseCode) {
        ResponseEntity<T> response = new ResponseEntity<>(responseBody, responseCode);
        when(mockRestTemplate.exchange(dataServicesUrl + nonBaseUrl, POST, httpEntityForRestCall(requestBody), responseType)).thenReturn(response);
        return response;
    }

    protected <T> ResponseEntity<T> setupPostWithRestResultExpectations(String nonBaseUrl, ParameterizedTypeReference<T> responseType, Object requestBody, T responseBody, HttpStatus responseCode) {
        ResponseEntity<T> response = new ResponseEntity<>(responseBody, responseCode);
        when(mockRestTemplate.exchange(dataServicesUrl + nonBaseUrl, POST, httpEntityForRestCall(requestBody), responseType)).thenReturn(response);
        return response;
    }

    protected <T> ResponseEntity<T> setupPutWithRestResultExpectations(String nonBaseUrl, Class<T> responseType, Object requestBody, T responseBody) {
        return setupPutWithRestResultExpectations(nonBaseUrl, responseType, requestBody, responseBody, OK);
    }

    protected <T> ResponseEntity<T> setupPutWithRestResultExpectations(String nonBaseUrl, Class<T> responseType, Object requestBody, T responseBody, HttpStatus responseCode) {
        ResponseEntity<T> response = new ResponseEntity<>(responseBody, responseCode);
        when(mockRestTemplate.exchange(dataServicesUrl + nonBaseUrl, PUT, httpEntityForRestCall(requestBody), responseType)).thenReturn(response);
        return response;
    }

    protected <T> ResponseEntity<T> setupPutWithRestResultExpectations(String nonBaseUrl, ParameterizedTypeReference<T> responseType, Object requestBody, T responseBody, HttpStatus responseCode) {
        ResponseEntity<T> response = new ResponseEntity<>(responseBody, responseCode);
        when(mockRestTemplate.exchange(dataServicesUrl + nonBaseUrl, PUT, httpEntityForRestCall(requestBody), responseType)).thenReturn(response);
        return response;
    }
}