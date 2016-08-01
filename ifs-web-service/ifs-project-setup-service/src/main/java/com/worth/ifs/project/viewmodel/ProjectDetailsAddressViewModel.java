package com.worth.ifs.project.viewmodel;

import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.project.resource.ProjectResource;

public class ProjectDetailsAddressViewModel implements BasicProjectDetailsViewModel {
    private Long projectId;
    private String projectNumber;
    private String projectName;
    private AddressResource registeredAddress;
    private AddressResource operatingAddress;
    private AddressResource projectAddress;

    public ProjectDetailsAddressViewModel(ProjectResource projectResource) {
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

    public AddressResource getProjectAddress() {
        return projectAddress;
    }

    public void setProjectAddress(AddressResource projectAddress) {
        this.projectAddress = projectAddress;
    }

    public Long getProjectId() {
        return projectId;
    }
}
