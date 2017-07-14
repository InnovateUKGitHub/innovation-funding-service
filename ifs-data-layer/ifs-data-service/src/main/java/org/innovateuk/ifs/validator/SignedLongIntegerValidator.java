package org.innovateuk.ifs.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.math.BigDecimal;

/**
 * This class validates the FormInputResponse, it checks that there is a number present and that it is an long integer.
 */
@Component
public class SignedLongIntegerValidator extends IntegerValidator {
    private static final Log LOG = LogFactory.getLog(SignedLongIntegerValidator.class);

    @Override
    protected void validate(BigDecimal bd, Errors errors) {
        // use default validation

    }
}