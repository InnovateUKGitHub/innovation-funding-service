package org.innovateuk.ifs.registration.viewmodel;

public class AcceptRejectApplicationInviteViewModel {

    private String leadOrganisation;
    private String leadApplicantName;
    private String inviteOrganisationName;
    private String leadApplicantEmail;
    private String competitionName;
    private Long competionId;

    public AcceptRejectApplicationInviteViewModel(String leadApplicantName, String leadApplicantEmail, String leadOrganisationName, String inviteOrganisationName, String competitionName, Long competetionId) {
        this.leadOrganisation = leadOrganisationName;
        this.leadApplicantName = leadApplicantName;
        this.inviteOrganisationName = inviteOrganisationName;
        this.leadApplicantEmail = leadApplicantEmail;
        this.competitionName = competitionName;
        this.competionId = competetionId;
    }

    public String getLeadOrganisation() {
        return leadOrganisation;
    }

    public void setLeadOrganisation(String leadOrganisation) {
        this.leadOrganisation = leadOrganisation;
    }

    public String getLeadApplicantName() {
        return leadApplicantName;
    }

    public void setLeadApplicantName(String leadApplicantName) {
        this.leadApplicantName = leadApplicantName;
    }

    public String getInviteOrganisationName() {
        return inviteOrganisationName;
    }

    public void setInviteOrganisationName(String inviteOrganisationName) {
        this.inviteOrganisationName = inviteOrganisationName;
    }

    public String getLeadApplicantEmail() {
        return leadApplicantEmail;
    }

    public void setLeadApplicantEmail(String leadApplicantEmail) {
        this.leadApplicantEmail = leadApplicantEmail;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public Long getCompetionId() {
        return competionId;
    }

    public void setCompetionId(Long competionId) {
        this.competionId = competionId;
    }
}
