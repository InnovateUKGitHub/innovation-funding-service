package org.innovateuk.ifs.assessment.viewmodel;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Holder of model attributes for the Assessor Competition Dashboard.
 */
public class AssessorCompetitionDashboardViewModel {

    private long competitionId;
    private String competitionTitle;
    private String competition;
    private String leadTechnologist;
    private ZonedDateTime acceptDeadline;
    private ZonedDateTime submitDeadline;
    private List<AssessorCompetitionDashboardApplicationViewModel> submitted;
    private List<AssessorCompetitionDashboardApplicationViewModel> outstanding;
    private boolean submitVisible;

    public AssessorCompetitionDashboardViewModel(long competitionId, String competitionTitle, String competition, String leadTechnologist, ZonedDateTime acceptDeadline, ZonedDateTime submitDeadline, List<AssessorCompetitionDashboardApplicationViewModel> submitted, List<AssessorCompetitionDashboardApplicationViewModel> outstanding, boolean submitVisible) {
        this.competitionId = competitionId;
        this.competitionTitle = competitionTitle;
        this.competition = competition;
        this.leadTechnologist = leadTechnologist;
        this.acceptDeadline = acceptDeadline;
        this.submitDeadline = submitDeadline;
        this.submitted = submitted;
        this.outstanding = outstanding;
        this.submitVisible = submitVisible;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionTitle() {
        return competitionTitle;
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

    public ZonedDateTime getAcceptDeadline() {
        return acceptDeadline;
    }

    public ZonedDateTime getSubmitDeadline() {
        return submitDeadline;
    }

    public List<AssessorCompetitionDashboardApplicationViewModel> getSubmitted() {
        return submitted;
    }

    public List<AssessorCompetitionDashboardApplicationViewModel> getOutstanding() {
        return outstanding;
    }

    public boolean isSubmitVisible() {
        return submitVisible;
    }
}
