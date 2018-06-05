package org.innovateuk.ifs.application.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;
import static org.innovateuk.ifs.commons.error.ValidationMessages.rejectValue;

/**
 * This class validates the FormInputResponse, it checks that there is a number present and that it is an non negative long integer.
 */
@Component
public class NonNegativeLongIntegerValidator extends IntegerValidator {

    @Override
    protected void validate(BigDecimal value, Errors errors) {
        if (ZERO.compareTo(value) > 0){
            rejectValue(errors, "value", "validation.standard.non.negative.integer.non.negative.format");
        }
    }
}
