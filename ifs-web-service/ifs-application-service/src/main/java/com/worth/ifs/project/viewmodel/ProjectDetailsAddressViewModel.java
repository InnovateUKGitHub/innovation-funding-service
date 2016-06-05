package com.worth.ifs.project.viewmodel;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.application.form.AddressForm;
import com.worth.ifs.controller.BindingResultTarget;
import com.worth.ifs.project.resource.ProjectResource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.validation.Valid;
import java.util.List;

public class ProjectDetailsAddressViewModel {
    private String projectNumber;
    private String projectName;
    private Long projectAddressId;
    private AddressResource registeredAddress;
    private AddressResource operatingAddress;
    private AddressResource projectAddress;

    public ProjectDetailsAddressViewModel(ProjectResource projectResource) {
        this.projectNumber = projectResource.getFormattedId();
        this.projectName = projectResource.getName();
        this.projectAddressId = projectResource.getAddress();
    }

    public String getProjectNumber() {
        return projectNumber;
    }

    public void setProjectNumber(String projectNumber) {
        this.projectNumber = projectNumber;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public AddressResource getRegisteredAddress() {
        return registeredAddress;
    }

    public void setRegisteredAddress(AddressResource registeredAddress) {
        this.registeredAddress = registeredAddress;
    }

    public AddressResource getOperatingAddress() {
        return operatingAddress;
    }

    public void setOperatingAddress(AddressResource operatingAddress) {
        this.operatingAddress = operatingAddress;
    }

    public Long getProjectAddressId() {
        return projectAddressId;
    }

    public void setProjectAddressId(Long projectAddressId) {
        this.projectAddressId = projectAddressId;
    }

    public AddressResource getProjectAddress() {
        return projectAddress;
    }

    public void setProjectAddress(AddressResource projectAddress) {
        this.projectAddress = projectAddress;
    }

    public static class ProjectDetailsAddressViewModelForm implements BindingResultTarget {
        private String projectAddressGroup;
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

        public String getProjectAddressGroup() {
            return projectAddressGroup;
        }

        public void setProjectAddressGroup(String projectAddressGroup) {
            this.projectAddressGroup = projectAddressGroup;
        }
    }
}
