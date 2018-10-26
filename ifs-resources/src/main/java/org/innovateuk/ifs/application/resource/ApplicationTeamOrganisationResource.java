package org.innovateuk.ifs.application.resource;

import java.util.List;

/**
 * Application Team Organisation data transfer object
 */
public class ApplicationTeamOrganisationResource {
    private String organisationName;
    private String organisationTypeName;
    private List<ApplicationTeamUserResource> users;

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public String getOrganisationTypeName() {
        return organisationTypeName;
    }

    public void setOrganisationTypeName(String organisationTypeName) {
        this.organisationTypeName = organisationTypeName;
    }

    public List<ApplicationTeamUserResource>getUsers() {
        return users;
    }

    public void setUsers(List<ApplicationTeamUserResource> users) {
        this.users = users;
    }
}
