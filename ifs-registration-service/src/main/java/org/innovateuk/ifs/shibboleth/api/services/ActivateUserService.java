package org.innovateuk.ifs.shibboleth.api.services;

import java.util.UUID;

public interface ActivateUserService {

    void activateUser(UUID uuid);

    void deactivateUser(UUID uuid);
}
