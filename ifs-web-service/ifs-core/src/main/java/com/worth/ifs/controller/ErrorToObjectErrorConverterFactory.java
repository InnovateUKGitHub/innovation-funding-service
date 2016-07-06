package com.worth.ifs.controller;

import org.springframework.validation.FieldError;

/**
 * TODO DW - document this class
 */
public class ErrorToObjectErrorConverterFactory {

    public static ErrorToObjectErrorConverter toField(String field) {
        return e -> new FieldError("", field, e.getErrorMessage());
    }

}
