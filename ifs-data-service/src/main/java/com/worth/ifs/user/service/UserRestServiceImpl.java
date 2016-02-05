package com.worth.ifs.user.service;

import com.worth.ifs.commons.resource.ResourceEnvelope;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.security.NotSecured;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserResourceEnvelope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

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
    public User retrieveUserByToken(String token) {
        if(StringUtils.isEmpty(token))
            return null;

        return restGet(userRestURL + "/token/" + token, User.class);
    }

    @NotSecured("Method should be able to be called by a web service for guest user that is creating his account to check for duplicate email")
    @Override
    public List<UserResource> findUserByEmail(String email) {
        if(StringUtils.isEmpty(email))
            return null;
        ResponseEntity<UserResource[]> usersResponse = restGetEntity(userRestURL+"/findByEmail/"+email+"/", UserResource[].class);
        UserResource[] users = usersResponse.getBody();
        return Arrays.asList(users);
    }

    @Override
    public User retrieveUserByEmailAndPassword(String email, String password) {
        if(StringUtils.isEmpty(email) || StringUtils.isEmpty(password))
            return null;

        return restGet(userRestURL + "/email/" + email + "/password/" + password, User.class);
    }

    @Override
    public User retrieveUserById(Long id) {
        if(id == null || id.equals(0L))
            return null;

        return restGet(userRestURL + "/id/" + id, User.class);
    }

    @Override
    public List<User> findAll() {
        ResponseEntity<User[]> responseEntity = restGetEntity(userRestURL + "/findAll/", User[].class);
        User[] users = responseEntity.getBody();
        return Arrays.asList(users);
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
        return getWithRestResult(processRoleRestURL + "/findByUserApplication/" + applicationId, processRoleListType());
    }

    private ParameterizedTypeReference<List<ProcessRole>> processRoleListType() {
        return new ParameterizedTypeReference<List<ProcessRole>>() {};
    }

    @Override
    public List<User> findAssignableUsers(Long applicationId){
        ResponseEntity<User[]> responseEntity = restGetEntity(userRestURL + "/findAssignableUsers/" + applicationId, User[].class);
        User[] users =responseEntity.getBody();
        return Arrays.asList(users);
    }

    @Override
    public RestResult<List<ProcessRole>> findAssignableProcessRoles(Long applicationId){
        return getWithRestResult(processRoleRestURL + "/findAssignable/" + applicationId, processRoleListType());
    }

    @Override
    public List<User> findRelatedUsers(Long applicationId){
        ResponseEntity<User[]> responseEntity = restGetEntity(getDataRestServiceURL() + userRestURL + "/findRelatedUsers/"+applicationId, User[].class);
        User[] users =responseEntity.getBody();
        return Arrays.asList(users);
    }

    @NotSecured("Method should be able to be called by a web service for guest user to create an account")
    @Override
    public ResourceEnvelope<UserResource> createLeadApplicantForOrganisation(String firstName, String lastName, String password, String email, String title, String phoneNumber, Long organisationId) {
        UserResource user = new UserResource();

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(password);
        user.setEmail(email);
        user.setTitle(title);
        user.setPhoneNumber(phoneNumber);

        String url = userRestURL + "/createLeadApplicantForOrganisation/" + organisationId;

        return restPost(url, user, UserResourceEnvelope.class);
    }

    @Override
    public ResourceEnvelope<UserResource> updateDetails(String email, String firstName, String lastName, String title, String phoneNumber) {
        UserResource user = new UserResource();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setTitle(title);
        user.setPhoneNumber(phoneNumber);
        String url = userRestURL + "/updateDetails";
        return restPost(url, user, UserResourceEnvelope.class);
    }
}
