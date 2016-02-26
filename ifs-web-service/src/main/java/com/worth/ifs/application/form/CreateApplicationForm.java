package com.worth.ifs.application.form;


import com.worth.ifs.address.resource.AddressResource;

import java.util.List;

public abstract class CreateApplicationForm extends Form {
    private boolean triedToSave = false;

    public abstract String getPostcodeInput();

    public abstract void setPostcodeOptions(List<AddressResource> addresses);

    public abstract List<AddressResource> getPostcodeOptions();

    public abstract String getSelectedPostcodeIndex();

    public abstract void setSelectedPostcode(AddressResource address);

    public boolean isTriedToSave() {
        return triedToSave;
    }

    public void setTriedToSave(boolean triedToSave) {
        this.triedToSave = triedToSave;
    }
}

