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

public class CreateIdentityServiceTest extends MockedService<CreateIdentityService> {

    @Test
    public void shouldAllowCreationWithValidDetails() throws InvalidPasswordException, DuplicateEmailException {

        final UUID uuid = UUID.randomUUID();
        final String email = "valid@email.com";
        final String password = "some-really-complex-P@55w0rd";

        setupFindingIdentityByUuid(uuid, email, password);

        getService().createIdentity(email, password);

        verify(ldapTemplate).create(isA(Identity.class));
        verifyFindingIdentityByUuid();
        verifyFindingIdentityByEmail();
        verifyBlacklistedPassworsLookedUp();
    }


    @Test
    public void shouldNotAllowDuplicateEmailAddresses() {

        final String email = "duplicate@email.com";
        final String password = "some-really-complex-password";

        setupFindingIdentityByEmail(new Identity("duplicate@email.com", "some-other-complex-password", false));

        try {
            getService().createIdentity(email, password);
            
            assertThat("Service failed to throw expected exception.", false);
        } catch (final Exception exception) {
            assertThat(exception, is(instanceOf(DuplicateEmailException.class)));
        }

        verifyFindingIdentityByEmail();
    }


    @Test
    public void shouldNotAllowBlacklistedPasswords() {

        final String email = "valid@email.com";
        final String password = "blacklisted-password";

        try {
            getService().createIdentity(email, password);

            assertThat("Service failed to throw expected exception.", false);
        } catch (final Exception exception) {
            assertThatExceptionIsInvalidPasswordOfType(exception, "blacklisted");
        }

        verifyFindingIdentityByEmail();
        verifyBlacklistedPassworsLookedUp();
    }

}
