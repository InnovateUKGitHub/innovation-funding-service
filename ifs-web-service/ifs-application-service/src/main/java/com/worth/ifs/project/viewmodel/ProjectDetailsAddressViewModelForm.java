package com.worth.ifs.project.viewmodel;

import com.worth.ifs.address.resource.AddressType;
import com.worth.ifs.application.form.AddressForm;
import com.worth.ifs.controller.BindingResultTarget;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public class ProjectDetailsAddressViewModelForm implements BindingResultTarget {
    @NotNull(message = "You need to select a project address before you can continue.")
    private AddressType addressType;
    private boolean useSearchResultAddress;
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

    public void setAddressForm(AddressForm addressForm) {
        this.addressForm = addressForm;
    }

    public AddressType getAddressType() {
        return addressType;
    }

    public void setAddressType(AddressType addressType) {
        this.addressType = addressType;
    }

    public boolean isUseSearchResultAddress() {
        return useSearchResultAddress;
    }

    public void setUseSearchResultAddress(boolean useSearchResultAddress) {
        this.useSearchResultAddress = useSearchResultAddress;
    }
}
