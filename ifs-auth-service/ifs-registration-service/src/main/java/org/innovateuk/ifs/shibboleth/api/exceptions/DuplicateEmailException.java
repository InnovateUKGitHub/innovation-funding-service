package org.innovateuk.ifs.shibboleth.api.exceptions;

public class DuplicateEmailException extends RestResponseException {

    public static final String ERROR_KEY = "DUPLICATE_EMAIL_ADDRESS";


    public DuplicateEmailException() {
        super(ERROR_KEY);
    }

}
