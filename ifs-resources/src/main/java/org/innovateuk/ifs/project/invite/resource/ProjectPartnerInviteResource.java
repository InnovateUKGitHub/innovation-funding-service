package org.innovateuk.ifs.project.invite.resource;

public class ProjectPartnerInviteResource {

    private String organisationName;
    private String userName;
    private String email;

    private ProjectPartnerInviteResource() {}

    public ProjectPartnerInviteResource(String organisationName, String userName, String email) {
        this.organisationName = organisationName;
        this.userName = userName;
        this.email = email;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
