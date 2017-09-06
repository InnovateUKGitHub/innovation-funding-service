package org.innovateuk.ifs.management.viewmodel;


import org.innovateuk.ifs.competition.resource.CompetitionStatus;

/**
 * Holder of model attributes for the Competition Assessment Panel dashboard
 */
public class AssessmentPanelViewModel {
    private Long competitionId;
    private String competitionName;
    private CompetitionStatus competitionStatus;
    private int applicationsInPanel;
    private int assessorsInvited;
    private int assessorsAccepted;

    public AssessmentPanelViewModel(Long competitionId,
                                    String competitionName,
                                    CompetitionStatus competitionStatus,
                                    int applicationsInPanel,
                                    int assessorsInvited,
                                    int assessorsAccepted) {
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

    public int getApplicationsInPanel() { return applicationsInPanel; }

    public int getAssessorsInvited() { return assessorsInvited; }

    public int getAssessorsAccepted() { return assessorsAccepted; }

    public String getCompetitionName() {
        return competitionName;
    }

    public CompetitionStatus getCompetitionStatus() {
        return competitionStatus;
    }
}
