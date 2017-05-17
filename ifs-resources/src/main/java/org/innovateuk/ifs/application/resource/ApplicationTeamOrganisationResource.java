package org.innovateuk.ifs.application.resource;

import org.innovateuk.ifs.organisation.resource.OrganisationAddressResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;

/**
 * Application Team Organisation data transfer object
 */
public class ApplicationTeamOrganisationResource {
    OrganisationAddressResource operatingAddress;
    OrganisationAddressResource registeredAddress;
    String organisationName;
    List<UserResource> users;

    public OrganisationAddressResource getRegisteredAddress() {
        return registeredAddress;
    }

    public void setRegisteredAddress(OrganisationAddressResource registeredAddress) {
        this.registeredAddress = registeredAddress;
    }

    public OrganisationAddressResource getOperatingAddress() {
        return operatingAddress;
    }

    public void setOperatingAddress(OrganisationAddressResource operatingAddress) {
        this.operatingAddress = operatingAddress;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public List<UserResource> getUsers() {
        return users;
    }

    public void setUsers(List<UserResource> users) {
        this.users = users;
    }
}
