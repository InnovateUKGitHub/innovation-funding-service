package org.innovateuk.ifs.registration.viewmodel;

public class InviteAndUserOrganisationDifferentViewModel {

    private String inviteOrganisationName;
    private String userOrganisationName;
    private String leadApplicantName;
    private String leadApplicantEmail;

    public InviteAndUserOrganisationDifferentViewModel(String inviteOrganisationName, String userOrganisationName, String leadApplicantName, String leadApplicantEmail) {
        this.leadApplicantName = leadApplicantName;
        this.inviteOrganisationName = inviteOrganisationName;
        this.leadApplicantEmail = leadApplicantEmail;
        this.userOrganisationName = userOrganisationName;
    }

    public String getInviteOrganisationName() {
        return inviteOrganisationName;
    }

    public String getUserOrganisationName() {
        return userOrganisationName;
    }

    public String getLeadApplicantName() {
        return leadApplicantName;
    }

    public String getLeadApplicantEmail() {
        return leadApplicantEmail;
    }
}


