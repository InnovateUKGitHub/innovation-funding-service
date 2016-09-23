package com.worth.ifs.user.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.commons.security.NotSecured;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Set;

/**
 * A Service that covers basic operations concerning Users
 */
public interface UserService {

    @NotSecured(value = "This UID method is needed prior to being able to put a User on the SecurityContext, and so it cannot be secured itself", mustBeSecuredByOtherServices = false)
    ServiceResult<UserResource> getUserResourceByUid(final String uid);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<UserResource> getUserById(final Long id);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<UserResource>> findAll();

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<UserResource>> findByProcessRole(UserRoleType roleType);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<UserResource> findByEmail(final String email);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<UserResource> findInactiveByEmail(final String email);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<Set<UserResource>> findAssignableUsers(final Long applicationId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<Set<UserResource>> findRelatedUsers(final Long applicationId);

    @PreAuthorize("hasPermission(#user, 'CHANGE_PASSWORD')")
    ServiceResult<Void> sendPasswordResetNotification(@P("user") UserResource user);

    @PreAuthorize("hasPermission(#hash, 'com.worth.ifs.token.domain.Token', 'CHANGE_PASSWORD')")
    ServiceResult<Void> changePassword(@P("hash") String hash, String password);
}
