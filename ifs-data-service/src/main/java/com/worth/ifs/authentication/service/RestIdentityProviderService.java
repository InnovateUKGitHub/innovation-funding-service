package com.worth.ifs.authentication.service;

import com.worth.ifs.authentication.resource.CreateUserResource;
import com.worth.ifs.authentication.resource.UpdateUserResource;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.transactional.ServiceResult;
import com.worth.ifs.util.JsonStatusResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.worth.ifs.authentication.service.RestIdentityProviderService.ServiceFailures.UNABLE_TO_CREATE_USER;
import static com.worth.ifs.authentication.service.RestIdentityProviderService.ServiceFailures.UNABLE_TO_UPDATE_USER;
import static com.worth.ifs.transactional.ServiceResult.failure;
import static com.worth.ifs.transactional.ServiceResult.success;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

/**
 * RESTful implementation of the service that talks to the Identity Provider (in this case, via some API)
 */
@Service
public class RestIdentityProviderService extends BaseRestService implements IdentityProviderService {

    enum ServiceFailures {
        UNABLE_TO_CREATE_USER,
        UNABLE_TO_UPDATE_USER,
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

        // TODO DW - INFUND-1267 - need to define the correct format of the create user request and the subsequent response - currently just
        // showing the uid being returned

        CreateUserResource createUserRequest = new CreateUserResource(emailAddress, password);
        ResponseEntity<JsonStatusResponse> response = restPostWithEntity(idpCreateUserPath, createUserRequest, JsonStatusResponse.class);
        return CREATED.equals(response.getStatusCode()) ? success(response.getBody().getMessage()) : failure(UNABLE_TO_CREATE_USER);
    }

    @Override
    public ServiceResult<String> updateUserPassword(String uid, String password) {

        // TODO DW - INFUND-1267 - need to define the correct format of the create user request and the subsequent response - currently just
        // showing the uid being returned

        UpdateUserResource updateUserRequest = new UpdateUserResource(password);
        ResponseEntity<String> response = restPut(idpUpdateUserPath + "/" + uid, updateUserRequest, String.class);
        return OK.equals(response.getStatusCode()) ? success(response.getBody()) : failure(UNABLE_TO_UPDATE_USER);
    }
}
