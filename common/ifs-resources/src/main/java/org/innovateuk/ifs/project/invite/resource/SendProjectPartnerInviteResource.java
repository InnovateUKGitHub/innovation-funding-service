package org.innovateuk.ifs.project.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class SendProjectPartnerInviteResource {

    private String organisationName;
    private String userName;
    private String email;

    protected SendProjectPartnerInviteResource() {}

    public SendProjectPartnerInviteResource(String organisationName, String userName, String email) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SendProjectPartnerInviteResource that = (SendProjectPartnerInviteResource) o;

        return new EqualsBuilder()
                .append(organisationName, that.organisationName)
                .append(userName, that.userName)
                .append(email, that.email)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(organisationName)
                .append(userName)
                .append(email)
                .toHashCode();
    }
}
