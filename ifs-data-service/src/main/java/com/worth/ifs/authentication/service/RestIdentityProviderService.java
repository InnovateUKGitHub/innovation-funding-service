package com.worth.ifs.authentication.service;

import com.worth.ifs.authentication.resource.CreateUserResource;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.transactional.ServiceResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.worth.ifs.authentication.service.RestIdentityProviderService.ServiceFailures.UNABLE_TO_CREATE_USER;
import static com.worth.ifs.transactional.ServiceResult.failure;
import static com.worth.ifs.transactional.ServiceResult.success;
import static org.springframework.http.HttpStatus.CREATED;

/**
 * RESTful implementation of the service that talks to the Identity Provider (in this case, via some API)
 */
@Service
public class RestIdentityProviderService extends BaseRestService implements IdentityProviderService {

    enum ServiceFailures {
        UNABLE_TO_CREATE_USER
    }

    @Value("${idp.rest.baseURL}")
    String idpRestServiceUrl;

    @Value("${idp.rest.createuser}")
    String idpCreateUserPath;

    @Override
    protected String getDataRestServiceURL() {
        return idpRestServiceUrl;
    }

    @Override
    public ServiceResult<String> createUserRecordWithUid(String title, String firstName, String lastName, String emailAddress, String password) {

        // TODO DW - INFUND-1267 - need to define the correct format of the create user request and the subsequent response - currently jus
        // showing the uid being returned

        ResponseEntity<String> response = restPostWithEntity(idpCreateUserPath, new CreateUserResource(title, firstName, lastName, emailAddress, password), String.class);
        return CREATED.equals(response.getStatusCode()) ? success(response.getBody()) : failure(UNABLE_TO_CREATE_USER);
    }
}
