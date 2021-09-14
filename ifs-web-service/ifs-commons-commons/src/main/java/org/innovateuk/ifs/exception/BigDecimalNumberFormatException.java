package org.innovateuk.ifs.exception;

public class BigDecimalNumberFormatException extends IllegalArgumentException {

    @Override
    public String getMessage() {
        return "validation.finance.exception.bigdecimal";
    }

    public BigDecimalNumberFormatException(String s) {
        super(s);
    }
}
