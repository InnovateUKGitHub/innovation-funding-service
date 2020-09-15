package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.application.security.ApplicationLookupStrategy;
import org.innovateuk.ifs.application.security.ApplicationPermissionRules;
import org.innovateuk.ifs.token.domain.Token;
import org.innovateuk.ifs.token.security.TokenLookupStrategies;
import org.innovateuk.ifs.token.security.TokenPermissionRules;
import org.innovateuk.ifs.user.resource.*;
import org.innovateuk.ifs.user.security.UserLookupStrategies;
import org.innovateuk.ifs.user.security.UserPermissionRules;
import org.innovateuk.ifs.user.transactional.UserService;
import org.innovateuk.ifs.user.transactional.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;

import static org.innovateuk.ifs.application.transactional.ApplicationServiceSecurityTest.verifyApplicationRead;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.UserOrganisationResourceBuilder.newUserOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.externalApplicantRoles;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

/**
 * Testing how the secured methods in UserService interact with Spring Security
 */
public class UserServiceSecurityTest extends BaseServiceSecurityTest<UserService> {

    private UserPermissionRules userRules;
    private TokenPermissionRules tokenRules;
    private UserLookupStrategies userLookupStrategies;
    private TokenLookupStrategies tokenLookupStrategies;
    ApplicationPermissionRules applicationPermissionRules;
    ApplicationLookupStrategy applicationLookupStrategy;

    @Before
    public void lookupPermissionRules() {
        userRules = getMockPermissionRulesBean(UserPermissionRules.class);
        tokenRules = getMockPermissionRulesBean(TokenPermissionRules.class);
        userLookupStrategies = getMockPermissionEntityLookupStrategiesBean(UserLookupStrategies.class);
        tokenLookupStrategies = getMockPermissionEntityLookupStrategiesBean(TokenLookupStrategies.class);
        applicationPermissionRules = getMockPermissionRulesBean(ApplicationPermissionRules.class);
        applicationLookupStrategy = getMockPermissionEntityLookupStrategiesBean(ApplicationLookupStrategy.class);

    }

    @Test
    public void findAssignableUsers() {
        verifyApplicationRead(applicationLookupStrategy, applicationPermissionRules,
                (applicationId) -> classUnderTest.findAssignableUsers(applicationId));
    }

    @Test
    public void findByEmail() {
        String email = "asdf@example.com";

        when(classUnderTestMock.findByEmail(email))
                .thenReturn(serviceSuccess(newUserResource().build()));

        assertAccessDenied(() -> classUnderTest.findByEmail(email), this::assertViewSingleUserExpectations);
    }

    @Test
    public void changePassword() {
        Token token = new Token();
        when(tokenLookupStrategies.getTokenByHash("hash")).thenReturn(token);

        assertAccessDenied(() -> classUnderTest.changePassword("hash", "newpassword"), () -> {
            verify(tokenRules).systemRegistrationUserCanUseTokensToResetPaswords(token, getLoggedInUser());
            verifyNoMoreInteractionsWithRules();
        });
    }

    @Test
    public void sendPasswordResetNotification() {

        UserResource user = newUserResource().build();
        assertAccessDenied(() -> classUnderTest.sendPasswordResetNotification(user), () -> {
            verify(userRules).usersCanChangeTheirOwnPassword(user, getLoggedInUser());
            verify(userRules).systemRegistrationUserCanChangePasswordsForUsers(user, getLoggedInUser());
            verifyNoMoreInteractionsWithRules();
        });
    }

    @Test
    public void findRelatedUsers() {
        verifyApplicationRead(applicationLookupStrategy, applicationPermissionRules,
                (applicationId) -> classUnderTest.findRelatedUsers(applicationId));
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
        verify(userRules, times(numberOfUsers))
                .stakeholdersCanViewUsersInCompetitionsTheyAreAssignedTo(isA(UserResource.class), eq(getLoggedInUser()));
        verify(userRules, times(numberOfUsers))
                .monitoringOfficersCanViewUsersInCompetitionsTheyAreAssignedTo(isA(UserResource.class), eq(getLoggedInUser()));
        verify(userRules, times(numberOfUsers))
                .competitionFinanceUsersCanViewUsersInCompetitionsTheyAreAssignedTo(isA(UserResource.class), eq(getLoggedInUser()));
        verifyNoMoreInteractionsWithRules();
    }

    private void verifyNoMoreInteractionsWithRules() {
        verifyNoMoreInteractions(tokenRules);
        verifyNoMoreInteractions(userRules);
    }

    @Test
    public void updateDetails() {
        UserResource user = newUserResource().build();

        assertAccessDenied(() -> classUnderTest.updateDetails(user), () -> {
            verify(userRules).usersCanUpdateTheirOwnProfiles(user, getLoggedInUser());
            verify(userRules).adminsCanUpdateUserDetails(user, getLoggedInUser());
            verifyNoMoreInteractions(userRules);
        });
    }

    @Test
    public void findActive() {
        when(classUnderTestMock.findActive("", PageRequest.of(0, 5)))
                .thenReturn(serviceSuccess(new ManageUserPageResource()));

        assertAccessDenied(() -> classUnderTest.findActive("", new PageRequest(0, 5)), () -> {
            verify(userRules).internalUsersCanViewEveryone(isA(ManageUserPageResource.class), eq(getLoggedInUser()));
            verifyNoMoreInteractions(userRules);
        });
    }

    @Test
    public void findInactive() {
        when(classUnderTestMock.findInactive("", PageRequest.of(0, 5)))
                .thenReturn(serviceSuccess(new ManageUserPageResource()));

        assertAccessDenied(() -> classUnderTest.findInactive("", new PageRequest(0, 5)), () -> {
            verify(userRules).internalUsersCanViewEveryone(isA(ManageUserPageResource.class), eq(getLoggedInUser()));
            verifyNoMoreInteractions(userRules);
        });
    }

    @Test
    public void findActiveExternal() {
        when(classUnderTestMock.findActiveExternal("", PageRequest.of(0, 5)))
                .thenReturn(serviceSuccess(new ManageUserPageResource()));

        assertAccessDenied(() -> classUnderTest.findActiveExternal("", new PageRequest(0, 5)), () -> {
            verify(userRules).supportUsersCanViewExternalUsers(isA(ManageUserPageResource.class), eq(getLoggedInUser()));
            verifyNoMoreInteractions(userRules);
        });
    }

    @Test
    public void findInactiveExternal() {
        when(classUnderTestMock.findInactiveExternal("", PageRequest.of(0, 5)))
                .thenReturn(serviceSuccess(new ManageUserPageResource()));

        assertAccessDenied(() -> classUnderTest.findInactiveExternal("", new PageRequest(0, 5)), () -> {
            verify(userRules).supportUsersCanViewExternalUsers(isA(ManageUserPageResource.class), eq(getLoggedInUser()));
            verifyNoMoreInteractions(userRules);
        });
    }

    @Test
    public void findByProcessRolesAndSearchCriteria() {
        when(classUnderTestMock.findByProcessRolesAndSearchCriteria(externalApplicantRoles(), "%aar%", SearchCategory
                .NAME))
                .thenReturn(serviceSuccess(newUserOrganisationResource().build(2)));

        classUnderTest.findByProcessRolesAndSearchCriteria(externalApplicantRoles(), "%aar%", SearchCategory.NAME);

        verify(userRules, times(2))
                .internalUsersCanViewUserOrganisation(isA(UserOrganisationResource.class), eq(getLoggedInUser()));
        verifyNoMoreInteractions(userRules);
    }

    @Test
    public void agreeNewTermsAndConditions() {
        UserResource user = newUserResource().build();

        when(userLookupStrategies.findById(user.getId())).thenReturn(user);

        assertAccessDenied(() -> classUnderTest.agreeNewTermsAndConditions(user.getId()), () -> {
            verify(userRules).usersCanAgreeSiteTermsAndConditions(user, getLoggedInUser());
            verifyNoMoreInteractions(userRules);
        });
    }

    @Override
    protected Class<? extends UserService> getClassUnderTest() {
        return UserServiceImpl.class;
    }
}