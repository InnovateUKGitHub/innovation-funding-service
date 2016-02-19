package com.worth.ifs.commons.error.exception;

import org.springframework.security.access.AccessDeniedException;

/**
 * Created by rav on 18/02/2016.
 *
 */
public class ForbiddenActionException extends AccessDeniedException {
    public ForbiddenActionException(String msg) {
        super(msg);
    }

    public ForbiddenActionException(String msg, Throwable t) {
        super(msg, t);
    }
}
