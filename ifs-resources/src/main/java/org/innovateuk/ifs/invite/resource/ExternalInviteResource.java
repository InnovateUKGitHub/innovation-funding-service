package org.innovateuk.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.invite.constant.InviteStatus;

public class ExternalInviteResource {
    private String name;
    private String organisationName;
    private String organisationId;
    private String email;
    private Long applicationId;
    private InviteStatus status;

    public ExternalInviteResource(String name, String organisationName, String organisationId, String email, Long applicationId, InviteStatus status) {
        this.name = name;
        this.organisationName = organisationName;
        this.organisationId = organisationId;
        this.email = email;
        this.applicationId = applicationId;
        this.status = status;
    }

    public ExternalInviteResource() {
    }

    public String getName() {
        return name;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public String getOrganisationId() {
        return organisationId;
    }

    public String getEmail() {
        return email;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public InviteStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ExternalInviteResource that = (ExternalInviteResource) o;

        return new EqualsBuilder()
                .append(name, that.name)
                .append(organisationName, that.organisationName)
                .append(organisationId, that.organisationId)
                .append(email, that.email)
                .append(applicationId, that.applicationId)
                .append(status, that.status)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(organisationName)
                .append(organisationId)
                .append(email)
                .append(applicationId)
                .append(status)
                .toHashCode();
    }
}
