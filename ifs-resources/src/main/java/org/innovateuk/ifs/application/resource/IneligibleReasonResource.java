package org.innovateuk.ifs.application.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Resource class for the ineligible reason
 */
public class IneligibleReasonResource {

    private String reason;

    public IneligibleReasonResource() {

    }

    public IneligibleReasonResource(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        IneligibleReasonResource that = (IneligibleReasonResource) o;

        return new EqualsBuilder()
                .append(reason, that.reason)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(reason)
                .toHashCode();
    }
}
