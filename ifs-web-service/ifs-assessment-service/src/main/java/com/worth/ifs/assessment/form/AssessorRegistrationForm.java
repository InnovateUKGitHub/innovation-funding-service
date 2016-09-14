package com.worth.ifs.assessment.form;

import com.worth.ifs.form.AddressForm;
import com.worth.ifs.registration.form.RegistrationForm;

import javax.validation.Valid;

/**
 * Created by wouter on 13/09/2016.
 */
public class AssessorRegistrationForm extends RegistrationForm {


    @Valid
    private AddressForm addressForm = new AddressForm();

    public AddressForm getAddressForm() {
        return addressForm;
    }

    public void setAddressForm(AddressForm addressForm) {
        this.addressForm = addressForm;
    }
    private boolean useSearchResultAddress = false;

    public boolean isUseSearchResultAddress() {
        return useSearchResultAddress;
    }

    public void setUseSearchResultAddress(boolean useSearchResultAddress) {
        this.useSearchResultAddress = useSearchResultAddress;
    }
}
