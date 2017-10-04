package org.innovateuk.ifs.shibboleth.api.models.validators;

import org.innovateuk.ifs.shibboleth.api.exceptions.DuplicateEmailException;
import org.innovateuk.ifs.shibboleth.api.models.Identity;
import org.innovateuk.ifs.shibboleth.api.services.FindIdentityService;

import java.util.Optional;

public class EmailValidator implements Validator<String, DuplicateEmailException> {

    private final FindIdentityService findIdentityService;

    public EmailValidator(final FindIdentityService findIdentityService) {
        this.findIdentityService = findIdentityService;
    }

    @Override
    public void validate(final String email) throws DuplicateEmailException {

        final Optional<Identity> byEmail = findIdentityService.findByEmail(email);

        // TODO - consider an update to the current identity that doesn't actually change anything.

        if (byEmail.isPresent()) {
            throw new DuplicateEmailException();
        }
    }

}
