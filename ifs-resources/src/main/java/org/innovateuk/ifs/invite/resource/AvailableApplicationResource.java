package org.innovateuk.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.user.resource.BusinessType;

import java.util.List;

/**
 * DTO for an assessor that is available to be invited.
 */
public class AvailableApplicationResource {

    private final long id;
    private final String name;
    private final String leadOrganisation;

    public AvailableApplicationResource() {
        this.id = -1;
        this.name = null;
        this.leadOrganisation = null;
    }

    public AvailableApplicationResource(long id, String name, String leadOrganisation) {
        this.id = id;
        this.name = name;
        this.leadOrganisation = leadOrganisation;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLeadOrganisation() {
        return leadOrganisation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AvailableApplicationResource that = (AvailableApplicationResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(name, that.name)
                .append(leadOrganisation, that.leadOrganisation)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(leadOrganisation)
                .toHashCode();
    }
}
