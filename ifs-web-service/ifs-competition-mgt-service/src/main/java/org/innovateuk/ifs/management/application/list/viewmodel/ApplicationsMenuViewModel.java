package org.innovateuk.ifs.management.application.list.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

/**
 * View model for the Competition Applications menu screen.
 */
public class ApplicationsMenuViewModel {

    private long competitionId;
    private String competitionName;
    private boolean displayAssessorStats;
    private long assessorsInvited;
    private long applicationsInProgress;
    private long applicationsSubmitted;
    private long ineligibleApplications;
    private boolean innovationLeadView;

    public ApplicationsMenuViewModel(CompetitionResource competitionResource,
                                     long assessorsInvited,
                                     long applicationsInProgress,
                                     long applicationsSubmitted,
                                     long ineligibleApplications,
                                     boolean innovationLeadView) {
        this.competitionId = competitionResource.getId();
        this.competitionName = competitionResource.getName();
        this.displayAssessorStats = !CompetitionCompletionStage.COMPETITION_CLOSE.equals(competitionResource.getCompletionStage());
        this.assessorsInvited = assessorsInvited;
        this.applicationsInProgress = applicationsInProgress;
        this.applicationsSubmitted = applicationsSubmitted;
        this.ineligibleApplications = ineligibleApplications;
        this.innovationLeadView = innovationLeadView;
    }

    public boolean isDisplayAssessorStats() {
        return displayAssessorStats;
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
