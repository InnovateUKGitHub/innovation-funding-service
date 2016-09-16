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
    private LocalDate assessmentStartDate;
    private LocalDate assessmentEndDate;

    public AssessorDashboardUpcomingCompetitionViewModel(Long competitionId, String displayLabel,
                                                         LocalDate assessmentStartDate,
                                                         LocalDate assessmentEndDate) {
        this.competitionId = competitionId;
        this.displayLabel = displayLabel;
        this.assessmentStartDate = assessmentStartDate;
        this.assessmentEndDate = assessmentEndDate;
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

    public LocalDate getAssessmentStartDate() {
        return assessmentStartDate;
    }

    public void setAssessmentStartDate(LocalDate assessmentStartDate) {
        this.assessmentStartDate = assessmentStartDate;
    }

    public LocalDate getAssessmentEndDate() {
        return assessmentEndDate;
    }

    public void setAssessmentEndDate(LocalDate assessmentEndDate) {
        this.assessmentEndDate = assessmentEndDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorDashboardUpcomingCompetitionViewModel that = (AssessorDashboardUpcomingCompetitionViewModel) o;

        return new EqualsBuilder()
                .append(competitionId, that.competitionId)
                .append(displayLabel, that.displayLabel)
                .append(assessmentStartDate, that.assessmentStartDate)
                .append(assessmentEndDate, that.assessmentEndDate)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(displayLabel)
                .append(assessmentStartDate)
                .append(assessmentEndDate)
                .toHashCode();
    }
}