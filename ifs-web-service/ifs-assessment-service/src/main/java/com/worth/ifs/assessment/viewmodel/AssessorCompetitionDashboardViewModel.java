package com.worth.ifs.assessment.viewmodel;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Holder of model attributes for the Assessor Competition Dashboard.
 */
public class AssessorCompetitionDashboardViewModel {

    private String competitionTitle;
    private String competition;
    private String leadTechnologist;
    private LocalDateTime acceptDeadline;
    private LocalDateTime submitDeadline;
    private List<AssessorCompetitionDashboardApplicationViewModel> submitted;
    private List<AssessorCompetitionDashboardApplicationViewModel> outstanding;
    private boolean submitVisible;

    public AssessorCompetitionDashboardViewModel(String competitionTitle, String competition, String leadTechnologist, LocalDateTime acceptDeadline, LocalDateTime submitDeadline, List<AssessorCompetitionDashboardApplicationViewModel> submitted, List<AssessorCompetitionDashboardApplicationViewModel> outstanding, boolean submitVisible) {
        this.competitionTitle = competitionTitle;
        this.competition = competition;
        this.leadTechnologist = leadTechnologist;
        this.acceptDeadline = acceptDeadline;
        this.submitDeadline = submitDeadline;
        this.submitted = submitted;
        this.outstanding = outstanding;
        this.submitVisible = submitVisible;
    }

    public String getCompetitionTitle() {
        return competitionTitle;
    }

    public void setCompetitionTitle(String competitionTitle) {
        this.competitionTitle = competitionTitle;
    }

    public String getCompetition() {
        return competition;
    }

    public void setCompetition(String competition) {
        this.competition = competition;
    }

    public String getLeadTechnologist() {
        return leadTechnologist;
    }

    public void setLeadTechnologist(String leadTechnologist) {
        this.leadTechnologist = leadTechnologist;
    }

    public LocalDateTime getAcceptDeadline() {
        return acceptDeadline;
    }

    public void setAcceptDeadline(LocalDateTime acceptDeadline) {
        this.acceptDeadline = acceptDeadline;
    }

    public LocalDateTime getSubmitDeadline() {
        return submitDeadline;
    }

    public void setSubmitDeadline(LocalDateTime submitDeadline) {
        this.submitDeadline = submitDeadline;
    }

    public List<AssessorCompetitionDashboardApplicationViewModel> getSubmitted() {
        return submitted;
    }

    public void setSubmitted(List<AssessorCompetitionDashboardApplicationViewModel> submitted) {
        this.submitted = submitted;
    }

    public List<AssessorCompetitionDashboardApplicationViewModel> getOutstanding() {
        return outstanding;
    }

    public void setOutstanding(List<AssessorCompetitionDashboardApplicationViewModel> outstanding) {
        this.outstanding = outstanding;
    }

    public boolean isSubmitVisible() {
        return submitVisible;
    }

    public void setSubmitVisible(boolean submitVisible) {
        this.submitVisible = submitVisible;
    }
}
