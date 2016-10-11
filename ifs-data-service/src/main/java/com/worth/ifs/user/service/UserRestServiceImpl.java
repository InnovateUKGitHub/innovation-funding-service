package com.worth.ifs.user.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.user.controller.UserController;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.Future;

import static com.worth.ifs.commons.error.CommonErrors.badRequestError;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static com.worth.ifs.commons.service.ParameterizedTypeReferences.*;
import static com.worth.ifs.util.CollectionFunctions.simpleJoiner;
import static java.lang.String.format;

/**
 * UserRestServiceImpl is a utility for CRUD operations on {@link User}.
 * This class connects to the {@link com.worth.ifs.user.controller.UserController}
 * through a REST call.
 */
@Service
public class UserRestServiceImpl extends BaseRestService implements UserRestService {

    private static final Log LOG = LogFactory.getLog(UserRestServiceImpl.class);

    private String userRestURL = "/user";

    private String processRoleRestURL = "/processrole";

    @Override
    public RestResult<UserResource> retrieveUserResourceByUid(String uid) {
        if(StringUtils.isEmpty(uid))
            return restFailure(notFoundError(User.class, uid));

        return getWithRestResult(userRestURL + "/uid/" + uid, UserResource.class);
    }

    @Override
    public RestResult<Void> sendPasswordResetNotification(String email) {
        if(StringUtils.isEmpty(email))
            return restFailure(notFoundError(User.class, email));

        return getWithRestResultAnonymous(userRestURL + "/"+UserController.URL_SEND_PASSWORD_RESET_NOTIFICATION+"/"+ email+"/", Void.class);
    }

    @Override
    public RestResult<Void> checkPasswordResetHash(String hash) {
        LOG.warn("checkPasswordResetHash");

        if(StringUtils.isEmpty(hash))
            return restFailure(badRequestError("Missing the hash to reset the password with"));

        LOG.warn("checkPasswordResetHash 2 " + userRestURL + "/"+ UserController.URL_CHECK_PASSWORD_RESET_HASH+"/"+hash);
        return getWithRestResultAnonymous(userRestURL + "/"+ UserController.URL_CHECK_PASSWORD_RESET_HASH+"/"+hash, Void.class);
    }

    @Override
    public RestResult<Void> resetPassword(String hash, String password) {
        LOG.warn("resetPassword");

        if(StringUtils.isEmpty(hash))
            return restFailure(badRequestError("Missing the hash to reset the password with"));

        LOG.warn("resetPassword 2 " + userRestURL + "/"+ UserController.URL_PASSWORD_RESET+"/"+hash+" body: "+password);
        return postWithRestResultAnonymous(format("%s/%s/%s", userRestURL, UserController.URL_PASSWORD_RESET, hash), password,  Void.class);
    }

    @Override
    public RestResult<UserResource> findUserByEmail(String email) {
        if(StringUtils.isEmpty(email)) {
            return restFailure(notFoundError(User.class, email));
        }

        return getWithRestResult(userRestURL + "/findByEmail/" + email + "/", UserResource.class);
    }

    @Override
    public RestResult<UserResource> findUserByEmailForAnonymousUserFlow(String email) {
        if(StringUtils.isEmpty(email)) {
            return restFailure(notFoundError(User.class, email));
        }

        return getWithRestResultAnonymous(userRestURL + "/findByEmail/" + email + "/", UserResource.class);
    }

    @Override
    public RestResult<UserResource> retrieveUserById(Long id) {
        if(id == null || id.equals(0L)) {
            return restFailure(notFoundError(User.class, id));
        }

        return getWithRestResult(userRestURL + "/id/" + id, UserResource.class);
    }

    @Override
    public RestResult<List<UserResource>> findAll() {
        return getWithRestResult(userRestURL + "/findAll/", userListType());
    }

    @Override
    public RestResult<List<UserResource>> findByUserRoleType(UserRoleType userRoleType) {
        String roleName = userRoleType.getName();
        return getWithRestResult(userRestURL + "/findByRole/"+roleName, userListType());
    }

    @Override
    public RestResult<ProcessRoleResource> findProcessRole(Long userId, Long applicationId) {
        return getWithRestResult(processRoleRestURL + "/findByUserApplication/" + userId + "/" + applicationId, ProcessRoleResource.class);
    }

    @Override
    public Future<RestResult<ProcessRoleResource>> findProcessRoleById(Long processRoleId) {
        return getWithRestResultAsync(processRoleRestURL + "/" + processRoleId, ProcessRoleResource.class);
    }

    @Override
    public RestResult<List<ProcessRoleResource>> findProcessRole(Long applicationId) {
        return getWithRestResult(processRoleRestURL + "/findByApplicationId/" + applicationId, processRoleResourceListType());
    }

    @Override
    public RestResult<List<UserResource>> findAssignableUsers(Long applicationId){
        return getWithRestResult(userRestURL + "/findAssignableUsers/" + applicationId, userListType());
    }

    @Override
    public Future<RestResult<ProcessRoleResource[]>> findAssignableProcessRoles(Long applicationId){
        return getWithRestResultAsync(processRoleRestURL + "/findAssignable/" + applicationId, ProcessRoleResource[].class);
    }

    @Override
    public RestResult<List<UserResource>> findRelatedUsers(Long applicationId){
        return getWithRestResult(userRestURL + "/findRelatedUsers/"+applicationId, userListType());
    }

    @Override
    public RestResult<Void> verifyEmail(String hash){
        return getWithRestResultAnonymous(format("%s/%s/%s", userRestURL, UserController.URL_VERIFY_EMAIL, hash), Void.class);
    }

    @Override
    public RestResult<Void> resendEmailVerificationNotification(String email) {
        return putWithRestResultAnonymous(format("%s/%s/%s/", userRestURL, UserController.URL_RESEND_EMAIL_VERIFICATION_NOTIFICATION, email), Void.class);
    }

    @Override
    public RestResult<UserResource> createLeadApplicantForOrganisationWithCompetitionId(String firstName, String lastName, String password, String email, String title, String phoneNumber, Long organisationId, Long competitionId) {
        UserResource user = new UserResource();

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(password);
        user.setEmail(email);
        user.setTitle(title);
        user.setPhoneNumber(phoneNumber);

        String url;
        if(competitionId != null){
            url = userRestURL + "/createLeadApplicantForOrganisation/" + organisationId +"/"+competitionId;
        }else{
            url = userRestURL + "/createLeadApplicantForOrganisation/" + organisationId;
        }

        return postWithRestResultAnonymous(url, user, UserResource.class);
    }

    @Override
    public RestResult<UserResource> createLeadApplicantForOrganisation(String firstName, String lastName, String password, String email, String title, String phoneNumber, Long organisationId) {
        return this.createLeadApplicantForOrganisationWithCompetitionId(firstName, lastName, password, email, title, phoneNumber, organisationId, null);
    }

    @Override
    public RestResult<UserResource> updateDetails(Long id, String email, String firstName, String lastName, String title, String phoneNumber) {
        UserResource user = new UserResource();
        user.setId(id);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setTitle(title);
        user.setPhoneNumber(phoneNumber);
        String url = userRestURL + "/updateDetails";
        return postWithRestResult(url, user, UserResource.class);
    }

    @Override
    public RestResult<UserResource> updateProfile(Long id, ProfileResource profile) {
        return putWithRestResult(format("%s/id/%s/updateProfile", userRestURL, id), profile, UserResource.class);
    }

    @Override
    public RestResult<List<AffiliationResource>> getUserAffiliations(Long userId) {
        return getWithRestResult(format("%s/id/%s/getUserAffiliations", userRestURL, userId), affiliationResourceListType());
    }

    @Override
    public RestResult<Void> updateUserAffiliations(Long userId, List<AffiliationResource> affiliations) {
        return putWithRestResult(format("%s/id/%s/updateUserAffiliations", userRestURL, userId), affiliations, Void.class);
    }

    @Override
    public RestResult<Void> updateUserContract(Long userId, ProfileResource profileResource) {
        return putWithRestResult(format("%s/id/%s/updateUserContract", userRestURL, userId), profileResource, Void.class);
    }
}
