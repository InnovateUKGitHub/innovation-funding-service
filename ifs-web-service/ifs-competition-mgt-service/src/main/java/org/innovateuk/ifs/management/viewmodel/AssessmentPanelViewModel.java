package org.innovateuk.ifs.management.viewmodel;


import org.innovateuk.ifs.competition.resource.CompetitionStatus;

/**
 * Holder of model attributes for the Competition Assessment Panel dashboard
 */
public class AssessmentPanelViewModel {
    private Long competitionId;
    private String competitionName;
    private CompetitionStatus competitionStatus;
    private Long applicationsInPanel;
    private Long assessorsInvited;
    private Long assessorsAccepted;

    public AssessmentPanelViewModel(Long competitionId,
                                    String competitionName,
                                    CompetitionStatus competitionStatus,
                                    Long applicationsInPanel,
                                    Long assessorsInvited,
                                    Long assessorsAccepted) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.competitionStatus = competitionStatus;
        this.applicationsInPanel = applicationsInPanel;
        this.assessorsInvited = assessorsInvited;
        this.assessorsAccepted = assessorsAccepted;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public Long getApplicationsInPanel() { return applicationsInPanel; }

    public Long getAssessorsInvited() { return assessorsInvited; }

    public Long getAssessorsAccepted() { return assessorsAccepted; }

    public String getCompetitionName() {
        return competitionName;
    }

    public CompetitionStatus getCompetitionStatus() {
        return competitionStatus;
    }
}
