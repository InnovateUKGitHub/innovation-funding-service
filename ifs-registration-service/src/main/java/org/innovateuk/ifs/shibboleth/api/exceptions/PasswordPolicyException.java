package org.innovateuk.ifs.shibboleth.api.exceptions;

public class PasswordPolicyException extends RestResponseException {

    public static final String ERROR_KEY = "PASSWORD_POLICY_ERROR";


    public PasswordPolicyException() {
        super(ERROR_KEY);
    }

}
