package com.worth.ifs.validator;

import com.worth.ifs.form.domain.FormInputResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;


@Component
public class EmailValidator implements BaseValidator {
    private final Log log = LogFactory.getLog(getClass());

    @Override
    public boolean supports(Class<?> clazz) {
        return FormInputResponse.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        log.debug("do Email validation ");
        FormInputResponse response = (FormInputResponse) target;

        if(!response.getValue().contains("@")){
            log.debug("Email validation message for: " + response.getId());
            errors.rejectValue("value", "response.invalidEmail", "Please enter a valid emailaddress");
        }
    }
}
