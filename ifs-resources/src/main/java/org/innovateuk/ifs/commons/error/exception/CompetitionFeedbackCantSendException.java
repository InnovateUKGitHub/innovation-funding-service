package org.innovateuk.ifs.commons.error.exception;

import java.util.List;

/**
 * Represents error raised by data layer when releasing a competition's feedback cannot occur
 */
public class CompetitionFeedbackCantSendException extends IFSRuntimeException {

    public CompetitionFeedbackCantSendException(String message, List<Object> arguments) {
        super(message, arguments);
    }
}
