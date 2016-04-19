package com.worth.ifs.user.service;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.token.domain.Token;
import com.worth.ifs.token.security.TokenLookupStrategies;
import com.worth.ifs.token.security.TokenPermissionRules;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.security.UserPermissionRules;
import com.worth.ifs.user.transactional.UserService;
import org.junit.Before;
import org.junit.Test;

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
        service.findAll();
        assertViewMultipleUsersExpectations();
    }

    @Test
    public void testFindAssignableUsers() {
        service.findAssignableUsers(123L);
        assertViewMultipleUsersExpectations();
    }

    @Test
    public void testFindByEmail() {
        assertAccessDenied(() -> service.findByEmail("asdf@example.com"), () -> {
            assertViewSingleUserExpectations();
        });
    }

    @Test
    public void testGetUserById() {
        assertAccessDenied(() -> service.getUserById(123L), () -> {
            assertViewSingleUserExpectations();
        });
    }

    @Test
    public void testGetUserByUid() {

        // this method must remain unsecured because it is the way in which we get a user onto the
        // SecurityContext in the first place for permission checking
        service.getUserResourceByUid("asdf");
        verifyNoMoreInteractionsWithRules();
    }

    @Test
    public void testChangePassword() {

        Token token = new Token();
        when(tokenLookupStrategies.getTokenByHash("hash")).thenReturn(token);

        assertAccessDenied(() -> service.changePassword("hash", "newpassword"), () -> {
            verify(tokenRules).systemRegistrationUserCanUseTokensToResetPaswords(token, getLoggedInUser());
            verifyNoMoreInteractionsWithRules();
        });
    }

    @Test
    public void testPasswordResetHashValidity() {

        Token token = new Token();
        when(tokenLookupStrategies.getTokenByHash("hash")).thenReturn(token);

        assertAccessDenied(() -> service.checkPasswordResetHashValidity("hash"), () -> {
            verify(tokenRules).systemRegistrationUserCanReadTokens(token, getLoggedInUser());
            verifyNoMoreInteractionsWithRules();
        });
    }

    @Test
    public void testSendPasswordResetNotification() {

        UserResource user = newUserResource().build();
        assertAccessDenied(() -> service.sendPasswordResetNotification(user), () -> {
            verify(userRules).usersCanChangeTheirOwnPassword(user, getLoggedInUser());
            verify(userRules).systemRegistrationUserCanChangePasswordsForUsers(user, getLoggedInUser());
            verifyNoMoreInteractionsWithRules();
        });
    }

    @Test
    public void testFindRelatedUsers() {
        service.findRelatedUsers(123L);
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
        verifyNoMoreInteractionsWithRules();
    }

    private void verifyNoMoreInteractionsWithRules() {
        verifyNoMoreInteractions(tokenRules);
        verifyNoMoreInteractions(userRules);
    }

    @Override
    protected Class<? extends UserService> getServiceClass() {
        return TestUserService.class;
    }

}
