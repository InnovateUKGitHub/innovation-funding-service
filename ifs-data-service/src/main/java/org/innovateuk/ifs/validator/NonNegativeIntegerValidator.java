package org.innovateuk.ifs.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.math.BigDecimal;

import static java.lang.Integer.MAX_VALUE;
import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static org.innovateuk.ifs.commons.rest.ValidationMessages.rejectValue;

/**
 * This class validates the FormInputResponse, it checks that there is a number present and that it is an non negative integer.
 */
@Component
public class NonNegativeIntegerValidator extends BaseValidator {
    private static final Log LOG = LogFactory.getLog(NonNegativeIntegerValidator.class);

    @Override
    public void validate(Object target, Errors errors) {
        FormInputResponse response = (FormInputResponse) target;
        String responseValue = response.getValue();
        BigDecimal value = toBigDecimal(responseValue);
        if (value == null){
            rejectValue(errors, "value", "validation.standard.non.negative.integer.format");
        }
        else {
            if (fractionalPartLength(value) > 0){
                rejectValue(errors, "value", "validation.standard.non.negative.integer.non.decimal.format");
            }
            if (ZERO.compareTo(value) > 0){
                rejectValue(errors, "value", "validation.standard.non.negative.integer.non.negative.format");
            }
            if (value.compareTo(valueOf(MAX_VALUE)) > 0){
                rejectValue(errors, "value", "validation.standard.non.negative.integer.max.value.format");
            }
        }
    }

    private BigDecimal toBigDecimal(String value) {
        try {
            return new BigDecimal(value);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    private int fractionalPartLength(BigDecimal value){
        int fractionPartLength = value.scale() < 0 ? 0 : value.scale();
        return fractionPartLength;
    }
}
