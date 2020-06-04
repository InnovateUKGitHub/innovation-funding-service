package org.innovateuk.ifs.address.validator;

import org.innovateuk.ifs.address.domain.Address;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class InternationalPostcodeValidator implements ConstraintValidator<InternationalPostcode, Address> {

    @Override
    public void initialize(final InternationalPostcode annotation) {
    }

    @Override
    public boolean isValid(Address address, final ConstraintValidatorContext context) {
        if (address.getCountry() == null && address.getPostcode().length() > 9) {
            return false;
        }

        if (address.getCountry()!= null && address.getPostcode() == null) {
            return true;
        }

        if (address.getCountry()!= null && address.getPostcode().length() >= 9) {
            return true;
        }

        return true;
    }
}
