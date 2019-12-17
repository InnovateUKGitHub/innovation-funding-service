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

import java.util.List;
import java.util.concurrent.Future;

import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.*;
import static org.innovateuk.ifs.user.resource.UserRelatedURLs.*;
import static org.springframework.util.StringUtils.isEmpty;


/**
 * UserRestServiceImpl is a utility for CRUD operations on {@link UserResource}.
 * This class connects to the {org.innovateuk.ifs.user.controller.UserController}
 * through a REST call.
 */
@Service
public class UserRestServiceImpl extends BaseRestService implements UserRestService {


    private String userRestURL = "/user";

    private String processRoleRestURL = "/processrole";

    @Override
    public RestResult<UserResource> retrieveUserResourceByUid(String uid) {
        if(isEmpty(uid))
            return restFailure(CommonErrors.notFoundError(UserResource.class, uid));

        return getWithRestResultAnonymous(userRestURL + "/uid/" + uid, UserResource.class);
    }

    @Override
    public Future<RestResult<Void>> sendPasswordResetNotification(String email) {
        return getWithRestResultAsyncAnonymous(userRestURL + "/"+URL_SEND_PASSWORD_RESET_NOTIFICATION+"/"+ email+"/", Void.class);
    }

    @Override
    public RestResult<Void> checkPasswordResetHash(String hash) {

        if(isEmpty(hash))
            return restFailure(CommonErrors.badRequestError("Missing the hash to reset the password with"));

        return getWithRestResultAnonymous(userRestURL + "/"+ URL_CHECK_PASSWORD_RESET_HASH+"/"+hash, Void.class);
    }

    @Override
    public RestResult<Void> resetPassword(String hash, String password) {


        if(isEmpty(hash))
            return restFailure(CommonErrors.badRequestError("Missing the hash to reset the password with"));

        return postWithRestResultAnonymous(String.format("%s/%s/%s", userRestURL, URL_PASSWORD_RESET, hash), password,  Void.class);
    }

    @Override
    public RestResult<UserResource> findUserByEmail(String email) {
        if (isEmpty(email)) {
            return restFailure(CommonErrors.notFoundError(UserResource.class, email));
        }

        return getWithRestResultAnonymous(userRestURL + "/find-by-email/" + email + "/", UserResource.class);
    }

    @Override
    public RestResult<UserResource> retrieveUserById(long id) {
        if (id == 0) {
            return restFailure(CommonErrors.notFoundError(UserResource.class, id));
        }

        return getWithRestResult(userRestURL + "/id/" + id, UserResource.class);
    }

    @Override
    public RestResult<List<UserResource>> findAll() {
        return getWithRestResult(userRestURL + "/find-all/", userListType());
    }

    @Override
    public RestResult<List<UserOrganisationResource>> findExternalUsers(String searchString, SearchCategory searchCategory) {
        return getWithRestResult(userRestURL + "/find-external-users?searchString=" + searchString + "&searchCategory=" + searchCategory.name(), userOrganisationListType());
    }

    @Override
    public RestResult<List<UserResource>> findByUserRole(Role role) {
        return getWithRestResult(userRestURL + "/find-by-role/" + role, userListType());
    }

    @Override
    public RestResult<UserPageResource> getActiveUsers(String filter, int pageNumber, int pageSize) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("filter", filter);
        String uriWithParams = buildPaginationUri(userRestURL + "/active", pageNumber, pageSize, null, params);
        return getWithRestResult(uriWithParams, UserPageResource.class);
    }

    @Override
    public RestResult<UserPageResource> getInactiveUsers(String filter, int pageNumber, int pageSize) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("filter", filter);
        String uriWithParams = buildPaginationUri(userRestURL + "/inactive", pageNumber, pageSize, null, params);
        return getWithRestResult(uriWithParams, UserPageResource.class);
    }

    @Override
    public RestResult<UserPageResource> getActiveExternalUsers(String filter, int pageNumber, int pageSize) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("filter", filter);
        String uriWithParams = buildPaginationUri(userRestURL + "/external/active", pageNumber, pageSize, null, params);
        return getWithRestResult(uriWithParams, UserPageResource.class);
    }

    @Override
    public RestResult<UserPageResource> getInactiveExternalUsers(String filter, int pageNumber, int pageSize) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("filter", filter);
        String uriWithParams = buildPaginationUri(userRestURL + "/external/inactive", pageNumber, pageSize, null, params);
        return getWithRestResult(uriWithParams, UserPageResource.class);
    }

    @Override
    public RestResult<ProcessRoleResource> findProcessRole(long userId, long applicationId) {
        return getWithRestResult(processRoleRestURL + "/find-by-user-application/" + userId + "/" + applicationId, ProcessRoleResource.class);
    }

    @Override
    public Future<RestResult<ProcessRoleResource>> findProcessRoleById(long processRoleId) {
        return getWithRestResultAsync(processRoleRestURL + "/" + processRoleId, ProcessRoleResource.class);
    }

    @Override
    public RestResult<List<ProcessRoleResource>> findProcessRole(long applicationId) {
        return getWithRestResult(processRoleRestURL + "/find-by-application-id/" + applicationId, processRoleResourceListType());
    }

    @Override
    public RestResult<List<ProcessRoleResource>> findProcessRoleByUserId(long userId) {
        return getWithRestResult(processRoleRestURL + "/find-by-user-id/" + userId, processRoleResourceListType());
    }

    @Override
    public RestResult<List<UserResource>> findAssignableUsers(long applicationId){
        return getWithRestResult(userRestURL + "/find-assignable-users/" + applicationId, userListType());
    }

    @Override
    public Future<RestResult<ProcessRoleResource[]>> findAssignableProcessRoles(long applicationId){
        return getWithRestResultAsync(processRoleRestURL + "/find-assignable/" + applicationId, ProcessRoleResource[].class);
    }

    @Override
    public RestResult<Boolean> userHasApplicationForCompetition(long userId, long competitionId) {
        return getWithRestResult(processRoleRestURL + "/user-has-application-for-competition/" + userId + "/" + competitionId, Boolean.class);
    }

    @Override
    public RestResult<Void> verifyEmail(String hash){
        return getWithRestResultAnonymous(String.format("%s/%s/%s", userRestURL, URL_VERIFY_EMAIL, hash), Void.class);
    }

    @Override
    public RestResult<Void> resendEmailVerificationNotification(String email) {
        return putWithRestResultAnonymous(String.format("%s/%s/%s/", userRestURL, URL_RESEND_EMAIL_VERIFICATION_NOTIFICATION, email), Void.class);
    }

    @Override
    public RestResult<UserResource> createLeadApplicantForOrganisationWithCompetitionId(String firstName, String lastName, String password, String email, String title,
                                                                                        String phoneNumber, long organisationId, Long competitionId, Boolean allowMarketingEmails) {
        UserResource user = new UserResource();

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(password);
        user.setEmail(email);
        if(!isEmpty(title)) {
            user.setTitle(Title.valueOf(title));
        }
        user.setPhoneNumber(phoneNumber);
        user.setAllowMarketingEmails(allowMarketingEmails);

        String url;
        if (competitionId != null) {
            url = userRestURL + "/create-lead-applicant-for-organisation/" + organisationId +"/"+competitionId;
        } else{
            url = userRestURL + "/create-lead-applicant-for-organisation/" + organisationId;
        }

        return postWithRestResultAnonymous(url, user, UserResource.class);
    }

    @Override
    public RestResult<UserResource> createLeadApplicantForOrganisation(String firstName, String lastName, String password, String email, String title,
                                                                       String phoneNumber, long organisationId, Boolean allowMarketingEmails) {
        return this.createLeadApplicantForOrganisationWithCompetitionId(firstName, lastName, password, email, title, phoneNumber, organisationId, null, allowMarketingEmails);
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
        if(!isEmpty(title)) {
            user.setTitle(Title.valueOf(title));
        }
        user.setPhoneNumber(phoneNumber);
        String url = userRestURL + "/update-details";
        return postWithRestResult(url, user, UserResource.class);
    }

    @Override
    public RestResult<Void> createInternalUser(String inviteHash, InternalUserRegistrationResource internalUserRegistrationResource) {
        String url = userRestURL + "/internal/create/" + inviteHash;
        return postWithRestResultAnonymous(url, internalUserRegistrationResource, Void.class);
    }

    @Override
    public RestResult<Void> editInternalUser(EditUserResource editUserResource) {
        String url = userRestURL + "/internal/edit";
        return postWithRestResult(url, editUserResource, Void.class);
    }

    @Override
    public RestResult<Void> agreeNewSiteTermsAndConditions(long userId) {
        String url = userRestURL + "/id/" + userId + "/agree-new-site-terms-and-conditions";
        return postWithRestResult(url, Void.class);
    }

    @Override
    public RestResult<Void> deactivateUser(long userId) {
        String url = userRestURL + "/id/" + userId + "/deactivate";
        return postWithRestResult(url, Void.class);
    }

    @Override
    public RestResult<Void> reactivateUser(long userId) {
        String url = userRestURL + "/id/" + userId + "/reactivate";
        return postWithRestResult(url, Void.class);
    }

    @Override
    public RestResult<Void> grantRole(long userId, Role targetRole) {
        return postWithRestResult(userRestURL + "/" + userId + "/grant/" + targetRole.name());
    }

    @Override
    public RestResult<Void> updateEmail(long userId, String email) {
        return putWithRestResult(userRestURL + "/" + userId + "/update-email/" + email);
    }
}
