package org.innovateuk.ifs.project.correspondenceaddress.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.innovateuk.ifs.address.form.AddressForm;

import javax.validation.Valid;

/**
 * Form field model for the project address content
 */
public class ProjectDetailsAddressForm extends BaseBindingResultTarget {

    @Valid
    private AddressForm addressForm = new AddressForm();

    public AddressForm getAddressForm() {
        return addressForm;
    }

}
