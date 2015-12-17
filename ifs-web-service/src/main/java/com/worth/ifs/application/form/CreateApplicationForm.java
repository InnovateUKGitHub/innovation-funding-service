package com.worth.ifs.application.form;

import com.worth.ifs.organisation.domain.Address;

import java.util.List;

public abstract class CreateApplicationForm  extends Form{

    public abstract String getPostcodeInput();

    public abstract void setPostcodeOptions(List<Address> addresses);

    public abstract List<Address> getPostcodeOptions();

    public abstract String getSelectedPostcodeIndex();

    public abstract void setSelectedPostcode(Address address);

}

