package org.innovateuk.ifs.management.viewmodel;

/**
 * Holder of model attributes for the Competition Management Competition 'In Assessment' view.
 */
public class CompetitionInAssessmentViewModel {

    private Long competitionId;
    private String competitionName;
    private Integer changesSinceLastNotify;

    public CompetitionInAssessmentViewModel(Long competitionId, String competitionName, Integer changesSinceLastNotify) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.changesSinceLastNotify = changesSinceLastNotify;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public Integer getChangesSinceLastNotify() {
        return changesSinceLastNotify;
    }
}
