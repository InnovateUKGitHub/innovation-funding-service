package com.worth.ifs.application.form;

import com.worth.ifs.organisation.domain.Address;

import java.util.List;

public abstract class CreateApplicationForm  extends Form{
    private boolean triedToSave = false;

    public abstract String getPostcodeInput();

    public abstract void setPostcodeOptions(List<Address> addresses);

    public abstract List<Address> getPostcodeOptions();

    public abstract String getSelectedPostcodeIndex();

    public abstract void setSelectedPostcode(Address address);

    public boolean isTriedToSave() {
        return triedToSave;
    }

    public void setTriedToSave(boolean triedToSave) {
        this.triedToSave = triedToSave;
    }
}

