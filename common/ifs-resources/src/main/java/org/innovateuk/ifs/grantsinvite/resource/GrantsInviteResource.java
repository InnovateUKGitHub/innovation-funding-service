package org.innovateuk.ifs.grantsinvite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class GrantsInviteResource {

    public enum GrantsInviteRole {
        GRANTS_PROJECT_MANAGER("Project manager"),
        GRANTS_PROJECT_FINANCE_CONTACT("Finance contact"),
        GRANTS_MONITORING_OFFICER("Monitor officer");

        GrantsInviteRole(String displayName) {
            this.displayName = displayName;
        }

        private String displayName;

        public String getDisplayName() {
            return displayName;
        }
    }

    private String userName;
    private String email;
    private GrantsInviteRole grantsInviteRole;

    //Optionaly link invite to an existing project organisation.
    private Long organisationId;

    public GrantsInviteResource() {
    }

    public GrantsInviteResource(Long organisationId, String userName, String email, GrantsInviteRole grantsInviteRole) {
        this.userName = userName;
        this.email = email;
        this.grantsInviteRole = grantsInviteRole;
        this.organisationId = organisationId;
    }
    public GrantsInviteResource(String userName, String email, GrantsInviteRole grantsInviteRole) {
        this(null, userName, email, grantsInviteRole);
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
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
                .append(organisationId, that.organisationId)
                .append(userName, that.userName)
                .append(email, that.email)
                .append(grantsInviteRole, that.grantsInviteRole)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(organisationId)
                .append(userName)
                .append(email)
                .append(grantsInviteRole)
                .toHashCode();
    }
}
