package org.innovateuk.ifs.shibboleth.api.exceptions;

import java.util.Collections;
import java.util.List;

public class InvalidPasswordException extends RestResponseException {

    public static final String ERROR_KEY = "INVALID_PASSWORD";

    public InvalidPasswordException(final String problem) {
        this(Collections.singletonList(problem));
    }

    public InvalidPasswordException(final List<String> problems) {
        super(ERROR_KEY, problems);
    }

}
