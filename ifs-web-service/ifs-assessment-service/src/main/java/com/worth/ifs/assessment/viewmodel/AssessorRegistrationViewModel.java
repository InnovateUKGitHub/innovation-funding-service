package com.worth.ifs.assessment.viewmodel;

/**
 * Holder of model attributes for the Assessor Registration.
 */
public class AssessorRegistrationViewModel {
    private String competitionInviteHash;
    private String email;

    public AssessorRegistrationViewModel(String competitionInviteHash, String email) {
        this.email = email;
        this.competitionInviteHash = competitionInviteHash;
    }

    public String getCompetitionInviteHash() {
        return competitionInviteHash;
    }

    public void setCompetitionInviteHash(String competitionInviteHash) {
        this.competitionInviteHash = competitionInviteHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
