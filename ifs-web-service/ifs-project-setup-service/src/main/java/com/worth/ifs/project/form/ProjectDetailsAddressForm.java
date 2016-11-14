package com.worth.ifs.project.form;

import com.worth.ifs.address.resource.OrganisationAddressType;
import com.worth.ifs.controller.BaseBindingResultTarget;
import com.worth.ifs.form.AddressForm;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class ProjectDetailsAddressForm extends BaseBindingResultTarget {
    @NotNull(message = "{validation.bankdetailsresource.organisation.required}")
    private OrganisationAddressType addressType;

    @Valid
    private AddressForm addressForm = new AddressForm();

    // for spring form binding
    public ProjectDetailsAddressForm() {
    }

    public AddressForm getAddressForm() {
        return addressForm;
    }

    public OrganisationAddressType getAddressType() {
        return addressType;
    }

    public void setAddressType(OrganisationAddressType addressType) {
        this.addressType = addressType;
    }
}
