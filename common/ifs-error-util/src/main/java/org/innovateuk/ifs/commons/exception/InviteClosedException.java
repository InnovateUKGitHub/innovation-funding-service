package org.innovateuk.ifs.commons.exception;

import java.util.List;

public class InviteClosedException extends IFSRuntimeException {

    public InviteClosedException(String message, List<Object> arguments) {
        super(message, arguments);
    }
}
