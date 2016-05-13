package com.worth.ifs.exception;

public class BigDecimalNumberFormatException extends NumberFormatException {
    public BigDecimalNumberFormatException() {
    }

    @Override
    public String getMessage() {
        return "validation.finance.exception.bigdecimal";
    }

    public BigDecimalNumberFormatException(String s) {
        super(s);
    }
}
