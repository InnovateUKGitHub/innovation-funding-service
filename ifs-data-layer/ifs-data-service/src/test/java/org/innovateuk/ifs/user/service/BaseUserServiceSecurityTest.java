package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.token.security.TokenLookupStrategies;
import org.innovateuk.ifs.token.security.TokenPermissionRules;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.security.UserPermissionRules;
import org.innovateuk.ifs.user.transactional.BaseUserService;
import org.innovateuk.ifs.user.transactional.BaseUserServiceImpl;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

/**
 * Testing how the secured methods in UserService interact with Spring Security
 */
public class BaseUserServiceSecurityTest extends BaseServiceSecurityTest<BaseUserService> {

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
        when(classUnderTestMock.findAll())
                .thenReturn(serviceSuccess(newUserResource().build(2)));

        classUnderTest.findAll();
        assertViewMultipleUsersExpectations();
    }

    @Test
    public void testGetUserById() {
        when(classUnderTestMock.getUserById(123L))
                .thenReturn(serviceSuccess(newUserResource().build()));

        assertAccessDenied(() -> classUnderTest.getUserById(123L), this::assertViewSingleUserExpectations);
    }

    @Test
    public void testGetUserByUid() {
        when(classUnderTestMock.getUserResourceByUid("asdf"))
                .thenReturn(serviceSuccess(newUserResource().build()));

        // this method must remain unsecured because it is the way in which we get a user onto the
        // SecurityContext in the first place for permission checking
        classUnderTest.getUserResourceByUid("asdf");
        verifyNoMoreInteractionsWithRules();
    }

    private void assertViewSingleUserExpectations() {
        assertViewXUsersExpectations(1);
    }

    private void assertViewMultipleUsersExpectations() {
        assertViewXUsersExpectations(2);
    }

    private void assertViewXUsersExpectations(int numberOfUsers) {
        verify(userRules, times(numberOfUsers))
                .anyUserCanViewThemselves(isA(UserResource.class), eq(getLoggedInUser()));
        verify(userRules, times(numberOfUsers))
                .assessorsCanViewConsortiumUsersOnApplicationsTheyAreAssessing(isA(UserResource.class), eq
                        (getLoggedInUser()));
        verify(userRules, times(numberOfUsers))
                .internalUsersCanViewEveryone(isA(UserResource.class), eq(getLoggedInUser()));
        verify(userRules, times(numberOfUsers))
                .consortiumMembersCanViewOtherConsortiumMembers(isA(UserResource.class), eq(getLoggedInUser()));
        verify(userRules, times(numberOfUsers))
                .systemRegistrationUserCanViewEveryone(isA(UserResource.class), eq(getLoggedInUser()));
        verifyNoMoreInteractionsWithRules();
    }

    private void verifyNoMoreInteractionsWithRules() {
        verifyNoMoreInteractions(tokenRules);
        verifyNoMoreInteractions(userRules);
    }

    @Override
    protected Class<? extends BaseUserService> getClassUnderTest() {
        return BaseUserServiceImpl.class;
    }
}
