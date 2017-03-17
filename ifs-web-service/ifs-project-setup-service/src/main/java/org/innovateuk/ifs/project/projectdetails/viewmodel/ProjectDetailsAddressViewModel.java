package org.innovateuk.ifs.project.projectdetails.viewmodel;

import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.project.resource.ProjectResource;

/**
 * The view model that backs the project address
 */
public class ProjectDetailsAddressViewModel implements BasicProjectDetailsViewModel {
    private Long applicationId;
    private Long projectId;
    private String projectName;
    private AddressResource registeredAddress;
    private AddressResource operatingAddress;
    private AddressResource projectAddress;

    public ProjectDetailsAddressViewModel(ProjectResource projectResource) {
        this.projectId = projectResource.getId();
        this.projectName = projectResource.getName();
        this.applicationId = projectResource.getApplication();
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

    public Long getApplicationId() {
        return applicationId;
    }
}
