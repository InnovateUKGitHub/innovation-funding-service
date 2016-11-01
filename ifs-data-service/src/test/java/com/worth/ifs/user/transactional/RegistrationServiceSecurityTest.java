package com.worth.ifs.user.transactional;

import com.worth.ifs.BaseServiceSecurityTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.registration.resource.UserRegistrationResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.security.UserLookupStrategies;
import com.worth.ifs.user.security.UserPermissionRules;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.method.P;

import java.util.Optional;

import static com.worth.ifs.registration.builder.UserRegistrationResourceBuilder.newUserRegistrationResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static java.util.Optional.of;
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
    public void testCreateUser() {
        UserRegistrationResource userToCreate = newUserRegistrationResource().build();

        assertAccessDenied(() -> classUnderTest.createUser(userToCreate), () -> {
            verify(rules).systemRegistrationUserCanCreateUsers(userToCreate, getLoggedInUser());
            verifyNoMoreInteractions(rules);
        });
    }

    @Test
    public void testCreateOrganisationUser() {

        UserResource userToCreate = newUserResource().build();

        assertAccessDenied(() -> classUnderTest.createOrganisationUser(123L, userToCreate), () -> {
            verify(rules).systemRegistrationUserCanCreateUsers(userToCreate, getLoggedInUser());
            verifyNoMoreInteractions(rules);
        });
    }

    @Test
    public void testActivateUser() {

        UserResource userToActivate = newUserResource().build();

        when(lookup.findById(123L)).thenReturn(userToActivate);

        assertAccessDenied(() -> classUnderTest.activateUser(123L), () -> {
            verify(rules).systemRegistrationUserCanActivateUsers(userToActivate, getLoggedInUser());
            verifyNoMoreInteractions(rules);
        });
    }

    @Test
    public void testSendUserVerificationEmail() throws Exception {
        final UserResource userToSendVerificationEmail = newUserResource().build();

        assertAccessDenied(
                () -> classUnderTest.sendUserVerificationEmail(userToSendVerificationEmail, of(123L)),
                () -> {
                    verify(rules).systemRegistrationUserCanSendUserVerificationEmail(userToSendVerificationEmail, getLoggedInUser());
                    verifyNoMoreInteractions(rules);
                });
    }

    @Test
    public void testResendUserVerificationEmail() throws Exception {
        final UserResource userToSendVerificationEmail = newUserResource().build();

        assertAccessDenied(
                () -> classUnderTest.resendUserVerificationEmail(userToSendVerificationEmail),
                () -> {
                    verify(rules).systemRegistrationUserCanSendUserVerificationEmail(userToSendVerificationEmail, getLoggedInUser());
                    verifyNoMoreInteractions(rules);
                });
    }

    @Override
    protected Class<? extends RegistrationService> getClassUnderTest() {
        return TestRegistrationService.class;
    }

    public static class TestRegistrationService implements RegistrationService {

        @Override
        public ServiceResult<UserResource> createUser(@P("user") UserRegistrationResource userResource) {
            return null;
        }

        @Override
        public ServiceResult<UserResource> createOrganisationUser(Long organisationId, UserResource userResource) {
            return null;
        }

        @Override
        public ServiceResult<Void> activateUser(Long userId) {
            return null;
        }

        @Override
        public ServiceResult<Void> sendUserVerificationEmail(UserResource user, Optional<Long> competitionId) {
            return null;
        }

        @Override
        public ServiceResult<Void> resendUserVerificationEmail(UserResource user) {
            return null;
        }
    }
}