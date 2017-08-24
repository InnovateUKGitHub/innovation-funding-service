package org.innovateuk.ifs.project.projectdetails.form;

import org.innovateuk.ifs.address.resource.AddressTypeEnum;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.innovateuk.ifs.form.AddressForm;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Form field model for the project address content
 */
public class ProjectDetailsAddressForm extends BaseBindingResultTarget {
    @NotNull(message = "{validation.bankdetailsresource.organisationaddress.required}")
    private AddressTypeEnum addressType;

    @Valid
    private AddressForm addressForm = new AddressForm();

    // for spring form binding
    public ProjectDetailsAddressForm() {
    }

    public AddressForm getAddressForm() {
        return addressForm;
    }

    public AddressTypeEnum getAddressType() {
        return addressType;
    }

    public void setAddressType(AddressTypeEnum addressType) {
        this.addressType = addressType;
    }
}
