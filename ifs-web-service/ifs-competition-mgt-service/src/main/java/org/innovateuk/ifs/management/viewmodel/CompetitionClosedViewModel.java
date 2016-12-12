package org.innovateuk.ifs.management.viewmodel;

/**
 * Holder of model attributes for the Competition Management Competition 'Closed' view.
 */
public class CompetitionClosedViewModel {

    private Long competitionId;
    private String competitionName;

    public CompetitionClosedViewModel(Long competitionId, String competitionName) {
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
