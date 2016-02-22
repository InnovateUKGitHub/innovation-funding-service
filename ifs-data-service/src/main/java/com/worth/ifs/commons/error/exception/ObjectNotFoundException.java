package com.worth.ifs.commons.error.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This object is used when a request to the RestServices returns a null value,
 * so the object is not found on the date-service side. We can then handle this
 * exception in a way to show the error message to the user.
 */
@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Unable to find requested entity")  // 404
public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(String message) {
        super(message);
    }
}