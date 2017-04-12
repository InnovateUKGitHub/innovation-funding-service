package org.innovateuk.ifs.assessment.dashboard.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDate;

/**
 * Holder of attributes for the pending invitations shown on the Assessor Dashboard.
 */
public class AssessorDashboardPendingInviteViewModel {

    private final String hash;
    private final String displayLabel;
    private final LocalDate assessmentPeriodDateFrom;
    private final LocalDate assessmentPeriodDateTo;

    public AssessorDashboardPendingInviteViewModel(
            String hash,
            String displayLabel,
            LocalDate assessmentPeriodDateFrom,
            LocalDate assessmentPeriodDateTo
    ) {
        this.hash = hash;
        this.displayLabel = displayLabel;
        this.assessmentPeriodDateFrom = assessmentPeriodDateFrom;
        this.assessmentPeriodDateTo = assessmentPeriodDateTo;
    }

    public String getHash() {
        return hash;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public LocalDate getAssessmentPeriodDateFrom() {
        return assessmentPeriodDateFrom;
    }

    public LocalDate getAssessmentPeriodDateTo() {
        return assessmentPeriodDateTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorDashboardPendingInviteViewModel that = (AssessorDashboardPendingInviteViewModel) o;

        return new EqualsBuilder()
                .append(hash, that.hash)
                .append(displayLabel, that.displayLabel)
                .append(assessmentPeriodDateFrom, that.assessmentPeriodDateFrom)
                .append(assessmentPeriodDateTo, that.assessmentPeriodDateTo)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(hash)
                .append(displayLabel)
                .append(assessmentPeriodDateFrom)
                .append(assessmentPeriodDateTo)
                .toHashCode();
    }
}
