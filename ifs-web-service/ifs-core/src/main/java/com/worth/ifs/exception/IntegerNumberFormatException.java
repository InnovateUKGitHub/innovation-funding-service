package com.worth.ifs.exception;


public class IntegerNumberFormatException extends NumberFormatException {
    public IntegerNumberFormatException() {
    }

    @Override
    public String getMessage() {
        return "validation.finance.exception.integer";
    }

    public IntegerNumberFormatException(String s) {
        super(s);
    }
}
