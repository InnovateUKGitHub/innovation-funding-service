package com.worth.ifs.authentication.service;

import com.worth.ifs.authentication.resource.CreateUserResource;
import com.worth.ifs.authentication.resource.CreateUserResponse;
import com.worth.ifs.authentication.resource.IdentityProviderError;
import com.worth.ifs.authentication.resource.UpdateUserResource;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.AbstractRestTemplateAdaptor;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.util.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.worth.ifs.commons.error.CommonErrors.internalServerErrorError;
import static com.worth.ifs.commons.service.ServiceResult.*;
import static com.worth.ifs.util.Either.left;
import static com.worth.ifs.util.Either.right;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

/**
 * RESTful implementation of the service that talks to the Identity Provider (in this case, via some API)
 */
@Service
public class RestIdentityProviderService implements IdentityProviderService {

    @Autowired
    @Qualifier("shibboleth_adaptor")
    private AbstractRestTemplateAdaptor adaptor;

    public enum ServiceFailures {
        UNABLE_TO_CREATE_USER,
        UNABLE_TO_UPDATE_USER,
        DUPLICATE_EMAIL_ADDRESS
    }

    @Value("${idp.rest.baseURL}")
    private String idpBaseURL;

    @Value("${idp.rest.user}")
    private String idpUserPath;

    @Override
    public ServiceResult<String> createUserRecordWithUid(String emailAddress, String password) {

        return handlingErrors(() -> {

            CreateUserResource createUserRequest = new CreateUserResource(emailAddress, password);
            Either<ResponseEntity<IdentityProviderError[]>, CreateUserResponse> response = restPost(idpBaseURL + idpUserPath, createUserRequest, CreateUserResponse.class, IdentityProviderError[].class, CREATED);
            return response.mapLeftOrRight(
                    failure -> serviceFailure(errors(failure.getStatusCode(), failure.getBody())),
                    success -> serviceSuccess(success.getUuid())
            );
        });
    }

    private static final List<Error> errors(HttpStatus code, IdentityProviderError... errors){
        return asList(errors).stream().map(e -> new Error(e.getKey(), code)).collect(toList());
    }

    protected <T, R> Either<ResponseEntity<R>, T> restPost(String path, Object postEntity, Class<T> successClass, Class<R> failureClass, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        return adaptor.restPostWithEntity(path, postEntity, successClass, failureClass, expectedSuccessCode, otherExpectedStatusCodes).mapLeftOrRight(
                failure -> left(failure),
                success -> right(success.getBody())
        );
    }

    @Override
    public ServiceResult<String> updateUserPassword(String uid, String password) {
        return handlingErrors(() -> {
            UpdateUserResource updateUserRequest = new UpdateUserResource(password);
            Either<ResponseEntity<IdentityProviderError[]>, Void> response = restPut(idpBaseURL + idpUserPath + "/" + uid + "/password", updateUserRequest, Void.class, IdentityProviderError[].class, OK);
            return response.mapLeftOrRight(
                    failure -> serviceFailure(errors(failure.getStatusCode(), failure.getBody())),
                    success -> serviceSuccess(uid)
            );
        });
    }

    @Override
    public ServiceResult<String> activateUser(String uid) {
        return handlingErrors(() -> {
            Either<ResponseEntity<IdentityProviderError>, Void> response = restPut(idpBaseURL + idpUserPath + "/" + uid + "/activateUser", null, Void.class, IdentityProviderError.class, OK);
            return response.mapLeftOrRight(
                    failure -> serviceFailure(internalServerErrorError()),
                    success -> serviceSuccess(uid)
            );
        });
    }

    protected <T, R> Either<ResponseEntity<R>, T> restPut(String path, Object postEntity, Class<T> successClass, Class<R> failureClass, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        return adaptor.restPutWithEntity(path, postEntity, successClass, failureClass, expectedSuccessCode, otherExpectedStatusCodes).mapLeftOrRight(
                failure -> left(failure),
                success -> right(success.getBody())
        );
    }
}