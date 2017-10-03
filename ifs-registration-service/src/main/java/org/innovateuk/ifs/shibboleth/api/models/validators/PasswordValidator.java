package org.innovateuk.ifs.shibboleth.api.models.validators;

import org.innovateuk.ifs.shibboleth.api.PasswordPolicyProperties;
import org.innovateuk.ifs.shibboleth.api.exceptions.InvalidPasswordException;

public class PasswordValidator implements Validator<String, InvalidPasswordException> {

    private final PasswordPolicyProperties passwordPolicy;

    public PasswordValidator(final PasswordPolicyProperties passwordPolicy) {
        this.passwordPolicy = passwordPolicy;
    }

    @Override
    public void validate(final String password) throws InvalidPasswordException {

        final String passwordLowerCase = password.toLowerCase();
        
        if(passwordPolicy.getBlacklist().stream().anyMatch(passwordLowerCase::contains)) {
            throw new InvalidPasswordException("blacklisted");
        }

    }

}
