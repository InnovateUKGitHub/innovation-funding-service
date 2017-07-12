package org.innovateuk.ifs.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static org.innovateuk.ifs.commons.rest.ValidationMessages.rejectValue;

/**
 * This class validates the FormInputResponse, it checks that there is a number present and that it is an non negative long integer.
 */
@Component
public class NonNegativeLongIntegerValidator extends LongIntegerValidator {
    private static final Log LOG = LogFactory.getLog(NonNegativeLongIntegerValidator.class);

    @Override
    protected void validate(BigDecimal value, Errors errors) {
        if (ZERO.compareTo(value) > 0){
            rejectValue(errors, "value", "validation.standard.non.negative.integer.non.negative.format");
        }
    }
}
