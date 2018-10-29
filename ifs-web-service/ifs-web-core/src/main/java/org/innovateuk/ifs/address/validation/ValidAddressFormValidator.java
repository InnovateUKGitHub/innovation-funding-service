package org.innovateuk.ifs.address.validation;

import org.innovateuk.ifs.address.form.AddressForm;
import org.innovateuk.ifs.address.resource.AddressResource;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.google.common.base.Strings.isNullOrEmpty;

public class ValidAddressFormValidator implements ConstraintValidator<ValidAddressForm, AddressForm> {

    @Override
    public void initialize(ValidAddressForm constraintAnnotation) {
    }

    @Override
    public boolean isValid(AddressForm value, ConstraintValidatorContext context) {
        boolean valid;
        if (value.isManualAddress()) {
            valid = isAddressValid(value.getAddress(), context);
        } else if (isNullOrEmpty(value.getPostcodeInput())) {
            valid = false;
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate("{validation.field.must.not.be.blank}")
                    .addPropertyNode("postcodeInput").addConstraintViolation();

        } else {
            valid = true;
        }
        return valid;

    }

    private boolean isAddressValid(AddressResource address, ConstraintValidatorContext context) {
        boolean valid = true;
        if (isNullOrEmpty(address.getAddressLine1())) {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate("{validation.standard.addressline1.required}")
                    .addPropertyNode("address.addressLine1").addConstraintViolation();
            valid = false;
        }
        if (isNullOrEmpty(address.getTown())) {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate("{validation.standard.town.required}")
                    .addPropertyNode("address.town").addConstraintViolation();
            valid = false;
        }
        if (isNullOrEmpty(address.getPostcode())) {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate("{validation.standard.postcode.required}")
                    .addPropertyNode("address.postcode").addConstraintViolation();
            valid = false;
        } else if (address.getPostcode().length() >= 9) {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate("{validation.standard.postcode.length}")
                    .addPropertyNode("address.postcode").addConstraintViolation();
            valid = false;
        }
        return valid;
    }
}
