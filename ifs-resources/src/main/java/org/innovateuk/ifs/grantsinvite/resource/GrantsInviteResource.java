package org.innovateuk.ifs.grantsinvite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class GrantsInviteResource {

    public enum GrantsInviteRole {
        GRANTS_PROJECT_MANAGER,
        GRANTS_PROJECT_FINANCE_CONTACT,
        GRANTS_MONITORING_OFFICER
    }

    private String organisationName;
    private String userName;
    private String email;
    private GrantsInviteRole grantsInviteRole;

    public GrantsInviteResource() {
    }

    public GrantsInviteResource(String organisationName, String userName, String email, GrantsInviteRole grantsInviteRole) {
        this.organisationName = organisationName;
        this.userName = userName;
        this.email = email;
        this.grantsInviteRole = grantsInviteRole;
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

    public GrantsInviteRole getGrantsInviteRole() {
        return grantsInviteRole;
    }

    public void setGrantsInviteRole(GrantsInviteRole grantsInviteRole) {
        this.grantsInviteRole = grantsInviteRole;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        GrantsInviteResource that = (GrantsInviteResource) o;

        return new EqualsBuilder()
                .append(organisationName, that.organisationName)
                .append(userName, that.userName)
                .append(email, that.email)
                .append(grantsInviteRole, that.grantsInviteRole)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(organisationName)
                .append(userName)
                .append(email)
                .append(grantsInviteRole)
                .toHashCode();
    }
}
