package com.worth.ifs.assessment.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDate;

/**
 * Holder of model attributes for the active competitions shown on the Assessment Dashboard.
 */
public class AssessorDashboardActiveCompetitionViewModel {

    private Long competitionId;
    private String displayLabel;
    private long progressAssessed;
    private long progressTotal;
    private LocalDate submitDeadline;
    private long daysLeft;
    private long daysLeftPercentage;

    public AssessorDashboardActiveCompetitionViewModel(Long competitionId, String displayLabel, long progressAssessed, long progressTotal, LocalDate submitDeadline, long daysLeft, long daysLeftPercentage) {
        this.competitionId = competitionId;
        this.displayLabel = displayLabel;
        this.progressAssessed = progressAssessed;
        this.progressTotal = progressTotal;
        this.submitDeadline = submitDeadline;
        this.daysLeft = daysLeft;
        this.daysLeftPercentage = daysLeftPercentage;
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

    public long getProgressAssessed() {
        return progressAssessed;
    }

    public void setProgressAssessed(long progressAssessed) {
        this.progressAssessed = progressAssessed;
    }

    public long getProgressTotal() {
        return progressTotal;
    }

    public void setProgressTotal(long progressTotal) {
        this.progressTotal = progressTotal;
    }

    public LocalDate getSubmitDeadline() {
        return submitDeadline;
    }

    public void setSubmitDeadline(LocalDate submitDeadline) {
        this.submitDeadline = submitDeadline;
    }

    public long getDaysLeft() {
        return daysLeft;
    }

    public void setDaysLeft(long daysLeft) {
        this.daysLeft = daysLeft;
    }

    public long getDaysLeftPercentage() {
        return daysLeftPercentage;
    }

    public void setDaysLeftPercentage(long daysLeftPercentage) {
        this.daysLeftPercentage = daysLeftPercentage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessorDashboardActiveCompetitionViewModel that = (AssessorDashboardActiveCompetitionViewModel) o;

        return new EqualsBuilder()
                .append(daysLeft, that.daysLeft)
                .append(daysLeftPercentage, that.daysLeftPercentage)
                .append(competitionId, that.competitionId)
                .append(displayLabel, that.displayLabel)
                .append(progressAssessed, that.progressAssessed)
                .append(progressTotal, that.progressTotal)
                .append(submitDeadline, that.submitDeadline)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(displayLabel)
                .append(progressAssessed)
                .append(progressTotal)
                .append(submitDeadline)
                .append(daysLeft)
                .append(daysLeftPercentage)
                .toHashCode();
    }
}
