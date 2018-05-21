package org.innovateuk.ifs.shibboleth.api.exceptions;

import org.innovateuk.ifs.shibboleth.api.models.ErrorResponse;

import java.util.List;
import java.util.stream.Collectors;


public class InvalidPasswordException extends RestResponseException {

    private static final String ERROR_KEY = "INVALID_PASSWORD";
    private final List<String> problems;

    public InvalidPasswordException(final List<String> problems) {
        super(ERROR_KEY, problems);

        this.problems = problems;
    }

    public List<ErrorResponse> getErrorResponses() {
        return problems
                .stream()
                .map(ErrorResponse::new)
                .collect(Collectors.toList());
    }
}
