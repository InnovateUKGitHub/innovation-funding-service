package org.innovateuk.ifs.application.viewmodel;

import java.util.List;

/**
 * Holder of model attributes for the Update Organisation view.
 */
public class ApplicationTeamManagementViewModel {

    private long applicationId;
    private String applicationName;
    private Long organisationId;
    private Long inviteOrganisationId;
    private String organisationName;
    private boolean leadOrganisation;
    private boolean userLeadApplicant;
    private List<ApplicationTeamManagementApplicantRowViewModel> applicants;

    public ApplicationTeamManagementViewModel(long applicationId, String applicationName, Long organisationId, Long inviteOrganisationId, String organisationName, boolean leadOrganisation, boolean userLeadApplicant, List<ApplicationTeamManagementApplicantRowViewModel> applicants) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.organisationId = organisationId;
        this.inviteOrganisationId = inviteOrganisationId;
        this.organisationName = organisationName;
        this.leadOrganisation = leadOrganisation;
        this.userLeadApplicant = userLeadApplicant;
        this.applicants = applicants;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public Long getInviteOrganisationId() {
        return inviteOrganisationId;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public boolean isLeadOrganisation() {
        return leadOrganisation;
    }

    public boolean isUserLeadApplicant() {
        return userLeadApplicant;
    }

    public List<ApplicationTeamManagementApplicantRowViewModel> getApplicants() {
        return applicants;
    }


}
