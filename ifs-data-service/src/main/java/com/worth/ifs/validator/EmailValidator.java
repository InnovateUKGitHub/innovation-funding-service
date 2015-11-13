package com.worth.ifs.validator;

import com.worth.ifs.application.domain.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import javax.validation.ConstraintValidatorContext;

public class EmailValidator implements BaseValidator{
    @Autowired
    @Qualifier("emailValidator")
    org.hibernate.validator.internal.constraintvalidators.EmailValidator emailValidator;


    @Override
    public boolean supports(Class<?> clazz) {
        return String.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        String response = (String) target;
        ConstraintValidatorContext context = null;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "emailfield", "response.invalidEmailAddress", "Please enter an emailaddress");

        if(emailValidator.isValid(response, context)){
            errors.rejectValue("fieldnamex", "response.invalidEmailAddress", "Invalid email address");
        }

    }
}
