package org.innovateuk.ifs.registration.viewmodel;

public class AcceptRejectApplicationInviteViewModel {

    private String leadOrganisationName;
    private String leadApplicantName;
    private String inviteOrganisationName;
    private String leadApplicantEmail;
    private String competitionName;
    private Long competitionId;
    private boolean inviteOrganisationExists;

    public AcceptRejectApplicationInviteViewModel(String leadApplicantName, String leadApplicantEmail, String leadOrganisationName, String inviteOrganisationName, String competitionName, Long competitionId, boolean inviteOrganisationExists) {
        this.leadOrganisationName = leadOrganisationName;
        this.leadApplicantName = leadApplicantName;
        this.inviteOrganisationName = inviteOrganisationName;
        this.leadApplicantEmail = leadApplicantEmail;
        this.competitionName = competitionName;
        this.competitionId = competitionId;
        this.inviteOrganisationExists = inviteOrganisationExists;
    }

    public String getLeadOrganisationName() {
        return leadOrganisationName;
    }

    public String getLeadApplicantName() {
        return leadApplicantName;
    }

    public String getInviteOrganisationName() {
        return inviteOrganisationName;
    }

    public String getLeadApplicantEmail() {
        return leadApplicantEmail;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public boolean isInviteOrganisationExists() {
        return inviteOrganisationExists;
    }
}


