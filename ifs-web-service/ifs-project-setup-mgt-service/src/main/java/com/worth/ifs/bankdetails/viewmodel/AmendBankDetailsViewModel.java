package com.worth.ifs.bankdetails.viewmodel;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.project.resource.ProjectResource;

public class AmendBankDetailsViewModel {
    private Long projectId;
    private String projectNumber;
    private String projectName;
    private String companyNumber;
    private String organisationId;
    private String organisationName;
    private AddressResource registeredAddress;
    private AddressResource operatingAddress;
    private AddressResource bankAddress;

    public AmendBankDetailsViewModel(ProjectResource projectResource) {
        this.projectId = projectResource.getId();
        this.projectNumber = projectResource.getFormattedId();
        this.projectName = projectResource.getName();
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

    public AddressResource getBankAddress() {
        return bankAddress;
    }

    public void setBankAddress(AddressResource bankAddress) {
        this.bankAddress = bankAddress;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(String organisationId) {
        this.organisationId = organisationId;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }
}
