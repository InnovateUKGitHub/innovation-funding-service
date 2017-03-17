package org.innovateuk.ifs.project.projectdetails.form;

import org.innovateuk.ifs.address.resource.OrganisationAddressType;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.innovateuk.ifs.form.AddressForm;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Form field model for the project address content
 */
public class ProjectDetailsAddressForm extends BaseBindingResultTarget {
    @NotNull(message = "{validation.bankdetailsresource.organisationaddress.required}")
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
