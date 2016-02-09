package com.worth.ifs.user.service;

import com.worth.ifs.commons.resource.ResourceEnvelope;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.worth.ifs.commons.error.Errors.notFoundError;
import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.commons.service.ParameterizedTypeReferences.*;
import static java.util.Collections.emptyList;

/**
 * UserRestServiceImpl is a utility for CRUD operations on {@link User}.
 * This class connects to the {@link com.worth.ifs.user.controller.UserController}
 * through a REST call.
 */
@Service
public class UserRestServiceImpl extends BaseRestService implements UserRestService {

    private String userRestURL;
    private String processRoleRestURL;

    @Value("${ifs.data.service.rest.user}")
    void setUserRestUrl(String userRestURL) {
        this.userRestURL = userRestURL;
    }

    @Value("${ifs.data.service.rest.processrole}")
    void setProcessRoleRestUrl(String processRoleRestURL) {
        this.processRoleRestURL = processRoleRestURL;
    }

    @Override
    public RestResult<User> retrieveUserByToken(String token) {
        if(StringUtils.isEmpty(token))
            return restFailure(notFoundError(User.class, token));

        return getWithRestResult(userRestURL + "/token/" + token, User.class);
    }

    @Override
    public RestResult<List<UserResource>> findUserByEmail(String email) {
        if(StringUtils.isEmpty(email)) {
            return restSuccess(emptyList());
        }

        return getWithRestResult(userRestURL + "/findByEmail/" + email + "/", userResourceListType());
    }

    @Override
    public RestResult<User> retrieveUserByEmailAndPassword(String email, String password) {
        if(StringUtils.isEmpty(email) || StringUtils.isEmpty(password))
            return restFailure(notFoundError(User.class, email));

        return getWithRestResult(userRestURL + "/email/" + email + "/password/" + password, User.class);
    }

    @Override
    public RestResult<User> retrieveUserById(Long id) {
        if(id == null || id.equals(0L)) {
            return restFailure(notFoundError(User.class, id));
        }

        return getWithRestResult(userRestURL + "/id/" + id, User.class);
    }

    @Override
    public RestResult<List<User>> findAll() {
        return getWithRestResult(userRestURL + "/findAll/", userListType());
    }

    @Override
    public RestResult<ProcessRole> findProcessRole(Long userId, Long applicationId) {
        return getWithRestResult(processRoleRestURL + "/findByUserApplication/" + userId + "/" + applicationId, ProcessRole.class);
    }

    @Override
    public RestResult<ProcessRole> findProcessRoleById(Long processRoleId) {
        return getWithRestResult(processRoleRestURL + "/"+ processRoleId, ProcessRole.class);
    }

    @Override
    public RestResult<List<ProcessRole>> findProcessRole(Long applicationId) {
        return getWithRestResult(processRoleRestURL + "/findByApplicationId/" + applicationId, processRoleListType());
    }

    @Override
    public RestResult<List<User>> findAssignableUsers(Long applicationId){
        return getWithRestResult(userRestURL + "/findAssignableUsers/" + applicationId, userListType());
    }

    @Override
    public RestResult<List<ProcessRole>> findAssignableProcessRoles(Long applicationId){
        return getWithRestResult(processRoleRestURL + "/findAssignable/" + applicationId, processRoleListType());
    }

    @Override
    public RestResult<List<User>> findRelatedUsers(Long applicationId){
        return getWithRestResult(userRestURL + "/findRelatedUsers/"+applicationId, userListType());
    }

    @Override
    // TODO DW - INFUND-1555 - get rid of ResourceEnvelope
    public RestResult<ResourceEnvelope<UserResource>> createLeadApplicantForOrganisation(String firstName, String lastName, String password, String email, String title, String phoneNumber, Long organisationId) {
        UserResource user = new UserResource();

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(password);
        user.setEmail(email);
        user.setTitle(title);
        user.setPhoneNumber(phoneNumber);

        String url = userRestURL + "/createLeadApplicantForOrganisation/" + organisationId;

        return postWithRestResult(url, user, new ParameterizedTypeReference<ResourceEnvelope<UserResource>>() {});
    }

    @Override
    // TODO DW - INFUND-1555 - get rid of ResourceEnvelope
    public RestResult<ResourceEnvelope<UserResource>> updateDetails(String email, String firstName, String lastName, String title, String phoneNumber) {
        UserResource user = new UserResource();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setTitle(title);
        user.setPhoneNumber(phoneNumber);
        String url = userRestURL + "/updateDetails";
        return postWithRestResult(url, user, new ParameterizedTypeReference<ResourceEnvelope<UserResource>>() {});
    }
}
