package com.worth.ifs.validator;

import com.worth.ifs.form.domain.FormInputResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

/**
 * This class validates the FormInputResponse, it checks if there is a emailaddress present.
 */
@Component
public class EmailValidator extends BaseValidator {
    private final Log log = LogFactory.getLog(getClass());


    @Override
    public void validate(Object target, Errors errors) {
        log.debug("do Email validation ");
        FormInputResponse response = (FormInputResponse) target;
        CharSequence responseValue = response.getValue();


        org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator externEmailValidator = new org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator();
        if (!externEmailValidator.isValid(responseValue, null)) {
            errors.rejectValue("value", "response.invalidEmail", "Please enter a valid emailaddress");
        }
    }
}
