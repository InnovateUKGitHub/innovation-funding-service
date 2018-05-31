package org.innovateuk.ifs.commons.error.exception;

import java.util.List;

public class InviteExpiredException extends IFSRuntimeException {

    public InviteExpiredException(String message, List<Object> arguments) {
        super(message, arguments);
    }
}
