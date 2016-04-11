package com.worth.ifs.user.service;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.security.UserPermissionRules;
import com.worth.ifs.user.transactional.UserService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.method.P;

import java.util.List;
import java.util.Set;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Testing how the secured methods in UserService interact with Spring Security
 */
public class UserServiceSecurityTest extends BaseServiceSecurityTest<UserService> {

    private UserPermissionRules rules;

    @Before
    public void lookupPermissionRules() {
        rules = getMockPermissionRulesBean(UserPermissionRules.class);
    }

    @Test
    public void testFindAll() {

        service.findAll();

        verify(rules, times(2)).anyUserCanViewThemselves(isA(UserResource.class), eq(getLoggedInUser()));
        verify(rules, times(2)).assessorsCanViewConsortiumUsersOnApplicationsTheyAreAssessing(isA(UserResource.class), eq(getLoggedInUser()));
        verify(rules, times(2)).compAdminsCanViewEveryone(isA(UserResource.class), eq(getLoggedInUser()));
        verify(rules, times(2)).consortiumMembersCanViewOtherConsortiumMembers(isA(UserResource.class), eq(getLoggedInUser()));
        verify(rules, times(2)).systemRegistrationUserCanViewEveryone(isA(UserResource.class), eq(getLoggedInUser()));
        verifyNoMoreInteractions(rules);
    }

    @Override
    protected Class<? extends UserService> getServiceClass() {
        return TestUserService.class;
    }

    private static class TestUserService implements UserService {

        @Override
        public ServiceResult<UserResource> getUserResourceByUid(String uid) {
            return null;
        }

        @Override
        public ServiceResult<UserResource> getUserById(Long id) {
            return null;
        }

        @Override
        public ServiceResult<List<UserResource>> findAll() {
            return serviceSuccess(newUserResource().build(2));
        }

        @Override
        public ServiceResult<UserResource> findByEmail(String email) {
            return null;
        }

        @Override
        public ServiceResult<Set<UserResource>> findAssignableUsers(Long applicationId) {
            return null;
        }

        @Override
        public ServiceResult<Set<UserResource>> findRelatedUsers(Long applicationId) {
            return null;
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
}
