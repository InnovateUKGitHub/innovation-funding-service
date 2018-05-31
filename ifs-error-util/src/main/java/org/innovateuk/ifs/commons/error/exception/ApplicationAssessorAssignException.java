package org.innovateuk.ifs.commons.error.exception;

import java.util.List;

public class ApplicationAssessorAssignException extends IFSRuntimeException {

    public ApplicationAssessorAssignException(String message, List<Object> arguments) {
        super(message, arguments);
    }
}
