package com.worth.ifs.assessment.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDateTime;

/**
 * ViewModel of an UpcomingCompetition.
 */
public class UpcomingCompetitionViewModel {

    private String competitionName;
    private String competitionDescription;
    private LocalDateTime assessmentPeriodDateFrom;
    private LocalDateTime assessmentPeriodDateTo;

    public UpcomingCompetitionViewModel(String competitionName, String competitionDescription, LocalDateTime assessmentPeriodDateFrom, LocalDateTime assessmentPeriodDateTo) {
        this.competitionName = competitionName;
        this.competitionDescription = competitionDescription;
        this.assessmentPeriodDateFrom = assessmentPeriodDateFrom;
        this.assessmentPeriodDateTo = assessmentPeriodDateTo;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public String getCompetitionDescription() {
        return competitionDescription;
    }

    public void setCompetitionDescription(String competitionDescription) {
        this.competitionDescription = competitionDescription;
    }

    public LocalDateTime getAssessmentPeriodDateFrom() {
        return assessmentPeriodDateFrom;
    }

    public void setAssessmentPeriodDateFrom(LocalDateTime assessmentPeriodDateFrom) {
        this.assessmentPeriodDateFrom = assessmentPeriodDateFrom;
    }

    public LocalDateTime getAssessmentPeriodDateTo() {
        return assessmentPeriodDateTo;
    }

    public void setAssessmentPeriodDateTo(LocalDateTime assessmentPeriodDateTo) {
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

        UpcomingCompetitionViewModel that = (UpcomingCompetitionViewModel) o;

        return new EqualsBuilder()
                .append(competitionName, that.competitionName)
                .append(competitionDescription, that.competitionDescription)
                .append(assessmentPeriodDateFrom, assessmentPeriodDateFrom)
                .append(assessmentPeriodDateTo, assessmentPeriodDateTo)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionName)
                .append(competitionDescription)
                .append(assessmentPeriodDateFrom)
                .append(assessmentPeriodDateTo)
                .toHashCode();
    }
}
