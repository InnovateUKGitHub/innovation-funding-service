package org.innovateuk.ifs.commons.exception;

import java.util.List;

public class InviteAlreadySentException extends IFSRuntimeException {

    public InviteAlreadySentException(String message, List<Object> arguments) {
        super(message, arguments);
    }
}
