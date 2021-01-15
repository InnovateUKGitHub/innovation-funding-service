package org.innovateuk.ifs.authentication.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.authentication.resource.*;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.AbstractRestTemplateAdaptor;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.config.rest.RestTemplateAdaptorFactory;
import org.innovateuk.ifs.events.UserCreationEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.authentication.service.RestIdentityProviderService.ServiceFailures.DUPLICATE_EMAIL_ADDRESS;
import static org.innovateuk.ifs.authentication.service.RestIdentityProviderService.ServiceFailures.UNABLE_TO_CREATE_USER;
import static org.innovateuk.ifs.commons.error.CommonErrors.internalServerErrorError;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.*;

/**
 * Tests around the RestIdentityProviderService talking to the Shib REST API via the restTemplate
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class RestIdentityProviderServiceTest extends BaseUnitTestMocksTest  {

    @Mock
    protected RestTemplate mockRestTemplate;

    @Mock
    protected AsyncRestTemplate mockAsyncRestTemplate;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private RestIdentityProviderService service;

    private AbstractRestTemplateAdaptor adaptor;

    @Before
    public void setupServiceWithMockTemplateAndSpringSecurity() {
        final RestTemplateAdaptorFactory factory = new RestTemplateAdaptorFactory();
        ReflectionTestUtils.setField(factory, "shibbolethKey", "api-key");
        adaptor = factory.shibbolethAdaptor();
        adaptor.setAsyncRestTemplate(mockAsyncRestTemplate);
        adaptor.setRestTemplate(mockRestTemplate);
        service = new RestIdentityProviderService();
        ReflectionTestUtils.setField(service, "adaptor", adaptor);
        ReflectionTestUtils.setField(service, "idpBaseURL", "http://idprest");
        ReflectionTestUtils.setField(service, "idpUserPath", "/user");
        service.setApplicationEventPublisher(applicationEventPublisher);
    }

    @Test
    public void createUserRecordWithUid() throws JsonProcessingException {

        CreateUserResource createRequest = new CreateUserResource("email@example.com", "thepassword");
        CreateUserResponse successResponse = new CreateUserResponse("new-uid", "email@example.com", null, null);

        ResponseEntity<String> successResponseEntity = new ResponseEntity<>(asJson(successResponse), CREATED);

        when(mockRestTemplate.postForEntity("http://idprest/user", adaptor.jsonEntity(createRequest), String.class)).thenReturn(successResponseEntity);

        ServiceResult<String> result = service.createUserRecordWithUid("email@example.com", "thepassword");

        verify(applicationEventPublisher).publishEvent(any(UserCreationEvent.class));
        assertTrue(result.isSuccess());
        assertEquals("new-uid", result.getSuccess());
    }

    @Test
    public void createUserRecordWithUidButDuplicateEmailFailureResponseReturned() throws JsonProcessingException {

        CreateUserResource createRequest = new CreateUserResource("email@example.com", "thepassword");
        IdentityProviderError[] errorResponse = new IdentityProviderError[]{new IdentityProviderError(DUPLICATE_EMAIL_ADDRESS.name(), emptyList())};
        ResponseEntity<String> errorResponseEntity = new ResponseEntity<>(asJson(errorResponse), CONFLICT);

        when(mockRestTemplate.postForEntity("http://idprest/user", adaptor.jsonEntity(createRequest), String.class)).thenReturn(errorResponseEntity);

        ServiceResult<String> result = service.createUserRecordWithUid("email@example.com", "thepassword");
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(new Error(DUPLICATE_EMAIL_ADDRESS, CONFLICT)));
    }

    @Test
    public void createUserRecordWithUidButOtherFailureResponseReturned() throws JsonProcessingException {

        CreateUserResource createRequest = new CreateUserResource("email@example.com", "thepassword");
        IdentityProviderError[] errorResponse = new IdentityProviderError[]{new IdentityProviderError(UNABLE_TO_CREATE_USER.name(), emptyList())};
        ResponseEntity<String> errorResponseEntity = new ResponseEntity<>(asJson(errorResponse), CONFLICT);

        when(mockRestTemplate.postForEntity("http://idprest/user", adaptor.jsonEntity(createRequest), String.class)).thenReturn(errorResponseEntity);

        ServiceResult<String> result = service.createUserRecordWithUid("email@example.com", "thepassword");
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(new Error(UNABLE_TO_CREATE_USER, CONFLICT)));
    }

    @Test
    public void updateUserRecordWithUid() {

        UpdateUserResource updateRequest = new UpdateUserResource("newpassword");
        ResponseEntity<String> successResponseEntity = new ResponseEntity<>(OK);

        when(mockRestTemplate.exchange("http://idprest/user/existing-uid/password", PUT, adaptor.jsonEntity(updateRequest), String.class)).thenReturn(successResponseEntity);

        ServiceResult<String> result = service.updateUserPassword("existing-uid", "newpassword");
        assertTrue(result.isSuccess());
        assertEquals("existing-uid", result.getSuccess());
    }

    @Test
    public void updateUserRecordWithEmail() {

        UpdateEmailResource updateRequest = new UpdateEmailResource("new@email.com");
        ResponseEntity<String> successResponseEntity = new ResponseEntity<>(OK);

        when(mockRestTemplate.exchange("http://idprest/user/existing-uid/email", PUT, adaptor.jsonEntity(updateRequest), String.class)).thenReturn(successResponseEntity);

        ServiceResult<String> result = service.updateUserEmail("existing-uid", "new@email.com");
        assertTrue(result.isSuccess());
        assertEquals("existing-uid", result.getSuccess());
    }

    @Test
    public void updateUserRecordWithUidButFailureResponseReturned() throws JsonProcessingException {

        UpdateUserResource updateRequest = new UpdateUserResource("newpassword");
        ResponseEntity<String> failureResponseEntity = new ResponseEntity<>(asJson(new IdentityProviderError("Error!", emptyList())), BAD_REQUEST);

        ServiceResult<String> result = service.updateUserPassword("existing-uid", "newpassword");
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(internalServerErrorError()));
    }

    @Test
    public void weakPasswordError() throws JsonProcessingException {
        CreateUserResource createRequest = new CreateUserResource("email@example.com", "Password123");
        IdentityProviderError[] errorResponse = new IdentityProviderError[]{new IdentityProviderError(RestIdentityProviderService.INVALID_PASSWORD_KEY, Arrays.asList(new String[]{"blacklisted"}))};
        ResponseEntity<String> errorResponseEntity = new ResponseEntity<>(asJson(errorResponse), BAD_REQUEST);

        when(mockRestTemplate.postForEntity("http://idprest/user", adaptor.jsonEntity(createRequest), String.class)).thenReturn(errorResponseEntity);

        ServiceResult<String> result = service.createUserRecordWithUid("email@example.com", "Password123");
        assertTrue(result.isFailure());
        assertEquals(result.getFailure().getErrors().size(), 1);
        Error expectedError = result.getFailure().getErrors().get(0);
        assertEquals(expectedError.getFieldName(), "password");
    }

    private String asJson(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }
}
