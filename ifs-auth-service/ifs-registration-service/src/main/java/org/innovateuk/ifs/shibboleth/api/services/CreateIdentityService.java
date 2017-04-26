package org.innovateuk.ifs.shibboleth.api.services;

import org.innovateuk.ifs.shibboleth.api.exceptions.DuplicateEmailException;
import org.innovateuk.ifs.shibboleth.api.exceptions.InvalidPasswordException;
import org.innovateuk.ifs.shibboleth.api.models.Identity;

public interface CreateIdentityService {

    Identity createIdentity(String email, String password) throws DuplicateEmailException, InvalidPasswordException;

}
