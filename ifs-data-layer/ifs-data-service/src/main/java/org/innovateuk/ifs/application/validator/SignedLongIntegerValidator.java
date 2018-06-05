package org.innovateuk.ifs.application.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.math.BigDecimal;

/**
 * This class validates the FormInputResponse, it checks that there is a number present and that it is an long integer.
 */
@Component
public class SignedLongIntegerValidator extends IntegerValidator {

    @Override
    protected void validate(BigDecimal bd, Errors errors) {
        // use default validation

    }
}