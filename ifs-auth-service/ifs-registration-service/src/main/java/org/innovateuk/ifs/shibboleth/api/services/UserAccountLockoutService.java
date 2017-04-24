package org.innovateuk.ifs.shibboleth.api.services;

import org.innovateuk.ifs.shibboleth.api.exceptions.PasswordPolicyException;

import java.util.UUID;

/**
 * Created by jbritton on 29/04/16.
 */
public interface UserAccountLockoutService {

    boolean getAccountLockStatus(final UUID uuid) throws PasswordPolicyException;

    void unlockAccount(final UUID uuid);
}
