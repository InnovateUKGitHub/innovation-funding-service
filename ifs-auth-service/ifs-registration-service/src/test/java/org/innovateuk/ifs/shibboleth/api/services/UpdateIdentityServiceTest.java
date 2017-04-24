package org.innovateuk.ifs.shibboleth.api.services;

import org.innovateuk.ifs.shibboleth.api.exceptions.DuplicateEmailException;
import org.innovateuk.ifs.shibboleth.api.exceptions.InvalidPasswordException;
import org.innovateuk.ifs.shibboleth.api.mocks.MockedService;
import org.innovateuk.ifs.shibboleth.api.models.Identity;
import org.junit.Test;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

public class UpdateIdentityServiceTest extends MockedService<UpdateIdentityService> {

    @Test
    public void shouldAllowChangePasswordWithValidPassword() throws InvalidPasswordException, DuplicateEmailException {

        final UUID uuid = UUID.randomUUID();
        final String email = "valid@email.com";
        final String password = "some-really-complex-P@55w0rd";

        setupFindingIdentityByUuid(uuid, email, password);

        getService().changePassword(uuid, password);

        verifyFindingIdentityByUuid();
        verify(ldapTemplate).update(isA(Identity.class));
        verifyBlacklistedPassworsLookedUp();
    }


    @Test
    public void shouldAllowChangeEmailWithValidEmail() throws InvalidPasswordException, DuplicateEmailException {

        final UUID uuid = UUID.randomUUID();
        final String email = "valid@email.com";
        final String password = "some-really-complex-password";

        setupFindingIdentityByUuid(uuid, email, password);

        getService().changeEmail(uuid, email);

        verifyFindingIdentityByUuid();
        verify(ldapTemplate).update(isA(Identity.class));
        verifyFindingIdentityByEmail();
    }


    @Test
    public void shouldNotAllowDuplicateEmailAddresses() {

        final UUID uuid = UUID.randomUUID();
        final String email = "duplicate@email.com";
        final String password = "some-really-complex-password";

        setupFindingIdentityByUuid(uuid, email, password);
        setupFindingIdentityByEmail(new Identity("duplicate@email.com", "some-other-complex-password", false));

        try {
            getService().changeEmail(uuid, email);

            assertThat("Service failed to throw expected exception.", false);
        } catch (final Exception exception) {
            assertThat(exception, is(instanceOf(DuplicateEmailException.class)));
        }

        verifyFindingIdentityByUuid();
        verifyFindingIdentityByEmail();
    }


    @Test
    public void shouldNotAllowBlacklistedPasswords() {

        final UUID uuid = UUID.randomUUID();
        final String email = "valid@email.com";
        final String password = "blacklisted-password";

        setupFindingIdentityByUuid(uuid, email, password);

        try {
            getService().changePassword(uuid, password);

            assertThat("Service failed to throw expected exception.", false);
        } catch (final Exception exception) {
            assertThatExceptionIsInvalidPasswordOfType(exception, "blacklisted");
        }

        verifyFindingIdentityByUuid();
        verifyBlacklistedPassworsLookedUp();
        verifyNoMoreInteractions(ldapTemplate, passwordPolicy);
    }

}
