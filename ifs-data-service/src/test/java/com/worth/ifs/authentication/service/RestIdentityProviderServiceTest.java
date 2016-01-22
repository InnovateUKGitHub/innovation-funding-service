package com.worth.ifs.authentication.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.authentication.resource.CreateUserResource;
import com.worth.ifs.authentication.resource.IdentityProviderError;
import com.worth.ifs.authentication.resource.UpdateUserResource;
import com.worth.ifs.authentication.resource.CreateUserResponse;
import com.worth.ifs.transactional.ServiceResult;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import static com.worth.ifs.authentication.service.RestIdentityProviderService.ServiceFailures.DUPLICATE_EMAIL_ADDRESS;
import static com.worth.ifs.authentication.service.RestIdentityProviderService.ServiceFailures.UNABLE_TO_CREATE_USER;
import static com.worth.ifs.authentication.service.RestIdentityProviderService.ServiceFailures.UNABLE_TO_UPDATE_USER;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.*;

/**
 * Tests around the RestIdentityProviderService talking to the Shib REST API via the restTemplate
 */
public class RestIdentityProviderServiceTest extends BaseRestServiceUnitTest<RestIdentityProviderService> {

    @Override
    protected RestIdentityProviderService registerRestServiceUnderTest() {

        RestIdentityProviderService idpService = new RestIdentityProviderService();
        ReflectionTestUtils.setField(idpService, "idpRestServiceUrl", "http://idprest");
        ReflectionTestUtils.setField(idpService, "idpCreateUserPath", "/createuser");
        ReflectionTestUtils.setField(idpService, "idpUpdateUserPath", "/updateuser");
        return idpService;
    }

    @Test
    public void testCreateUserRecordWithUid() {

        CreateUserResource createRequest = new CreateUserResource("email@example.com", "thepassword");
        CreateUserResponse successResponse = new CreateUserResponse("new-uid");
        ResponseEntity<String> successResponseEntity = new ResponseEntity<>(asJson(successResponse), CREATED);

        when(mockRestTemplate.postForEntity("http://idprest/createuser", httpEntityForRestCall(createRequest), String.class)).thenReturn(successResponseEntity);

        ServiceResult<String> result = service.createUserRecordWithUid("email@example.com", "thepassword");
        assertTrue(result.isRight());
        assertEquals("new-uid", result.getRight());
    }

    @Test
    public void testCreateUserRecordWithUidButDuplicateEmailFailureResponseReturned() {

        CreateUserResource createRequest = new CreateUserResource("email@example.com", "thepassword");
        IdentityProviderError errorResponse = new IdentityProviderError(DUPLICATE_EMAIL_ADDRESS.name(), emptyList());
        ResponseEntity<String> errorResponseEntity = new ResponseEntity<>(asJson(errorResponse), CONFLICT);

        when(mockRestTemplate.postForEntity("http://idprest/createuser", httpEntityForRestCall(createRequest), String.class)).thenReturn(errorResponseEntity);

        ServiceResult<String> result = service.createUserRecordWithUid("email@example.com", "thepassword");
        assertTrue(result.isLeft());
        assertTrue(result.getLeft().is(DUPLICATE_EMAIL_ADDRESS));
    }

    @Test
    public void testCreateUserRecordWithUidButOtherFailureResponseReturned() {

        CreateUserResource createRequest = new CreateUserResource("email@example.com", "thepassword");
        IdentityProviderError errorResponse = new IdentityProviderError("Another error", emptyList());
        ResponseEntity<String> errorResponseEntity = new ResponseEntity<>(asJson(errorResponse), CONFLICT);

        when(mockRestTemplate.postForEntity("http://idprest/createuser", httpEntityForRestCall(createRequest), String.class)).thenReturn(errorResponseEntity);

        ServiceResult<String> result = service.createUserRecordWithUid("email@example.com", "thepassword");
        assertTrue(result.isLeft());
        assertTrue(result.getLeft().is(UNABLE_TO_CREATE_USER));
    }

    @Test
    public void testCreateUserRecordWithUidButExceptionThrown() {

        CreateUserResource createRequest = new CreateUserResource("email@example.com", "thepassword");
        when(mockRestTemplate.postForEntity("http://idprest/createuser", httpEntityForRestCall(createRequest), String.class)).thenThrow(new IllegalArgumentException("no creating!"));

        ServiceResult<String> result = service.createUserRecordWithUid("email@example.com", "thepassword");
        assertTrue(result.isLeft());
        assertTrue(result.getLeft().is(UNABLE_TO_CREATE_USER));
    }

    @Test
    public void testUpdateUserRecordWithUid() {

        UpdateUserResource updateRequest = new UpdateUserResource("newpassword");
        ResponseEntity<String> successResponseEntity = new ResponseEntity<>(OK);

        when(mockRestTemplate.exchange("http://idprest/updateuser/existing-uid", PUT, httpEntityForRestCall(updateRequest), String.class)).thenReturn(successResponseEntity);

        ServiceResult<String> result = service.updateUserPassword("existing-uid", "newpassword");
        assertTrue(result.isRight());
        assertEquals("existing-uid", result.getRight());
    }

    @Test
    public void testUpdateUserRecordWithUidButFailureResponseReturned() {

        UpdateUserResource updateRequest = new UpdateUserResource("newpassword");
        ResponseEntity<String> failureResponseEntity = new ResponseEntity<>(asJson(new IdentityProviderError("Error!", emptyList())), BAD_REQUEST);

        when(mockRestTemplate.exchange("http://idprest/updateuser/existing-uid", PUT, httpEntityForRestCall(updateRequest), String.class)).thenReturn(failureResponseEntity);

        ServiceResult<String> result = service.updateUserPassword("existing-uid", "newpassword");
        assertTrue(result.isLeft());
        assertTrue(result.getLeft().is(UNABLE_TO_UPDATE_USER));
    }

    @Test
    public void testUpdateUserRecordWithUidButExceptionThrown() {

        UpdateUserResource updateRequest = new UpdateUserResource("newpassword");

        when(mockRestTemplate.exchange("http://idprest/updateuser/existing-uid", PUT, httpEntityForRestCall(updateRequest), String.class)).thenThrow(new IllegalArgumentException("no updating!"));

        ServiceResult<String> result = service.updateUserPassword("existing-uid", "newpassword");
        assertTrue(result.isLeft());
        assertTrue(result.getLeft().is(UNABLE_TO_UPDATE_USER));
    }

}
