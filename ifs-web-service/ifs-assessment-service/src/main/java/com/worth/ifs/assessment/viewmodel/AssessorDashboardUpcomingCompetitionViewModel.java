package com.worth.ifs.assessment.viewmodel;

import java.time.LocalDate;

/**
 * Holder of model attributes for the upcoming competitions shown on the Assessment Dashboard.
 */
public class AssessorDashboardUpcomingCompetitionViewModel {

    private Long competitionId;
    private String displayLabel;
    private LocalDate assessmentStartDate;
    private LocalDate assessmentEndDate;

    public AssessorDashboardUpcomingCompetitionViewModel(Long competitionId, String displayLabel, String progressAssessed, String progressTotal, String deadlineDayOfMonth, String deadlineMonth, long daysLeft, long daysLeftPercentage) {
        this.competitionId = competitionId;
        this.displayLabel = displayLabel;
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
}