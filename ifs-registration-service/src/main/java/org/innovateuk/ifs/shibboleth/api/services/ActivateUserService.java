package org.innovateuk.ifs.shibboleth.api.services;

import java.util.UUID;

/**
 * Created by jbritton on 09/03/16.
 */
public interface ActivateUserService {

    void activateUser(UUID uuid);

    void deactivateUser(UUID uuid);
}
