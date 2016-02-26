package com.worth.ifs.application.form;

import javax.validation.Valid;

public abstract class CreateApplicationForm extends Form {
    @Valid
    private AddressForm addressForm = new AddressForm();
    private boolean triedToSave = false;

    public AddressForm getAddressForm() {
        return addressForm;
    }

    public void setAddressForm(AddressForm addressForm) {
        this.addressForm = addressForm;
    }

    public boolean isTriedToSave() {
        return triedToSave;
    }

    public void setTriedToSave(boolean triedToSave) {
        this.triedToSave = triedToSave;
    }

}

