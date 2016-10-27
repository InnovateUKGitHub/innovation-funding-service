package com.worth.ifs.assessment.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDate;

/**
 * Holder of model attributes for the upcoming competitions shown on the Assessment Dashboard.
 */
public class AssessorDashboardUpcomingCompetitionViewModel {

    private Long competitionId;
    private String displayLabel;
    private LocalDate assessmentPeriodDateFrom;
    private LocalDate assessmentPeriodDateTo;

    public AssessorDashboardUpcomingCompetitionViewModel(Long competitionId, String displayLabel, LocalDate assessmentPeriodDateFrom, LocalDate assessmentPeriodDateTo) {
        this.competitionId = competitionId;
        this.displayLabel = displayLabel;
        this.assessmentPeriodDateFrom = assessmentPeriodDateFrom;
        this.assessmentPeriodDateTo = assessmentPeriodDateTo;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public void setDisplayLabel(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public LocalDate getAssessmentPeriodDateFrom() {
        return assessmentPeriodDateFrom;
    }

    public void setAssessmentPeriodDateFrom(LocalDate assessmentPeriodDateFrom) {
        this.assessmentPeriodDateFrom = assessmentPeriodDateFrom;
    }

    public LocalDate getAssessmentPeriodDateTo() {
        return assessmentPeriodDateTo;
    }

    public void setAssessmentPeriodDateTo(LocalDate assessmentPeriodDateTo) {
        this.assessmentPeriodDateTo = assessmentPeriodDateTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessorDashboardUpcomingCompetitionViewModel that = (AssessorDashboardUpcomingCompetitionViewModel) o;

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