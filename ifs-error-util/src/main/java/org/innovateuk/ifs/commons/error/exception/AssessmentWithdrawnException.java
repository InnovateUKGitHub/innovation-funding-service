package org.innovateuk.ifs.commons.error.exception;

import java.util.List;

/**
 * Represents error raised by data layer when an assessor attempts to retrieve an assessment that has been withdrawn.
 */
public class AssessmentWithdrawnException extends IFSRuntimeException {

    public AssessmentWithdrawnException(String message, List<Object> arguments) {
        super(message, arguments);
    }
}