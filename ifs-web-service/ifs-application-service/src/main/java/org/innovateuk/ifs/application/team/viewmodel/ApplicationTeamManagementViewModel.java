package org.innovateuk.ifs.application.team.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
    private boolean organisationExists;

    public ApplicationTeamManagementViewModel(long applicationId,
                                              String applicationName,
                                              Long organisationId,
                                              Long inviteOrganisationId,
                                              String organisationName,
                                              boolean leadOrganisation,
                                              boolean userLeadApplicant,
                                              List<ApplicationTeamManagementApplicantRowViewModel> applicants,
                                              boolean organisationExists) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.organisationId = organisationId;
        this.inviteOrganisationId = inviteOrganisationId;
        this.organisationName = organisationName;
        this.leadOrganisation = leadOrganisation;
        this.userLeadApplicant = userLeadApplicant;
        this.applicants = applicants;
        this.organisationExists = organisationExists;
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

    public boolean isOrganisationExists() {
        return organisationExists;
    }

    public void setOrganisationExists(boolean organisationExists) {
        this.organisationExists = organisationExists;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationTeamManagementViewModel that = (ApplicationTeamManagementViewModel) o;

        return new EqualsBuilder()
                .append(applicationId, that.applicationId)
                .append(leadOrganisation, that.leadOrganisation)
                .append(userLeadApplicant, that.userLeadApplicant)
                .append(applicationName, that.applicationName)
                .append(organisationId, that.organisationId)
                .append(inviteOrganisationId, that.inviteOrganisationId)
                .append(organisationName, that.organisationName)
                .append(applicants, that.applicants)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applicationId)
                .append(applicationName)
                .append(organisationId)
                .append(inviteOrganisationId)
                .append(organisationName)
                .append(leadOrganisation)
                .append(userLeadApplicant)
                .append(applicants)
                .toHashCode();
    }
}
