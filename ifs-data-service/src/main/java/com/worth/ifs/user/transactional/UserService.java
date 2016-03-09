package com.worth.ifs.user.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.security.NotSecured;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserResource;

import java.util.List;
import java.util.Set;

/**
 * A Service that covers basic operations concerning Users
 */
public interface UserService {

    @NotSecured("Need to keep open to all to allow login")
    ServiceResult<User> getUserByUid(final String uid);

    @NotSecured("Need to keep open to all to allow login")
    ServiceResult<User> getUserByEmailandPassword(final String email, final String password);

    @NotSecured("TODO - implement when permissions matrix in place")
    ServiceResult<User> getUserById(final Long id);

    @NotSecured("TODO - implement when permissions matrix in place")
    ServiceResult<List<User>> getUserByName(final String name);

    @NotSecured("TODO - implement when permissions matrix in place")
    ServiceResult<List<User>> findAll();

    @NotSecured("TODO - implement when permissions matrix in place")
    ServiceResult<List<UserResource>> findByEmail(final String email);

    @NotSecured("TODO - implement when permissions matrix in place")
    ServiceResult<Set<User>> findAssignableUsers(final Long applicationId);

    @NotSecured("TODO - implement when permissions matrix in place")
    ServiceResult<Set<User>> findRelatedUsers(final Long applicationId);

    @NotSecured("TODO - implement when permissions matrix in place")
    ServiceResult<UserResource> createUser(final Long organisationId, UserResource userResource);

    @NotSecured("TODO - implement when permissions matrix in place")
    ServiceResult<UserResource> updateUser(UserResource userResource);

    @NotSecured("TODO - implement when permissions matrix in place")
    ServiceResult<Void> activateUser(Long userId);
}
