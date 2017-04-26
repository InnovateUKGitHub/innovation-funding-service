package org.innovateuk.ifs.shibboleth.api.services;

import org.innovateuk.ifs.shibboleth.api.mocks.MockedService;
import org.junit.Test;

import java.util.UUID;

public class FindIdentityServiceTest extends MockedService<FindIdentityService> {

    @Test
    public void shouldAllowFindingByEmailWithValidDetails() {
        
        final String email = "valid@email.com";

        setupFindingIdentityByEmail(email);

        getService().findByEmail(email);

        verifyFindingIdentityByEmail();
    }


    @Test
    public void shouldAllowFindingByUuidWithValidDetails() {

        final UUID uuid = UUID.randomUUID();
        final String email = "valid@email.com";
        final String password = "some-really-complex-password";

        setupFindingIdentityByUuid(uuid, email, password);

        getService().getIdentity(uuid);

        verifyFindingIdentityByUuid();
    }

}
