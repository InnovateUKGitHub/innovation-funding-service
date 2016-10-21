package com.worth.ifs.user.service;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.token.domain.Token;
import com.worth.ifs.token.security.TokenLookupStrategies;
import com.worth.ifs.token.security.TokenPermissionRules;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import com.worth.ifs.user.security.UserPermissionRules;
import com.worth.ifs.user.transactional.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.access.method.P;

import java.util.List;
import java.util.Set;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

/**
 * Testing how the secured methods in UserService interact with Spring Security
 */
public class UserServiceSecurityTest extends BaseServiceSecurityTest<UserService> {

    private UserPermissionRules userRules;
    private TokenPermissionRules tokenRules;
    private TokenLookupStrategies tokenLookupStrategies;

    @Before
    public void lookupPermissionRules() {
        userRules = getMockPermissionRulesBean(UserPermissionRules.class);
        tokenRules = getMockPermissionRulesBean(TokenPermissionRules.class);
        tokenLookupStrategies = getMockPermissionEntityLookupStrategiesBean(TokenLookupStrategies.class);
    }

    @Test
    public void testFindAll() {
        classUnderTest.findAll();
        assertViewMultipleUsersExpectations();
    }

    @Test
    public void testFindAssignableUsers() {
        classUnderTest.findAssignableUsers(123L);
        assertViewMultipleUsersExpectations();
    }

    @Test
    public void testFindByEmail() {
        assertAccessDenied(() -> classUnderTest.findByEmail("asdf@example.com"), () -> {
            assertViewSingleUserExpectations();
        });
    }

    @Test
    public void testGetUserById() {
        assertAccessDenied(() -> classUnderTest.getUserById(123L), () -> {
            assertViewSingleUserExpectations();
        });
    }

    @Test
    public void testGetUserByUid() {

        // this method must remain unsecured because it is the way in which we get a user onto the
        // SecurityContext in the first place for permission checking
        classUnderTest.getUserResourceByUid("asdf");
        verifyNoMoreInteractionsWithRules();
    }

    @Test
    public void testChangePassword() {

        Token token = new Token();
        when(tokenLookupStrategies.getTokenByHash("hash")).thenReturn(token);

        assertAccessDenied(() -> classUnderTest.changePassword("hash", "newpassword"), () -> {
            verify(tokenRules).systemRegistrationUserCanUseTokensToResetPaswords(token, getLoggedInUser());
            verifyNoMoreInteractionsWithRules();
        });
    }

    @Test
    public void testSendPasswordResetNotification() {

        UserResource user = UserResourceBuilder.newUserResource().build();
        assertAccessDenied(() -> classUnderTest.sendPasswordResetNotification(user), () -> {
            verify(userRules).usersCanChangeTheirOwnPassword(user, getLoggedInUser());
            verify(userRules).systemRegistrationUserCanChangePasswordsForUsers(user, getLoggedInUser());
            verifyNoMoreInteractionsWithRules();
        });
    }

    @Test
    public void testFindRelatedUsers() {
        classUnderTest.findRelatedUsers(123L);
        assertViewMultipleUsersExpectations();
    }

    private void assertViewSingleUserExpectations() {
        assertViewXUsersExpectations(1);
    }

    private void assertViewMultipleUsersExpectations() {
        assertViewXUsersExpectations(2);
    }

    private void assertViewXUsersExpectations(int numberOfUsers) {
        verify(userRules, times(numberOfUsers)).anyUserCanViewThemselves(isA(UserResource.class), eq(getLoggedInUser()));
        verify(userRules, times(numberOfUsers)).assessorsCanViewConsortiumUsersOnApplicationsTheyAreAssessing(isA(UserResource.class), eq(getLoggedInUser()));
        verify(userRules, times(numberOfUsers)).compAdminsCanViewEveryone(isA(UserResource.class), eq(getLoggedInUser()));
        verify(userRules, times(numberOfUsers)).consortiumMembersCanViewOtherConsortiumMembers(isA(UserResource.class), eq(getLoggedInUser()));
        verify(userRules, times(numberOfUsers)).systemRegistrationUserCanViewEveryone(isA(UserResource.class), eq(getLoggedInUser()));
        verify(userRules, times(numberOfUsers)).projectFinanceUsersCanViewEveryone(isA(UserResource.class), eq(getLoggedInUser()));
        verifyNoMoreInteractionsWithRules();
    }

    private void verifyNoMoreInteractionsWithRules() {
        Mockito.verifyNoMoreInteractions(tokenRules);
        Mockito.verifyNoMoreInteractions(userRules);
    }

    @Override
    protected Class<? extends UserService> getClassUnderTest() {
        return TestUserService.class;
    }

    /**
     * Test class for use in Service Security tests.
     */
    public static class TestUserService implements UserService {

        @Override
        public ServiceResult<UserResource> getUserResourceByUid(String uid) {
            return ServiceResult.serviceSuccess(UserResourceBuilder.newUserResource().build());
        }

        @Override
        public ServiceResult<UserResource> getUserById(Long id) {
            return ServiceResult.serviceSuccess(UserResourceBuilder.newUserResource().build());
        }

        @Override
        public ServiceResult<List<UserResource>> findAll() {
            return ServiceResult.serviceSuccess(UserResourceBuilder.newUserResource().build(2));
        }

        @Override
        public ServiceResult<List<UserResource>> findByProcessRole(UserRoleType roleType) {
            return ServiceResult.serviceSuccess(UserResourceBuilder.newUserResource().build(2));
        }

        @Override
        public ServiceResult<UserResource> findByEmail(String email) {
            return ServiceResult.serviceSuccess(UserResourceBuilder.newUserResource().build());
        }

        @Override
        public ServiceResult<UserResource> findInactiveByEmail(String email) {
            return ServiceResult.serviceSuccess(UserResourceBuilder.newUserResource().build());
        }

        @Override
        public ServiceResult<Set<UserResource>> findAssignableUsers(Long applicationId) {
            return ServiceResult.serviceSuccess(UserResourceBuilder.newUserResource().buildSet(2));
        }

        @Override
        public ServiceResult<Set<UserResource>> findRelatedUsers(Long applicationId) {
            return ServiceResult.serviceSuccess(UserResourceBuilder.newUserResource().buildSet(2));
        }

        @Override
        public ServiceResult<Void> sendPasswordResetNotification(@P("user") UserResource user) {
            return null;
        }

        @Override
        public ServiceResult<Void> changePassword(@P("hash") String hash, String password) {
            return null;
        }
    }
}
