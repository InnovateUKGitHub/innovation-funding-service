package com.worth.ifs.assessment.viewmodel;

/**
 * ViewModel of a CompetitionInvite.
 */
public class CompetitionInviteViewModel {

    private String competitionName;

    public CompetitionInviteViewModel(String competitionName) {
        this.competitionName = competitionName;
    }

    public String getCompetitionName() {
        return competitionName;
    }
}
