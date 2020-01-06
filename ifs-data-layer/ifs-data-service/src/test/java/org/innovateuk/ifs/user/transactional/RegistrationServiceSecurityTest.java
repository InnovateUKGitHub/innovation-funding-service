package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.security.UserLookupStrategies;
import org.innovateuk.ifs.user.security.UserPermissionRules;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.registration.builder.UserRegistrationResourceBuilder.newUserRegistrationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Mockito.*;

/**
 * Testing how this service integrates with Spring Security
 */
public class RegistrationServiceSecurityTest extends BaseServiceSecurityTest<RegistrationService> {

    private UserPermissionRules rules;
    private UserLookupStrategies lookup;

    @Before
    public void lookupPermissionRules() {
        rules = getMockPermissionRulesBean(UserPermissionRules.class);
        lookup = getMockPermissionEntityLookupStrategiesBean(UserLookupStrategies.class);
    }

    @Test
    public void createUser() {
        UserRegistrationResource userToCreate = newUserRegistrationResource().build();

        assertAccessDenied(() -> classUnderTest.createUser(userToCreate), () -> {
            verify(rules).systemRegistrationUserCanCreateUsers(userToCreate, getLoggedInUser());
            verifyNoMoreInteractions(rules);
        });
    }

    @Test
    public void createOrganisationUser() {

        UserResource userToCreate = newUserResource().build();

        assertAccessDenied(() -> classUnderTest.createUser(userToCreate), () -> {
            verify(rules).systemRegistrationUserCanCreateUsers(userToCreate, getLoggedInUser());
            verifyNoMoreInteractions(rules);
        });
    }

    @Test
    public void activateUser() {

        UserResource userToActivate = newUserResource().build();

        when(lookup.findById(123L)).thenReturn(userToActivate);

        assertAccessDenied(() -> classUnderTest.activateUser(123L), () -> {
            verify(rules).systemRegistrationUserCanActivateUsers(userToActivate, getLoggedInUser());
            verify(rules).ifsAdminCanReactivateUsers(userToActivate, getLoggedInUser());
            verify(rules).supportUserCanReactivateExternalUsers(userToActivate, getLoggedInUser());
            verifyNoMoreInteractions(rules);
        });
    }

    @Test
    public void reactivateUser() {

        UserResource userToActivate = newUserResource().build();

        when(lookup.findById(123L)).thenReturn(userToActivate);

        assertAccessDenied(() -> classUnderTest.activateUser(123L), () -> {
            verify(rules).systemRegistrationUserCanActivateUsers(userToActivate, getLoggedInUser());
            verify(rules).ifsAdminCanReactivateUsers(userToActivate, getLoggedInUser());
            verify(rules).supportUserCanReactivateExternalUsers(userToActivate, getLoggedInUser());
            verifyNoMoreInteractions(rules);
        });
    }


    @Test
    public void deactivateUser() {

        UserResource userToActivate = newUserResource().build();

        when(lookup.findById(123L)).thenReturn(userToActivate);

        assertAccessDenied(() -> classUnderTest.deactivateUser(123L), () -> {
            verify(rules).ifsAdminCanDeactivateUsers(userToActivate, getLoggedInUser());
            verify(rules).supportUserCanDeactivateExternalUsers(userToActivate, getLoggedInUser());
            verify(rules).systemMaintenanceUserCanDeactivateUsers(userToActivate, getLoggedInUser());
            verifyNoMoreInteractions(rules);
        });
    }

    @Test
    public void resendUserVerificationEmail() {
        final UserResource userToSendVerificationEmail = newUserResource().build();

        assertAccessDenied(
                () -> classUnderTest.resendUserVerificationEmail(userToSendVerificationEmail),
                () -> {
                    verify(rules).systemRegistrationUserCanSendUserVerificationEmail(userToSendVerificationEmail,
                            getLoggedInUser());
                    verifyNoMoreInteractions(rules);
                });
    }

    @Test
    public void editInternalUser() {
        UserResource userToEdit = UserResourceBuilder.newUserResource().build();

        assertAccessDenied(
                () -> classUnderTest.editInternalUser(userToEdit, Role.SUPPORT),
                () -> {
                    verify(rules).ifsAdminCanEditInternalUser(userToEdit, getLoggedInUser());
                    verifyNoMoreInteractions(rules);
                });
    }

    @Override
    protected Class<? extends RegistrationService> getClassUnderTest() {
        return RegistrationServiceImpl.class;
    }
}
