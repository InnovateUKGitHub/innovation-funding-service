package com.worth.ifs.authentication.service;

import com.worth.ifs.authentication.resource.CreateUserResource;
import com.worth.ifs.authentication.resource.CreateUserResponse;
import com.worth.ifs.authentication.resource.IdentityProviderError;
import com.worth.ifs.authentication.resource.UpdateUserResource;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.transactional.ServiceResult;
import com.worth.ifs.util.Either;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.worth.ifs.authentication.service.RestIdentityProviderService.ServiceFailures.DUPLICATE_EMAIL_ADDRESS;
import static com.worth.ifs.authentication.service.RestIdentityProviderService.ServiceFailures.UNABLE_TO_CREATE_USER;
import static com.worth.ifs.authentication.service.RestIdentityProviderService.ServiceFailures.UNABLE_TO_UPDATE_USER;
import static com.worth.ifs.transactional.ServiceResult.failure;
import static com.worth.ifs.transactional.ServiceResult.handlingErrors;
import static com.worth.ifs.transactional.ServiceResult.success;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

/**
 * RESTful implementation of the service that talks to the Identity Provider (in this case, via some API)
 */
@Service
public class RestIdentityProviderService extends BaseRestService implements IdentityProviderService {

    public enum ServiceFailures {
        UNABLE_TO_CREATE_USER,
        UNABLE_TO_UPDATE_USER,
        DUPLICATE_EMAIL_ADDRESS
    }

    @Value("${idp.rest.baseURL}")
    String idpRestServiceUrl;

    @Value("${idp.rest.createuser}")
    String idpCreateUserPath;

    @Value("${idp.rest.updateuser}")
    String idpUpdateUserPath;

    @Override
    protected String getDataRestServiceURL() {
        return idpRestServiceUrl;
    }

    @Override
    public ServiceResult<String> createUserRecordWithUid(String emailAddress, String password) {

        return handlingErrors(UNABLE_TO_CREATE_USER, () -> {

            CreateUserResource createUserRequest = new CreateUserResource(emailAddress, password);
            Either<IdentityProviderError, CreateUserResponse> response = restPost(idpCreateUserPath, createUserRequest, CreateUserResponse.class, IdentityProviderError.class, CREATED);
            return response.mapLeftOrRight(
                    failure -> DUPLICATE_EMAIL_ADDRESS.name().equals(failure.getMessageKey()) ? failure(DUPLICATE_EMAIL_ADDRESS) : failure(UNABLE_TO_CREATE_USER),
                    success -> success(success.getUniqueId())
            );
        });
    }

    @Override
    public ServiceResult<String> updateUserPassword(String uid, String password) {

        return handlingErrors(UNABLE_TO_UPDATE_USER, () -> {

            UpdateUserResource updateUserRequest = new UpdateUserResource(password);
            Either<IdentityProviderError, String> response = restPut(idpUpdateUserPath + "/" + uid, updateUserRequest, String.class, IdentityProviderError.class, OK);
            return response.mapLeftOrRight(
                    failure -> failure(UNABLE_TO_UPDATE_USER),
                    success -> success(uid)
            );
        });
    }
}
