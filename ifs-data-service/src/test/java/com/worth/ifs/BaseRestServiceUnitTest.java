package com.worth.ifs;

import com.worth.ifs.commons.service.AnonymousUserRestTemplateAdaptor;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.commons.service.RestTemplateAdaptor;
import org.junit.Before;
import org.mockito.Mock;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutionException;

import static com.worth.ifs.commons.security.UidAuthenticationService.AUTH_TOKEN;
import static com.worth.ifs.commons.service.HttpHeadersUtils.getJSONHeaders;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

/**
 * This is the base class for testing REST services with mock components.  In addition to the standard mocks provided,
 * this base class also provides a dummy dataServiceUrl and a mock restTemplate for testing and stubbing the routes
 * that the REST services use to exchange data with the "data" layer.
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

    private static final String ANONYMOUS_AUTH_TOKEN = "ANONYMOUS_AUTH_TOKEN";

    @Before
    public void setupServiceWithMockTemplateAndSpringSecurity() {

        service = registerRestServiceUnderTest();
        service.setDataRestServiceUrl(dataServicesUrl);

        RestTemplateAdaptor adaptor = new RestTemplateAdaptor();
        adaptor.setRestTemplate(mockRestTemplate);
        adaptor.setAsyncRestTemplate(mockAsyncRestTemplate);

        AnonymousUserRestTemplateAdaptor anonymousUserRestTemplateAdaptor = new AnonymousUserRestTemplateAdaptor();
        ReflectionTestUtils.setField(anonymousUserRestTemplateAdaptor, "ifsWebSystemUserUid", ANONYMOUS_AUTH_TOKEN);
        anonymousUserRestTemplateAdaptor.setRestTemplate(mockRestTemplate);
        anonymousUserRestTemplateAdaptor.setAsyncRestTemplate(mockAsyncRestTemplate);

        service.setRestTemplateAdaptor(adaptor);
        service.setAnonymousRestTemplateAdaptor(anonymousUserRestTemplateAdaptor);
        setLoggedInUser(VALID_AUTH_TOKEN);
    }

    private <T> ListenableFuture<ResponseEntity<T>> getAsyncResponseFromEntity(ResponseEntity<T> response) {
        ListenableFuture<ResponseEntity<T>> asyncResponse = mock(ListenableFuture.class);
        try {
            when(asyncResponse.get()).thenReturn(response);
        } catch (InterruptedException | ExecutionException e) {
            //won't ever get thrown as this is just a mock.
        }
        return asyncResponse;
    }

    protected void setLoggedInUser(String authToken) {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        SecurityContextHolder.setContext(securityContext);

        if (authToken != null) {
            securityContext.setAuthentication(new TestingAuthenticationToken("A_PRINCIPAL", authToken));
        } else {
            securityContext.setAuthentication(null);
        }
    }

    protected <T> HttpEntity<T> httpEntityForRestCall(T body) {
        HttpHeaders headers = getJSONHeaders();
        headers.set(AUTH_TOKEN, VALID_AUTH_TOKEN);
        return new HttpEntity<>(body, headers);
    }

    protected <T> HttpEntity<T> httpEntityForRestCallAnonymous(T body) {
        HttpHeaders headers = getJSONHeaders();
        headers.set(AUTH_TOKEN, ANONYMOUS_AUTH_TOKEN);
        return new HttpEntity<>(body, headers);
    }

    protected <T> HttpEntity<T> httpEntityForRestCallWithoutAuthToken(T body) {
        HttpHeaders headers = getJSONHeaders();
        return new HttpEntity<>(body, headers);
    }

    protected HttpEntity<byte[]> httpEntityForRestCallWithFileUpload(String requestBody, String contentType, long filesizeBytes) {
        HttpHeaders headers = getJSONHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(filesizeBytes);
        headers.set(AUTH_TOKEN, VALID_AUTH_TOKEN);
        return new HttpEntity<>(requestBody.getBytes(), headers);
    }

    protected HttpEntity<String> httpEntityForRestCall() {
        return httpEntityForRestCall(null);
    }

    protected HttpEntity<String> httpEntityForRestCallAnonymous() {
        return httpEntityForRestCallAnonymous(null);
    }

    protected HttpEntity<String> httpEntityForRestGetWithoutAuthToken() {
        return httpEntityForRestCallWithoutAuthToken("");
    }

    protected <T> ResponseEntity<T> setupGetWithRestResultAsyncExpectations(String nonBaseUrl, Class<T> responseType, T responseBody) {
        return setupGetWithRestResultAsyncExpectations(nonBaseUrl, responseType, responseBody, OK);
    }

    protected <T> ResponseEntity<T> setupGetWithRestResultAsyncExpectations(String nonBaseUrl, Class<T> responseType, T responseBody, HttpStatus responseCode) {
        ResponseEntity<T> response = new ResponseEntity<>(responseBody, responseCode);
        ListenableFuture<ResponseEntity<T>> asyncResponse = getAsyncResponseFromEntity(response);
        when(mockAsyncRestTemplate.exchange(dataServicesUrl + nonBaseUrl, GET, httpEntityForRestCall(), responseType)).thenReturn(asyncResponse);
        return response;
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

    protected <T> ResponseEntity<T> setupGetWithRestResultAnonymousExpectations(String nonBaseUrl, ParameterizedTypeReference<T> responseType, T responseBody, HttpStatus responseCode) {
        ResponseEntity<T> response = new ResponseEntity<>(responseBody, responseCode);
        when(mockRestTemplate.exchange(dataServicesUrl + nonBaseUrl, GET, httpEntityForRestCallAnonymous(), responseType)).thenReturn(response);
        return response;
    }

    protected ResponseEntity<Void> setupDeleteWithRestResultExpectations(String nonBaseUrl) {
        return setupDeleteWithRestResultExpectations(nonBaseUrl, NO_CONTENT);
    }

    protected ResponseEntity<Void> setupDeleteWithRestResultExpectations(String nonBaseUrl, HttpStatus responseCode) {
        ResponseEntity<Void> response = new ResponseEntity<>(responseCode);
        when(mockRestTemplate.exchange(dataServicesUrl + nonBaseUrl, DELETE, httpEntityForRestCall(), Void.class)).thenReturn(response);
        return response;
    }

    protected <T> ResponseEntity<T> setupPostWithRestResultExpectations(String nonBaseUrl, Class<T> responseType, Object requestBody, T responseBody, HttpStatus responseCode) {
        ResponseEntity<T> response = new ResponseEntity<>(responseBody, responseCode);
        when(mockRestTemplate.exchange(dataServicesUrl + nonBaseUrl, POST, httpEntityForRestCall(requestBody), responseType)).thenReturn(response);
        return response;
    }

    protected <T> ResponseEntity<T> setupFileUploadWithRestResultExpectations(String nonBaseUrl, Class<T> responseType, String requestBody, String mediaType, long filesizeBytes, T responseBody, HttpStatus responseCode) {
        ResponseEntity<T> response = new ResponseEntity<>(responseBody, responseCode);
        when(mockRestTemplate.exchange(dataServicesUrl + nonBaseUrl, POST, httpEntityForRestCallWithFileUpload(requestBody, mediaType, filesizeBytes), responseType)).thenReturn(response);
        return response;
    }

    protected <T> ResponseEntity<T> setupPostWithRestResultAnonymousExpectations(String nonBaseUrl, Class<T> responseType, Object requestBody, T responseBody, HttpStatus responseCode) {
        ResponseEntity<T> response = new ResponseEntity<>(responseBody, responseCode);
        when(mockRestTemplate.exchange(dataServicesUrl + nonBaseUrl, POST, httpEntityForRestCallAnonymous(requestBody), responseType)).thenReturn(response);
        return response;
    }

    protected <T> ResponseEntity<T> setupPostWithRestResultExpectations(String nonBaseUrl, ParameterizedTypeReference<T> responseType, Object requestBody, T responseBody) {
        return setupPostWithRestResultExpectations(nonBaseUrl, responseType, requestBody, responseBody, HttpStatus.OK);
    }

    protected <T> ResponseEntity<T> setupPostWithRestResultExpectations(String nonBaseUrl, ParameterizedTypeReference<T> responseType, Object requestBody, T responseBody, HttpStatus responseCode) {
        ResponseEntity<T> response = new ResponseEntity<>(responseBody, responseCode);
        when(mockRestTemplate.exchange(dataServicesUrl + nonBaseUrl, POST, httpEntityForRestCall(requestBody), responseType)).thenReturn(response);
        return response;
    }

    protected ResponseEntity<Void> setupPostWithRestResultExpectations(String nonBaseUrl, Object requestBody, HttpStatus responseCode) {
        ResponseEntity<Void> response = new ResponseEntity<>(responseCode);
        when(mockRestTemplate.exchange(dataServicesUrl + nonBaseUrl, POST, httpEntityForRestCall(requestBody), Void.class)).thenReturn(response);
        return response;
    }

    protected ResponseEntity<Void> setupPostWithRestResultExpectations(String nonBaseUrl, HttpStatus responseCode) {
        ResponseEntity<Void> response = new ResponseEntity<>(responseCode);
        when(mockRestTemplate.exchange(dataServicesUrl + nonBaseUrl, POST, httpEntityForRestCall(), Void.class)).thenReturn(response);
        return response;
    }

    protected <T> void setupPostWithRestResultVerifications(String nonBaseUrl, Class<T> responseType, Object requestBody) {
        verify(mockRestTemplate).exchange(dataServicesUrl + nonBaseUrl, POST, httpEntityForRestCall(requestBody), responseType);
    }

    protected <T> void setupPostWithRestResulVerifications(String nonBaseUrl, ParameterizedTypeReference<T> responseType, Object requestBody) {
        verify(mockRestTemplate).exchange(dataServicesUrl + nonBaseUrl, POST, httpEntityForRestCall(requestBody), responseType);
    }

    protected ResponseEntity<Void> setupPutWithRestResultExpectations(String nonBaseUrl, Object requestBody) {
        return setupPutWithRestResultExpectations(nonBaseUrl, requestBody, OK);
    }

    protected ResponseEntity<Void> setupPutWithRestResultExpectations(String nonBaseUrl, Object requestBody, HttpStatus responseCode) {
        ResponseEntity<Void> response = new ResponseEntity<>(responseCode);
        when(mockRestTemplate.exchange(dataServicesUrl + nonBaseUrl, PUT, httpEntityForRestCall(requestBody), Void.class)).thenReturn(response);
        return response;
    }

    protected ResponseEntity<Void> setupPutWithRestResultAnonymousExpectations(String nonBaseUrl, Object requestBody, HttpStatus responseCode) {
        ResponseEntity<Void> response = new ResponseEntity<>(responseCode);
        when(mockRestTemplate.exchange(dataServicesUrl + nonBaseUrl, PUT, httpEntityForRestCallAnonymous(requestBody), Void.class)).thenReturn(response);
        return response;
    }

    protected <T> ResponseEntity<T> setupGetWithRestResultAnonymousExpectations(String nonBaseUrl, Class<T> responseType, T responseBody) {
        ResponseEntity<T> response = new ResponseEntity<>(responseBody, OK);
        when(mockRestTemplate.exchange(dataServicesUrl + nonBaseUrl, GET, httpEntityForRestCallAnonymous(), responseType)).thenReturn(response);
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

    protected <T> void setupPutWithRestResultVerifications(String nonBaseUrl, Class<T> responseType, Object requestBody) {
        verify(mockRestTemplate).exchange(dataServicesUrl + nonBaseUrl, PUT, httpEntityForRestCall(requestBody), responseType);
    }

    protected <T> void setupPutWithRestResultVerifications(String nonBaseUrl, ParameterizedTypeReference<T> responseType, Object requestBody) {
        verify(mockRestTemplate).exchange(dataServicesUrl + nonBaseUrl, PUT, httpEntityForRestCall(requestBody), responseType);
    }

    protected void setupDeleteWithRestResultVerifications(String nonBaseUrl) {
        verify(mockRestTemplate).exchange(dataServicesUrl + nonBaseUrl, DELETE, httpEntityForRestCall(), Void.class);
    }
}