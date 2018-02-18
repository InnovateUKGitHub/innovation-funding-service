package org.innovateuk.ifs.interview.viewmodel;


import org.innovateuk.ifs.competition.resource.CompetitionStatus;

/**
 * Holder of model attributes for the Competition Interview Panel dashboard
 */
public class InterviewPanelViewModel {
    private final long competitionId;
    private final String competitionName;
    private final CompetitionStatus competitionStatus;

    public InterviewPanelViewModel(long competitionId,
                                   String competitionName,
                                   CompetitionStatus competitionStatus) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.competitionStatus = competitionStatus;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public CompetitionStatus getCompetitionStatus() {
        return competitionStatus;
    }
}
