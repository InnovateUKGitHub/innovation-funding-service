package org.innovateuk.ifs.shibboleth.api.services;

import org.innovateuk.ifs.shibboleth.api.mocks.MockedService;
import org.innovateuk.ifs.shibboleth.api.models.Identity;
import org.junit.Test;

import java.util.UUID;

import static org.mockito.Mockito.*;

public class DeleteIdentityServiceTest extends MockedService<DeleteIdentityService> {

    @Test
    public void shouldAllowDeletingWithValidDetails() {

        final UUID uuid = UUID.randomUUID();
        final String email = "valid@email.com";
        final String password = "some-really-complex-password";

        setupFindingIdentityByUuid(uuid, email, password);

        getService().deleteIdentity(uuid);

        verify(ldapTemplate).delete(isA(Identity.class));
        verifyFindingIdentityByUuid();
    }

}
