package com.worth.ifs.project.viewmodel;

import com.worth.ifs.address.resource.OrganisationAddressType;
import com.worth.ifs.application.form.AddressForm;
import com.worth.ifs.controller.BindingResultTarget;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public class ProjectDetailsAddressViewModelForm implements BindingResultTarget {
    @NotNull(message = "You need to select an address before you can continue")
    private OrganisationAddressType addressType;
    @Valid
    private AddressForm addressForm = new AddressForm();
    private List<ObjectError> objectErrors;
    private BindingResult bindingResult;

    // for spring form binding
    public ProjectDetailsAddressViewModelForm() {
    }

    @Override
    public List<ObjectError> getObjectErrors() {
        return objectErrors;
    }

    @Override
    public void setObjectErrors(List<ObjectError> objectErrors) {
        this.objectErrors = objectErrors;
    }

    @Override
    public BindingResult getBindingResult() {
        return bindingResult;
    }

    @Override
    public void setBindingResult(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
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
