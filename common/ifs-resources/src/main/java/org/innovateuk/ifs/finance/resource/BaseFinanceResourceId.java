package org.innovateuk.ifs.finance.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;


/**
 * Compound class for holding the application finance resource keys
 */
public abstract class BaseFinanceResourceId implements Serializable {
    private long targetId;
    private long organisationId;

    public BaseFinanceResourceId(long targetId, long organisationId) {
        this.targetId = targetId;
        this.organisationId = organisationId;
    }

    public long getTargetId() {
        return targetId;
    }

    public long getOrganisationId() {
        return organisationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        BaseFinanceResourceId that = (BaseFinanceResourceId) o;

        return new EqualsBuilder()
                .append(targetId, that.targetId)
                .append(organisationId, that.organisationId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(targetId)
                .append(organisationId)
                .toHashCode();
    }
}
