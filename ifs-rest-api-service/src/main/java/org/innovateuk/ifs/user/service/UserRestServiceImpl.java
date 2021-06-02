package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.commons.error.CommonErrors;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.invite.resource.EditUserResource;
import org.innovateuk.ifs.registration.resource.InternalUserRegistrationResource;
import org.innovateuk.ifs.user.resource.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.concurrent.Future;

import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.userListType;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.userOrganisationListType;
import static org.innovateuk.ifs.user.resource.UserRelatedURLs.*;


/**
 * UserRestServiceImpl is a utility for CRUD operations on {@link UserResource}.
 * This class connects to the {org.innovateuk.ifs.user.controller.UserController}
 * through a REST call.
 */
@Service
public class UserRestServiceImpl extends BaseRestService implements UserRestService {

    private static final String USER_REST_URL = "/user";

    @Override
    public RestResult<UserResource> retrieveUserResourceByUid(String uid) {
        if(ObjectUtils.isEmpty(uid))
            return restFailure(CommonErrors.notFoundError(UserResource.class, uid));

        return getWithRestResultAnonymous(USER_REST_URL + "/uid/" + uid, UserResource.class);
    }
    @Override
    public RestResult<UserResource> createUser(UserCreationResource user) {
        return postWithRestResultAnonymous(USER_REST_URL, user, UserResource.class);
    }
    @Override
    public Future<RestResult<Void>> sendPasswordResetNotification(String email) {
        return getWithRestResultAsyncAnonymous(USER_REST_URL + "/"+URL_SEND_PASSWORD_RESET_NOTIFICATION+"/"+ email+"/", Void.class);
    }

    @Override
    public RestResult<Void> checkPasswordResetHash(String hash) {

        if(ObjectUtils.isEmpty(hash))
            return restFailure(CommonErrors.badRequestError("Missing the hash to reset the password with"));

        return getWithRestResultAnonymous(USER_REST_URL + "/"+ URL_CHECK_PASSWORD_RESET_HASH+"/"+hash, Void.class);
    }

    @Override
    public RestResult<Void> resetPassword(String hash, String password) {


        if(ObjectUtils.isEmpty(hash))
            return restFailure(CommonErrors.badRequestError("Missing the hash to reset the password with"));

        return postWithRestResultAnonymous(String.format("%s/%s/%s", USER_REST_URL, URL_PASSWORD_RESET, hash), password,  Void.class);
    }

    @Override
    public RestResult<UserResource> findUserByEmail(String email) {
        if (ObjectUtils.isEmpty(email)) {
            return restFailure(CommonErrors.notFoundError(UserResource.class, email));
        }

        return getWithRestResultAnonymous(USER_REST_URL + "/find-by-email/" + email + "/", UserResource.class);
    }

    @Override
    public RestResult<UserResource> retrieveUserById(long id) {
        if (id == 0) {
            return restFailure(CommonErrors.notFoundError(UserResource.class, id));
        }

        return getWithRestResult(USER_REST_URL + "/id/" + id, UserResource.class);
    }

    @Override
    public RestResult<List<UserResource>> findAll() {
        return getWithRestResult(USER_REST_URL + "/find-all/", userListType());
    }

    @Override
    public RestResult<List<UserOrganisationResource>> findExternalUsers(String searchString, SearchCategory searchCategory) {
        return getWithRestResult(USER_REST_URL + "/find-external-users?searchString=" + searchString + "&searchCategory=" + searchCategory.name(), userOrganisationListType());
    }

    @Override
    public RestResult<List<UserResource>> findByUserRole(Role role) {
        return getWithRestResult(USER_REST_URL + "/find-by-role/" + role, userListType());
    }

    @Override
    public RestResult<List<UserResource>> findByUserRoleAndUserStatus(Role role, UserStatus userStatus) {
        return getWithRestResult(USER_REST_URL + "/find-by-role-and-status/" + role + "/status/" + userStatus, userListType());
    }

    @Override
    public RestResult<ManageUserPageResource> getActiveUsers(String filter, int pageNumber, int pageSize) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("filter", filter);
        String uriWithParams = buildPaginationUri(USER_REST_URL + "/active", pageNumber, pageSize, null, params);
        return getWithRestResult(uriWithParams, ManageUserPageResource.class);
    }

    @Override
    public RestResult<ManageUserPageResource> getInactiveUsers(String filter, int pageNumber, int pageSize) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("filter", filter);
        String uriWithParams = buildPaginationUri(USER_REST_URL + "/inactive", pageNumber, pageSize, null, params);
        return getWithRestResult(uriWithParams, ManageUserPageResource.class);
    }

    @Override
    public RestResult<ManageUserPageResource> getActiveExternalUsers(String filter, int pageNumber, int pageSize) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("filter", filter);
        String uriWithParams = buildPaginationUri(USER_REST_URL + "/external/active", pageNumber, pageSize, null, params);
        return getWithRestResult(uriWithParams, ManageUserPageResource.class);
    }

    @Override
    public RestResult<ManageUserPageResource> getInactiveExternalUsers(String filter, int pageNumber, int pageSize) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("filter", filter);
        String uriWithParams = buildPaginationUri(USER_REST_URL + "/external/inactive", pageNumber, pageSize, null, params);
        return getWithRestResult(uriWithParams, ManageUserPageResource.class);
    }

    @Override
    public RestResult<List<UserResource>> findAssignableUsers(long applicationId){
        return getWithRestResult(USER_REST_URL + "/find-assignable-users/" + applicationId, userListType());
    }

    @Override
    public RestResult<Void> verifyEmail(String hash){
        return getWithRestResultAnonymous(String.format("%s/%s/%s", USER_REST_URL, URL_VERIFY_EMAIL, hash), Void.class);
    }

    @Override
    public RestResult<Void> resendEmailVerificationNotification(String email) {
        return putWithRestResultAnonymous(String.format("%s/%s/%s/", USER_REST_URL, URL_RESEND_EMAIL_VERIFICATION_NOTIFICATION, email), Void.class);
    }

    @Override
    public RestResult<UserResource> updateDetails(long id, String email, String firstName, String lastName, String title,
                                                  String phoneNumber, boolean allowMarketingEmails) {
        UserResource user = new UserResource();
        user.setId(id);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAllowMarketingEmails(allowMarketingEmails);
        if(!ObjectUtils.isEmpty(title)) {
            user.setTitle(Title.valueOf(title));
        }
        user.setPhoneNumber(phoneNumber);
        String url = USER_REST_URL + "/update-details";
        return postWithRestResult(url, user, UserResource.class);
    }

    @Override
    public RestResult<Void> createInternalUser(String inviteHash, InternalUserRegistrationResource internalUserRegistrationResource) {
        String url = USER_REST_URL + "/internal/create/" + inviteHash;
        return postWithRestResultAnonymous(url, internalUserRegistrationResource, Void.class);
    }

    @Override
    public RestResult<Void> editInternalUser(EditUserResource editUserResource) {
        String url = USER_REST_URL + "/internal/edit";
        return postWithRestResult(url, editUserResource, Void.class);
    }

    @Override
    public RestResult<Void> agreeNewSiteTermsAndConditions(long userId) {
        String url = USER_REST_URL + "/id/" + userId + "/agree-new-site-terms-and-conditions";
        return postWithRestResult(url, Void.class);
    }

    @Override
    public RestResult<Void> deactivateUser(long userId) {
        String url = USER_REST_URL + "/id/" + userId + "/deactivate";
        return postWithRestResult(url, Void.class);
    }

    @Override
    public RestResult<Void> reactivateUser(long userId) {
        String url = USER_REST_URL + "/id/" + userId + "/reactivate";
        return postWithRestResult(url, Void.class);
    }

    @Override
    public RestResult<Void> grantRole(long userId, Role targetRole) {
        return postWithRestResult(USER_REST_URL + "/" + userId + "/grant/" + targetRole.name());
    }

    @Override
    public RestResult<Void> updateEmail(long userId, String email) {
        return putWithRestResult(USER_REST_URL + "/" + userId + "/update-email/" + email);
    }
}