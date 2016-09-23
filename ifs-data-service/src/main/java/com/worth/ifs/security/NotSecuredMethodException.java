package com.worth.ifs.security;

import com.worth.ifs.commons.security.NotSecured;

/**
 * Exception thrown when a method marked as {@link NotSecured} with {@link NotSecured#mustBeSecuredByOtherServices()} set to true
 * is called but it is not secured higher in the stack.
 */
public class NotSecuredMethodException extends RuntimeException {
    public NotSecuredMethodException(String message){
        super(message);
    }
}
