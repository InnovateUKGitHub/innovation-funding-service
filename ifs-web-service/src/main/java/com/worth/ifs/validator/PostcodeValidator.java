package com.worth.ifs.validator;

import com.worth.ifs.address.service.AddressRestService;
import com.worth.ifs.validator.constraints.Postcode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class PostcodeValidator implements ConstraintValidator<Postcode, String> {
    private String message;

    @Autowired
    private AddressRestService addressRestService;

    @Override
    public void initialize(Postcode constraintAnnotation) {
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean valid = addressRestService.validatePostcode(value).handleSuccessOrFailure(f -> false, s -> s);

        if(!value.isEmpty() && !valid) {
            addConstraintViolationMessageToField(context);
        }

        return valid;
    }

    private void addConstraintViolationMessageToField(ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
