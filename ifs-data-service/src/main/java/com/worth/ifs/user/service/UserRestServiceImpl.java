package com.worth.ifs.user.service;

import com.worth.ifs.commons.resource.ResourceEnvelope;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.user.resource.UserResourceEnvelope;
import com.worth.ifs.security.NotSecured;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Value;
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

    public User retrieveUserByToken(String token) {
        if(StringUtils.isEmpty(token))
            return null;

        return restGet(userRestURL + "/token/" + token, User.class);
    }

    @NotSecured("Method should be able to be called by a web service for guest user that is creating his account to check for duplicate email")
    public List<UserResource> findUserByEmail(String email) {
        if(StringUtils.isEmpty(email))
            return null;
        ResponseEntity<UserResource[]> usersResponse = restGetEntity(userRestURL+"/findByEmail/"+email+"/", UserResource[].class);
        UserResource[] users = usersResponse.getBody();
        return Arrays.asList(users);
    }

    public User retrieveUserByEmailAndPassword(String email, String password) {
        if(StringUtils.isEmpty(email) || StringUtils.isEmpty(password))
            return null;

        return restGet(userRestURL + "/email/" + email + "/password/" + password, User.class);
    }

    public User retrieveUserById(Long id) {
        if(id == null || id.equals(0L))
            return null;

        return restGet(userRestURL + "/id/" + id, User.class);
    }

    public List<User> findAll() {
        ResponseEntity<User[]> responseEntity = restGetEntity(userRestURL + "/findAll/", User[].class);
        User[] users =responseEntity.getBody();
        return Arrays.asList(users);
    }

    public ProcessRole findProcessRole(Long userId, Long applicationId) {
        return restGet(processRoleRestURL + "/findByUserApplication/" + userId + "/" + applicationId, ProcessRole.class);
    }

    public ProcessRole findProcessRoleById(Long processRoleId) {
        return restGet(processRoleRestURL + "/"+ processRoleId, ProcessRole.class);
    }

    public List<ProcessRole> findProcessRole(Long applicationId) {
        ResponseEntity<ProcessRole[]> responseEntity = restGetEntity(processRoleRestURL + "/findByUserApplication/" + applicationId, ProcessRole[].class);
        ProcessRole[] processRole = responseEntity.getBody();
        return Arrays.asList(processRole);
    }

    public List<User> findAssignableUsers(Long applicationId){
        ResponseEntity<User[]> responseEntity = restGetEntity(userRestURL + "/findAssignableUsers/" + applicationId, User[].class);
        User[] users =responseEntity.getBody();
        return Arrays.asList(users);
    }

    public List<ProcessRole> findAssignableProcessRoles(Long applicationId){
        ResponseEntity<ProcessRole[]> responseEntity = restGetEntity(processRoleRestURL + "/findAssignable/" + applicationId, ProcessRole[].class);
        ProcessRole[] processRoles =responseEntity.getBody();
        return Arrays.asList(processRoles);
    }

    public List<User> findRelatedUsers(Long applicationId){
        ResponseEntity<User[]> responseEntity = restGetEntity(getDataRestServiceURL() + userRestURL + "/findRelatedUsers/"+applicationId, User[].class);
        User[] users =responseEntity.getBody();
        return Arrays.asList(users);
    }

    @NotSecured("Method should be able to be called by a web service for guest user to create an account")
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
}
