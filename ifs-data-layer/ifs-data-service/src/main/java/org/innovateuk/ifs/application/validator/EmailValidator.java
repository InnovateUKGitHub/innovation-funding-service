package org.innovateuk.ifs.application.validator;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.application.domain.FormInputResponse;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import static org.innovateuk.ifs.commons.error.ValidationMessages.rejectValue;

/**
 * This class validates the FormInputResponse, it checks if there is a email address present.
 *
 * The hibernate validator uses these specs:
 * the specification of a valid email can be found in
 * <a href="http://www.faqs.org/rfcs/rfc2822.html">RFC 2822</a>
 */
@Slf4j
@Component
public class EmailValidator extends BaseValidator {

    @Override
    public void validate(Object target, Errors errors) {
        log.debug("do Email validation ");
        FormInputResponse response = (FormInputResponse) target;
        CharSequence responseValue = response.getValue();


        org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator externalEmailValidator = new org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator();
        if (!externalEmailValidator.isValid(responseValue, null)) {
            rejectValue(errors, "value", "validation.standard.email.format");
        }
    }
}
