package org.innovateuk.ifs.address.validation;

import static com.google.common.base.Strings.isNullOrEmpty;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.innovateuk.ifs.address.form.AddressForm;
import org.innovateuk.ifs.address.resource.AddressResource;

public class ValidAddressFormValidator implements ConstraintValidator<ValidAddressForm, AddressForm> {

    @Override
    public void initialize(ValidAddressForm constraintAnnotation) {
    }

    @Override
    public boolean isValid(AddressForm value, ConstraintValidatorContext context) {
        boolean valid;
        if (value.getAction().equals(AddressForm.Action.SAVE)) {
            if (value.isManualAddressEntry()) {
                valid = isAddressValid(value.getManualAddress(), context);
            } else if (value.isPostcodeAddressEntry()) {
                if (value.getSelectedPostcodeIndex() == null || value.getSelectedPostcodeIndex() < 0) {
                    updateConstraintValidatorContext(context, "{validation.standard.address.select.required}", "selectedPostcodeIndex");
                    valid = false;
                } else {
                    valid = true;
                }
            } else {
                updateConstraintValidatorContext(context, "{validation.standard.address.required}", "postcodeInput");
                valid = false;
            }
        } else if (value.getAction().equals(AddressForm.Action.SEARCH_POSTCODE) &&
            isNullOrEmpty(value.getPostcodeInput())) {
            updateConstraintValidatorContext(context, "{validation.standard.address.search.postcode.required}", "postcodeInput");
            valid = false;
        } else {
            valid = true;
        }
        return valid;

    }

    private boolean isAddressValid(AddressResource address, ConstraintValidatorContext context) {
        boolean valid = true;
        if (isNullOrEmpty(address.getAddressLine1())) {
            updateConstraintValidatorContext(context, "{validation.standard.addressline1.required}", "manualAddress.addressLine1");
            valid = false;
        }
        if (isNullOrEmpty(address.getTown())) {
            updateConstraintValidatorContext(context, "{validation.standard.town.required}", "manualAddress.town");
            valid = false;
        }
        if (isNullOrEmpty(address.getPostcode())) {
            updateConstraintValidatorContext(context, "{validation.standard.postcode.required}", "manualAddress.postcode");
            valid = false;
        } else if (address.getPostcode().length() >= 9) {
            updateConstraintValidatorContext(context, "{validation.standard.postcode.length}", "manualAddress.postcode");
            valid = false;
        }
        return valid;
    }

    private void updateConstraintValidatorContext(ConstraintValidatorContext context, String messageTemplate, String propertyNodeName) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(messageTemplate).addPropertyNode(propertyNodeName).addConstraintViolation();
    }
}
