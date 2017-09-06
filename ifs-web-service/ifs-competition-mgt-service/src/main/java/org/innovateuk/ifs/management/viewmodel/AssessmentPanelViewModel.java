package org.innovateuk.ifs.management.viewmodel;


import org.innovateuk.ifs.competition.resource.CompetitionStatus;

/**
 * Holder of model attributes for the Competition Assessment Panel dashboard
 */
public class AssessmentPanelViewModel {
    private Long competitionId;
    private String competitionName;
    private CompetitionStatus competitionStatus;

    public AssessmentPanelViewModel(Long competitionId, String competitionName, CompetitionStatus competitionStatus) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.competitionStatus = competitionStatus;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public CompetitionStatus getCompetitionStatus() {
        return competitionStatus;
    }
}
