package com.worth.ifs.user.service;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.transactional.UserService;
import org.springframework.security.access.method.P;

import java.util.List;
import java.util.Set;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;

/**
 * Test class for use in Service Security tests.
 */
public class TestUserService implements UserService {

    @Override
    public ServiceResult<UserResource> getUserResourceByUid(String uid) {
        return serviceSuccess(newUserResource().build());
    }

    @Override
    public ServiceResult<UserResource> getUserById(Long id) {
        return serviceSuccess(newUserResource().build());
    }

    @Override
    public ServiceResult<List<UserResource>> findAll() {
        return serviceSuccess(newUserResource().build(2));
    }

    @Override
    public ServiceResult<UserResource> findByEmail(String email) {
        return serviceSuccess(newUserResource().build());
    }

    @Override
    public ServiceResult<Set<UserResource>> findAssignableUsers(Long applicationId) {
        return serviceSuccess(newUserResource().buildSet(2));
    }

    @Override
    public ServiceResult<Set<UserResource>> findRelatedUsers(Long applicationId) {
        return serviceSuccess(newUserResource().buildSet(2));
    }

    @Override
    public ServiceResult<Void> sendPasswordResetNotification(@P("user") UserResource user) {
        return null;
    }

    @Override
    public ServiceResult<Void> checkPasswordResetHashValidity(@P("hash") String hash) {
        return null;
    }

    @Override
    public ServiceResult<Void> changePassword(@P("hash") String hash, String password) {
        return null;
    }
}
