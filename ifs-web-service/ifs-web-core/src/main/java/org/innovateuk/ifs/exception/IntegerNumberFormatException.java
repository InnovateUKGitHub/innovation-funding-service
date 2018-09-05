package org.innovateuk.ifs.exception;


public class IntegerNumberFormatException extends IllegalArgumentException {

    @Override
    public String getMessage() {
        return "validation.finance.exception.integer";
    }

    public IntegerNumberFormatException(String s) {
        super(s);
    }
}
