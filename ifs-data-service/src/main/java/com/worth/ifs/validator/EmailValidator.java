package com.worth.ifs.validator;

import com.worth.ifs.form.domain.FormInputResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import static com.worth.ifs.commons.rest.ValidationMessages.rejectValue;

/**
 * This class validates the FormInputResponse, it checks if there is a emailaddress present.
 *
 * The hibernate validator uses these specs:
 * the specification of a valid email can be found in
 * <a href="http://www.faqs.org/rfcs/rfc2822.html">RFC 2822</a>
 */
@Component
public class EmailValidator extends BaseValidator {
    private static final Log LOG = LogFactory.getLog(EmailValidator.class);

    @Override
    public void validate(Object target, Errors errors) {
        LOG.debug("do Email validation ");
        FormInputResponse response = (FormInputResponse) target;
        CharSequence responseValue = response.getValue();


        org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator externalEmailValidator = new org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator();
        if (!externalEmailValidator.isValid(responseValue, null)) {
            rejectValue(errors, "value", "validation.standard.email.format");
        }
    }
}
