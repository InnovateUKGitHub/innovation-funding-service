package org.innovateuk.ifs.assessment.dashboard.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDate;

/**
 * Holder of model attributes for the active competitions shown on the Assessment Dashboard.
 */
public class AssessorDashboardActiveCompetitionViewModel {

    private long competitionId;
    private String displayLabel;
    private long progressAssessed;
    private long progressTotal;
    private long pendingAssessments;
    private LocalDate submitDeadline;
    private long daysLeft;
    private long daysLeftPercentage;
    private boolean competitionAlwaysOpen;
    private long batchIndex;

    public AssessorDashboardActiveCompetitionViewModel(long competitionId, String displayLabel, long progressAssessed,
                                                       long progressTotal, long pendingAssessments, LocalDate submitDeadline,
                                                       long daysLeft, long daysLeftPercentage, boolean competitionAlwaysOpen,
                                                       long batchIndex) {
        this.competitionId = competitionId;
        this.displayLabel = displayLabel;
        this.progressAssessed = progressAssessed;
        this.progressTotal = progressTotal;
        this.pendingAssessments = pendingAssessments;
        this.submitDeadline = submitDeadline;
        this.daysLeft = daysLeft;
        this.daysLeftPercentage = daysLeftPercentage;
        this.competitionAlwaysOpen = competitionAlwaysOpen;
        this.batchIndex = batchIndex;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(long competitionId) {
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

    public long getPendingAssessments() {
        return pendingAssessments;
    }

    public void setPendingAssessments(long pendingAssessments) {
        this.pendingAssessments = pendingAssessments;
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

    public boolean hasPendingAssessments(){
        return pendingAssessments != 0;
    }

    public boolean hasApplicationsToAssess(){
        return progressTotal - progressAssessed != 0;
    }

    public boolean isCompetitionAlwaysOpen() {
        return competitionAlwaysOpen;
    }

    public long getBatchIndex() {
        return batchIndex;
    }

    public void setBatchIndex(long batchIndex) {
        this.batchIndex = batchIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorDashboardActiveCompetitionViewModel that = (AssessorDashboardActiveCompetitionViewModel) o;

        return new EqualsBuilder()
                .append(progressAssessed, that.progressAssessed)
                .append(progressTotal, that.progressTotal)
                .append(pendingAssessments, that.pendingAssessments)
                .append(daysLeft, that.daysLeft)
                .append(daysLeftPercentage, that.daysLeftPercentage)
                .append(competitionId, that.competitionId)
                .append(displayLabel, that.displayLabel)
                .append(submitDeadline, that.submitDeadline)
                .append(competitionAlwaysOpen, that.competitionAlwaysOpen)
                .append(batchIndex, that.batchIndex)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionId)
                .append(displayLabel)
                .append(progressAssessed)
                .append(progressTotal)
                .append(pendingAssessments)
                .append(submitDeadline)
                .append(daysLeft)
                .append(daysLeftPercentage)
                .append(competitionAlwaysOpen)
                .append(batchIndex)
                .toHashCode();
    }
}
