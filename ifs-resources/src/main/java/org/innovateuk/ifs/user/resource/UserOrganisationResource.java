package org.innovateuk.ifs.user.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class UserOrganisationResource {
    private UserResource userResource;
    private Long organisationId;
    private String organisationName;

    public UserOrganisationResource(UserResource userResource, Long organisationId, String organisationName) {
        this.userResource = userResource;
        this.organisationId = organisationId;
        this.organisationName = organisationName;
    }

    public UserOrganisationResource() {
    }

    public UserResource getUserResource() {
        return userResource;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        UserOrganisationResource that = (UserOrganisationResource) o;

        return new EqualsBuilder()
                .append(userResource, that.userResource)
                .append(organisationId, that.organisationId)
                .append(organisationName, that.organisationName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(userResource)
                .append(organisationId)
                .append(organisationName)
                .toHashCode();
    }
}
