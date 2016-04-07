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
    ServiceResult<UserResource> getUserResourceByUid(final String uid);

    @NotSecured("TODO - implement when permissions matrix in place")
    ServiceResult<UserResource> getUserById(final Long id);

    @NotSecured("TODO - implement when permissions matrix in place")
    ServiceResult<List<UserResource>> findAll();

    @NotSecured("TODO - implement when permissions matrix in place")
    ServiceResult<UserResource> findByEmail(final String email);

    @NotSecured("TODO - implement when permissions matrix in place")
    ServiceResult<Set<UserResource>> findAssignableUsers(final Long applicationId);

    @NotSecured("TODO - implement when permissions matrix in place")
    ServiceResult<Set<UserResource>> findRelatedUsers(final Long applicationId);

    @NotSecured("TODO - implement when permissions matrix in place")
    ServiceResult<Void> sendPasswordResetNotification(UserResource user);

    @NotSecured("Need to keep open to allow password reset")
    ServiceResult<Void> checkPasswordResetHashValidity(String hash);

    @NotSecured("Need to keep open to allow password reset")
    ServiceResult<Void> changePassword(String hash, String password);
}
