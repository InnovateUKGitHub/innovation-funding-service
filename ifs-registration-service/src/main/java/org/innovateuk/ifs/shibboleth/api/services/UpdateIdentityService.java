package org.innovateuk.ifs.shibboleth.api.services;

import org.innovateuk.ifs.shibboleth.api.exceptions.DuplicateEmailException;
import org.innovateuk.ifs.shibboleth.api.exceptions.InvalidPasswordException;

import java.util.UUID;

public interface UpdateIdentityService {

    void changePassword(UUID uuid, String password) throws InvalidPasswordException;

    void changeEmail(UUID uuid, String email) throws DuplicateEmailException;
}
