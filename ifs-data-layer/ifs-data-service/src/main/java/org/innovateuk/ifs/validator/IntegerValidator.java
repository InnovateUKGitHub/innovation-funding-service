package org.innovateuk.ifs.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.math.BigDecimal;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import static java.math.BigDecimal.valueOf;
import static org.innovateuk.ifs.commons.rest.ValidationMessages.rejectValue;

/**
 * This class validates the FormInputResponse, it checks that there is a number present and that it is an integer.
 */
@Component
public class IntegerValidator extends BaseValidator {
    private static final Log LOG = LogFactory.getLog(IntegerValidator.class);

    @Override
    public final void validate(Object target, Errors errors) {
        FormInputResponse response = (FormInputResponse) target;
        String responseValue = response.getValue();
        BigDecimal value = toBigDecimal(responseValue);
        if (value == null){
            rejectValue(errors, "value", "validation.field.must.not.be.blank");
        }
        else {
            if (fractionalPartLength(value) > 0){
                rejectValue(errors, "value", "validation.standard.integer.non.decimal.format");
            }
            if (value.compareTo(valueOf(MAX_VALUE)) > 0){
                rejectValue(errors, "value", "validation.standard.integer.max.value.format");
            }
            if (value.compareTo(valueOf(MIN_VALUE)) < 0){
                rejectValue(errors, "value", "validation.standard.integer.min.value.format");
            }

            validate(value, errors);
        }
    }

    protected void validate(BigDecimal bd, Errors errors){

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
