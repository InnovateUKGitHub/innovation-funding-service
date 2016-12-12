package org.innovateuk.ifs.management.viewmodel;

/**
 * Holder of model attributes for the Competition Management Competition 'In Assessment' view.
 */
public class CompetitionInAssessmentViewModel {

    private Long competitionId;
    private String competitionName;

    public CompetitionInAssessmentViewModel(Long competitionId, String competitionName) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }
}
