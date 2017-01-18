package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionStatus;

/**
 * Holder of model attributes for the Competition Management Competition 'In Assessment' view.
 */
public class CompetitionInAssessmentViewModel {

    private Long competitionId;
    private String competitionName;
    private CompetitionStatus competitionStatus;

    public CompetitionInAssessmentViewModel(Long competitionId, String competitionName, CompetitionStatus competitionStatus) {
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
