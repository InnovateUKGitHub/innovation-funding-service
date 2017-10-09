package org.innovateuk.ifs.management.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.ZonedDateTime;

/**
 * View model for the Competition Management Application Overview Application Ineligibility details.
 */
public class ApplicationOverviewIneligibilityViewModel {
    private final boolean readOnly;
    private final boolean ineligible;
    private final String removedBy;
    private final ZonedDateTime removedOn;
    private final String reasonForRemoval;

    public ApplicationOverviewIneligibilityViewModel(boolean readOnly) {
        this.readOnly = readOnly;
        this.ineligible = false;
        this.removedBy = null;
        this.removedOn = null;
        this.reasonForRemoval = null;
    }

    public ApplicationOverviewIneligibilityViewModel(boolean readOnly,
                                                     final String removedBy,
                                                     final ZonedDateTime removedOn,
                                                     final String reasonForRemoval) {
        this.readOnly = readOnly;
        this.ineligible = true;
        this.removedBy = removedBy;
        this.removedOn = removedOn;
        this.reasonForRemoval = reasonForRemoval;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isIneligible() {
        return ineligible;
    }

    public String getRemovedBy() {
        return removedBy;
    }

    public ZonedDateTime getRemovedOn() {
        return removedOn;
    }

    public String getReasonForRemoval() {
        return reasonForRemoval;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationOverviewIneligibilityViewModel that = (ApplicationOverviewIneligibilityViewModel) o;

        return new EqualsBuilder()
                .append(readOnly, that.readOnly)
                .append(ineligible, that.ineligible)
                .append(removedBy, that.removedBy)
                .append(removedOn, that.removedOn)
                .append(reasonForRemoval, that.reasonForRemoval)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(readOnly)
                .append(ineligible)
                .append(removedBy)
                .append(removedOn)
                .append(reasonForRemoval)
                .toHashCode();
    }
}
