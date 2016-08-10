package com.worth.ifs.assessment.viewmodel;

import java.time.LocalDate;

/**
 * Holder of model attributes for the active competitions shown on the Assessment Dashboard.
 */
public class AssessorDashboardActiveCompetitionViewModel {

    private Long competitionId;
    private String displayLabel;
    private String progressAssessed;
    private String progressTotal;
    private LocalDate deadline;
    private long daysLeft;
    private long daysLeftPercentage;

    public AssessorDashboardActiveCompetitionViewModel(Long competitionId, String displayLabel, String progressAssessed, String progressTotal, LocalDate deadline, long daysLeft, long daysLeftPercentage) {
        this.competitionId = competitionId;
        this.displayLabel = displayLabel;
        this.progressAssessed = progressAssessed;
        this.progressTotal = progressTotal;
        this.deadline = deadline;
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

    public String getProgressAssessed() {
        return progressAssessed;
    }

    public void setProgressAssessed(String progressAssessed) {
        this.progressAssessed = progressAssessed;
    }

    public String getProgressTotal() {
        return progressTotal;
    }

    public void setProgressTotal(String progressTotal) {
        this.progressTotal = progressTotal;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
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
}
