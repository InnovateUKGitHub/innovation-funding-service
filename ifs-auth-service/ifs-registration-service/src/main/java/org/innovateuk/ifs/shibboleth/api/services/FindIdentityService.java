package org.innovateuk.ifs.shibboleth.api.services;

import org.innovateuk.ifs.shibboleth.api.models.Identity;

import java.util.Optional;
import java.util.UUID;

public interface FindIdentityService {

    Identity getIdentity(UUID uuid);

    Optional<Identity> findByEmail(String email);

}
