package com.worth.ifs.finance.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;


/**
 * Compound class for holding the application finance resource keys
 */
public class ApplicationFinanceResourceId implements Serializable {
    private Long applicationId;
    private Long organisationId;

    public ApplicationFinanceResourceId(Long applicationId, Long organisationId) {
        this.applicationId = applicationId;
        this.organisationId = organisationId;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationFinanceResourceId that = (ApplicationFinanceResourceId) o;

        return new EqualsBuilder()
                .append(applicationId, that.applicationId)
                .append(organisationId, that.organisationId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applicationId)
                .append(organisationId)
                .toHashCode();
    }
}
