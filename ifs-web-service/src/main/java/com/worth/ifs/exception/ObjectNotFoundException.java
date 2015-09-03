package com.worth.ifs.exception;

//@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="No such application")  // 404
public class ObjectNotFoundException extends RuntimeException {


    public ObjectNotFoundException(String message) {
        super(message);
    }
}