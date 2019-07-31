package org.innovateuk.ifs.competition.viewmodel;

public class CompetitionTermsViewModel {

    private final long competitionId;

    public CompetitionTermsViewModel(long competitionId) {
        this.competitionId = competitionId;
    }

    public long getCompetitionId() {
        return competitionId;
    }
}