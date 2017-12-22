package org.innovateuk.ifs.management.viewmodel;


import org.innovateuk.ifs.competition.resource.CompetitionStatus;

/**
 * Holder of model attributes for the Competition Assessment Panel dashboard
 */
public class AssessmentPanelViewModel {
    private final long competitionId;
    private final String competitionName;
    private final CompetitionStatus competitionStatus;
    private final int applicationsInPanel;
    private final int assessorsInvited;
    private final int assessorsAccepted;
    private final boolean pendingReviewNotifications;

    public AssessmentPanelViewModel(long competitionId,
                                    String competitionName,
                                    CompetitionStatus competitionStatus,
                                    int applicationsInPanel,
                                    int assessorsInvited,
                                    int assessorsAccepted,
                                    boolean pendingReviewNotifications) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.competitionStatus = competitionStatus;
        this.applicationsInPanel = applicationsInPanel;
        this.assessorsInvited = assessorsInvited;
        this.assessorsAccepted = assessorsAccepted;
        this.pendingReviewNotifications = pendingReviewNotifications;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public int getApplicationsInPanel() { return applicationsInPanel; }

    public int getAssessorsInvited() { return assessorsInvited; }

    public int getAssessorsAccepted() { return assessorsAccepted; }

    public String getCompetitionName() {
        return competitionName;
    }

    public CompetitionStatus getCompetitionStatus() {
        return competitionStatus;
    }

    public boolean isPendingReviewNotifications() {
        return pendingReviewNotifications;
    }
}
