package com.worth.ifs.assessment.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDate;

/**
 * Holder of attributes for the pending invitations shown on the Assessor Dashboard.
 */
public class AssessorDashboardPendingInviteViewModel {

    private long competitionId;
    private final String displayLabel;
    private final LocalDate assessmentPeriodDateFrom;
    private final LocalDate assessmentPeriodDateTo;

    public AssessorDashboardPendingInviteViewModel(
            long competitionId,
            String displayLabel,
            LocalDate assessmentPeriodDateFrom,
            LocalDate assessmentPeriodDateTo
    ) {
        this.competitionId = competitionId;
        this.displayLabel = displayLabel;
        this.assessmentPeriodDateFrom = assessmentPeriodDateFrom;
        this.assessmentPeriodDateTo = assessmentPeriodDateTo;
    }

    public long getCompetitionId() {
        return competitionId;
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
                .append(competitionId, that.competitionId)
                .append(displayLabel, that.displayLabel)
                .append(assessmentPeriodDateFrom, that.assessmentPeriodDateFrom)
                .append(assessmentPeriodDateTo, that.assessmentPeriodDateTo)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(displayLabel)
                .append(assessmentPeriodDateFrom)
                .append(assessmentPeriodDateTo)
                .toHashCode();
    }
}
