package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.command.GrantRoleCommand;
import org.innovateuk.ifs.user.resource.*;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Set;

/**
 * A Service that covers basic operations concerning Users
 */
public interface UserService {

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<UserResource> findByEmail(String email);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<UserResource> findInactiveByEmail(String email);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<Set<UserResource>> findAssignableUsers(long applicationId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<Set<UserResource>> findRelatedUsers(long applicationId);

    @PreAuthorize("hasPermission(#user, 'CHANGE_PASSWORD')")
    ServiceResult<Void> sendPasswordResetNotification(UserResource user);

    @PreAuthorize("hasPermission(#hash, 'org.innovateuk.ifs.token.domain.Token', 'CHANGE_PASSWORD')")
    ServiceResult<Void> changePassword(String hash, String password);

    @PostAuthorize("hasPermission(returnObject, 'READ_INTERNAL')")
    ServiceResult<ManageUserPageResource> findActive(String filter, Pageable pageable);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<ManageUserPageResource> findActiveExternal(String filter, Pageable pageable);

    @PostAuthorize("hasPermission(returnObject, 'READ_INTERNAL')")
    ServiceResult<ManageUserPageResource> findInactive(String filter, Pageable pageable);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<ManageUserPageResource> findInactiveExternal(String filter, Pageable pageable);

    @PostFilter("hasPermission(filterObject, 'READ_USER_ORGANISATION')")
    ServiceResult<List<UserOrganisationResource>> findByProcessRolesAndSearchCriteria(Set<Role> roleTypes, String searchString, SearchCategory searchCategory);

    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserResource', 'AGREE_TERMS')")
    ServiceResult<UserResource> agreeNewTermsAndConditions(long userId);

    @PreAuthorize("hasPermission(#grantRoleCommand, 'GRANT_ROLE')")
    ServiceResult<UserResource> grantRole(GrantRoleCommand grantRoleCommand);

    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserResource', 'UPDATE_USER_EMAIL')")
    ServiceResult<UserResource> updateEmail(long userId, String email);

    @PreAuthorize("hasPermission(#userBeingUpdated, 'UPDATE')")
    ServiceResult<UserResource> updateDetails(UserResource userBeingUpdated);

    @NotSecured(value = "Can be called anywhere by anyone", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> evictUserCache(String uid);
}