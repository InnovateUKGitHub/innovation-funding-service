package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.EditUserResource;
import org.innovateuk.ifs.registration.resource.InternalUserRegistrationResource;
import org.innovateuk.ifs.user.resource.*;

import java.util.List;
import java.util.concurrent.Future;

/**
 * Interface for CRUD operations on {@link UserResource} related data.
 */
public interface UserRestService {
    RestResult<UserResource> retrieveUserResourceByUid(String uid);

    RestResult<UserResource> retrieveUserById(long id);

    RestResult<UserResource> createUser(UserCreationResource user);

    RestResult<List<UserResource>> findAll();

    RestResult<List<UserOrganisationResource>> findExternalUsers(String searchString, SearchCategory searchCategory);

    RestResult<List<UserResource>> findByUserRole(Role role);

    RestResult<List<UserResource>> findByUserRoleAndUserStatus(Role role, UserStatus userStatus);

    RestResult<ManageUserPageResource> getActiveUsers(String filter, int pageNumber, int pageSize);

    RestResult<ManageUserPageResource> getInactiveUsers(String filter, int pageNumber, int pageSize);

    RestResult<ManageUserPageResource> getActiveExternalUsers(String filter, int pageNumber, int pageSize);

    RestResult<ManageUserPageResource> getInactiveExternalUsers(String filter, int pageNumber, int pageSize);

    RestResult<List<UserResource>> findAssignableUsers(long applicationId);

    RestResult<UserResource> findUserByEmail(String email);

    RestResult<Void> verifyEmail(String hash);

    RestResult<Void> resendEmailVerificationNotification(String email);

    Future<RestResult<Void>> sendPasswordResetNotification(String email);

    RestResult<Void> checkPasswordResetHash(String hash);

    RestResult<Void> resetPassword(String hash, String password);

    RestResult<UserResource> updateDetails(long id, String email, String firstName, String lastName, String title, String phoneNumber, boolean allowMarketingEmails);

    RestResult<Void> createInternalUser(String inviteHash, InternalUserRegistrationResource internalUserRegistrationResource);

    RestResult<Void> editInternalUser(EditUserResource editUserResource);

    RestResult<Void> agreeNewSiteTermsAndConditions(long userId);

    RestResult<Void> deactivateUser(long userId);
    RestResult<Void> reactivateUser(long userId);
    RestResult<Void> grantRole(long userId, Role targetRole);
    RestResult<Void> updateEmail(long userId, String email);
}