package org.innovateuk.ifs.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.form.domain.FormInputResponse;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

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
        if (!isNonNegativeInteger(responseValue)) {
            rejectValue(errors, "value", "validation.standard.non.negative.integer.format");
        }
    }

    private boolean isNonNegativeInteger(String value){
        if (value!= null) {
            try {
                Integer intValue = Integer.valueOf(value);
                if (intValue > 0){
                    return true;
                }
            } catch (NumberFormatException e) {
            }
        }
        return false;
    }
}
