package com.worth.ifs.authentication.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.authentication.resource.CreateUserResource;
import com.worth.ifs.authentication.resource.CreateUserResponse;
import com.worth.ifs.authentication.resource.IdentityProviderError;
import com.worth.ifs.authentication.resource.UpdateUserResource;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import static com.worth.ifs.authentication.service.RestIdentityProviderService.ServiceFailures.*;
import static com.worth.ifs.commons.error.CommonErrors.internalServerErrorError;
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
    public void testCreateUserRecordWithUid() throws JsonProcessingException {

        CreateUserResource createRequest = new CreateUserResource("email@example.com", "thepassword");
        CreateUserResponse successResponse = new CreateUserResponse("new-uid");
        ResponseEntity<String> successResponseEntity = new ResponseEntity<>(asJson(successResponse), CREATED);

        when(mockRestTemplate.postForEntity("http://idprest/createuser", httpEntityForRestCall(createRequest), String.class)).thenReturn(successResponseEntity);

        ServiceResult<String> result = service.createUserRecordWithUid("email@example.com", "thepassword");
        assertTrue(result.isSuccess());
        assertEquals("new-uid", result.getSuccessObject());
    }

    @Test
    public void testCreateUserRecordWithUidButDuplicateEmailFailureResponseReturned() throws JsonProcessingException {

        CreateUserResource createRequest = new CreateUserResource("email@example.com", "thepassword");
        IdentityProviderError errorResponse = new IdentityProviderError(DUPLICATE_EMAIL_ADDRESS.name(), emptyList());
        ResponseEntity<String> errorResponseEntity = new ResponseEntity<>(asJson(errorResponse), CONFLICT);

        when(mockRestTemplate.postForEntity("http://idprest/createuser", httpEntityForRestCall(createRequest), String.class)).thenReturn(errorResponseEntity);

        ServiceResult<String> result = service.createUserRecordWithUid("email@example.com", "thepassword");
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(new Error(DUPLICATE_EMAIL_ADDRESS, CONFLICT)));
    }

    @Test
    public void testCreateUserRecordWithUidButOtherFailureResponseReturned() throws JsonProcessingException {

        CreateUserResource createRequest = new CreateUserResource("email@example.com", "thepassword");
        IdentityProviderError errorResponse = new IdentityProviderError("Another error", emptyList());
        ResponseEntity<String> errorResponseEntity = new ResponseEntity<>(asJson(errorResponse), CONFLICT);

        when(mockRestTemplate.postForEntity("http://idprest/createuser", httpEntityForRestCall(createRequest), String.class)).thenReturn(errorResponseEntity);

        ServiceResult<String> result = service.createUserRecordWithUid("email@example.com", "thepassword");
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(new Error(UNABLE_TO_CREATE_USER, INTERNAL_SERVER_ERROR)));
    }

    @Test
    public void testUpdateUserRecordWithUid() {

        UpdateUserResource updateRequest = new UpdateUserResource("newpassword");
        ResponseEntity<String> successResponseEntity = new ResponseEntity<>(OK);

        when(mockRestTemplate.exchange("http://idprest/updateuser/existing-uid", PUT, httpEntityForRestCall(updateRequest), String.class)).thenReturn(successResponseEntity);

        ServiceResult<String> result = service.updateUserPassword("existing-uid", "newpassword");
        assertTrue(result.isSuccess());
        assertEquals("existing-uid", result.getSuccessObject());
    }

    @Test
    public void testUpdateUserRecordWithUidButFailureResponseReturned() throws JsonProcessingException {

        UpdateUserResource updateRequest = new UpdateUserResource("newpassword");
        ResponseEntity<String> failureResponseEntity = new ResponseEntity<>(asJson(new IdentityProviderError("Error!", emptyList())), BAD_REQUEST);

        when(mockRestTemplate.exchange("http://idprest/updateuser/existing-uid", PUT, httpEntityForRestCall(updateRequest), String.class)).thenReturn(failureResponseEntity);

        ServiceResult<String> result = service.updateUserPassword("existing-uid", "newpassword");
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(internalServerErrorError()));
    }

    private String asJson(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }
}
