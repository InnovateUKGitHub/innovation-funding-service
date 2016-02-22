package com.worth.ifs.commons.error.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by rav on 18/02/2016.
 *
 */
@ResponseStatus(value= HttpStatus.FORBIDDEN, reason="User is forbidden from performing requested action")
public class ForbiddenActionException extends AccessDeniedException {
    public ForbiddenActionException(String msg) {
        super(msg);
    }

    public ForbiddenActionException(String msg, Throwable t) {
        super(msg, t);
    }
}
