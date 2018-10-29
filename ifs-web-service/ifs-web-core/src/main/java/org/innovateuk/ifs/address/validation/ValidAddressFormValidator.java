package org.innovateuk.ifs.address.validation;

import org.innovateuk.ifs.address.form.AddressForm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidAddressFormValidator implements ConstraintValidator<ValidAddressForm, AddressForm> {

    @Override
    public void initialize(ValidAddressForm constraintAnnotation) { }

    @Override
    public boolean isValid(AddressForm value, ConstraintValidatorContext context) {
        return true;


//        context.disableDefaultConstraintViolation();
//        context
//                .buildConstraintViolationWithTemplate( "{my.custom.template}" )
//                .addPropertyNode( "postcodeInput" ).addConstraintViolation();
//        return false;
    }
}
