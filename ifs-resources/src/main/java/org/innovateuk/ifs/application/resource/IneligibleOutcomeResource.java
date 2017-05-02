package org.innovateuk.ifs.application.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.ZonedDateTime;

/**
 * DTO for marking applications as ineligible.
 */
public class IneligibleOutcomeResource {

    private String reason;
    private String removedBy;
    private ZonedDateTime removedOn;

    public String getReason() {
        return reason;
    }

    public void setReason(final String reason) {
        this.reason = reason;
    }

    public String getRemovedBy() {
        return removedBy;
    }

    public void setRemovedBy(final String removedBy) {
        this.removedBy = removedBy;
    }

    public ZonedDateTime getRemovedOn() {
        return removedOn;
    }

    public void setRemovedOn(final ZonedDateTime removedOn) {
        this.removedOn = removedOn;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final IneligibleOutcomeResource that = (IneligibleOutcomeResource) o;

        return new EqualsBuilder()
                .append(reason, that.reason)
                .append(removedBy, that.removedBy)
                .append(removedOn, that.removedOn)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(reason)
                .append(removedBy)
                .append(removedOn)
                .toHashCode();
    }
}