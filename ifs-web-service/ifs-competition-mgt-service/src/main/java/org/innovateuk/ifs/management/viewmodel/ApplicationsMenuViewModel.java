package org.innovateuk.ifs.management.viewmodel;

/**
 * View model for the Competition Applications menu screen.
 */
public class ApplicationsMenuViewModel {

    private long competitionId;
    private String competitionName;
    private long assessorsInvited;
    private long applicationsInProgress;
    private long applicationsSubmitted;
    private long ineligibleApplications;
    private boolean innovationLeadView;

    public ApplicationsMenuViewModel(long competitionId,
                                     String competitionName,
                                     long assessorsInvited,
                                     long applicationsInProgress,
                                     long applicationsSubmitted,
                                     long ineligibleApplications,
                                     boolean innovationLeadView) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.assessorsInvited = assessorsInvited;
        this.applicationsInProgress = applicationsInProgress;
        this.applicationsSubmitted = applicationsSubmitted;
        this.ineligibleApplications = ineligibleApplications;
        this.innovationLeadView = innovationLeadView;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public long getAssessorsInvited() {
        return assessorsInvited;
    }

    public long getApplicationsInProgress() {
        return applicationsInProgress;
    }

    public long getApplicationsSubmitted() {
        return applicationsSubmitted;
    }

    public long getIneligibleApplications() {
        return ineligibleApplications;
    }

    public boolean isInnovationLeadView() {
        return innovationLeadView;
    }
}
