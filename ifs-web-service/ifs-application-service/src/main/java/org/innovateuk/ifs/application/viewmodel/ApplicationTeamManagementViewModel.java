package org.innovateuk.ifs.application.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;

public class ApplicationTeamManagementViewModel {

    private long applicationId;
    private String applicationName;
    private UserResource leadApplicant;
    private OrganisationResource leadOrganisation;
    private OrganisationResource selectedOrganisation;
    private List<ApplicationTeamManagementOrganisationRowViewModel> organisations;
    private UserResource authenticatedUser;
    private Long authenticatedUserOrganisation;

    public ApplicationTeamManagementViewModel(long applicationId, String applicationName, UserResource leadApplicant, OrganisationResource leadOrganisation, OrganisationResource selectedOrganisation, UserResource authenticatedUser, Long authenticatedUserOrganisation, List<ApplicationTeamManagementOrganisationRowViewModel> organisations) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.leadApplicant = leadApplicant;
        this.leadOrganisation = leadOrganisation;
        this.selectedOrganisation = selectedOrganisation;
        this.authenticatedUser = authenticatedUser;
        this.authenticatedUserOrganisation = authenticatedUserOrganisation;
        this.organisations = organisations;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public UserResource getLeadApplicant() {
        return leadApplicant;
    }

    public void setLeadApplicant(UserResource leadApplicant) {
        this.leadApplicant = leadApplicant;
    }

    public OrganisationResource getLeadOrganisation() {
        return leadOrganisation;
    }

    public void setLeadOrganisation(OrganisationResource leadOrganisation) {
        this.leadOrganisation = leadOrganisation;
    }

    public OrganisationResource getSelectedOrganisation() {
        return selectedOrganisation;
    }

    public void setSelectedOrganisation(OrganisationResource selectedOrganisation) {
        this.selectedOrganisation = selectedOrganisation;
    }

    public List<ApplicationTeamManagementOrganisationRowViewModel> getOrganisations() {
        return organisations;
    }

    public void setOrganisations(List<ApplicationTeamManagementOrganisationRowViewModel> organisations) {
        this.organisations = organisations;
    }

    public UserResource getAuthenticatedUser() {
        return authenticatedUser;
    }

    public void setAuthenticatedUser(UserResource authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
    }

    public long getAuthenticatedUserOrganisation() {
        return authenticatedUserOrganisation;
    }

    public void setAuthenticatedUserOrganisation(long authenticatedUserOrganisation) {
        this.authenticatedUserOrganisation = authenticatedUserOrganisation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationTeamManagementViewModel that = (ApplicationTeamManagementViewModel) o;

        return new EqualsBuilder()
                .append(applicationId, that.applicationId)
                .append(authenticatedUserOrganisation, that.authenticatedUserOrganisation)
                .append(applicationName, that.applicationName)
                .append(leadApplicant, that.leadApplicant)
                .append(leadOrganisation, that.leadOrganisation)
                .append(selectedOrganisation, that.selectedOrganisation)
                .append(organisations, that.organisations)
                .append(authenticatedUser, that.authenticatedUser)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(applicationId)
                .append(applicationName)
                .append(leadApplicant)
                .append(leadOrganisation)
                .append(selectedOrganisation)
                .append(organisations)
                .append(authenticatedUser)
                .append(authenticatedUserOrganisation)
                .toHashCode();
    }
}
