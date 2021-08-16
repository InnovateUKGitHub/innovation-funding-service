package org.innovateuk.ifs.project.spendprofile.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.spendprofile.OrganisationReviewDetails;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Module: innovation-funding-service
 * View model for project manager Spend profile
 **/
public class ProjectSpendProfileProjectSummaryViewModel {
    private Long projectId;
    private Long applicationId;
    private String projectName;
    private Map<Long, OrganisationReviewDetails> editablePartners;
    private List<OrganisationResource> partnerOrganisations;
    private OrganisationResource leadOrganisation;
    private boolean submitted;
    private boolean approved;
    private boolean rejected;
    private boolean monitoringOfficer;
    private boolean moSpendProfileJourneyUpdateEnabled;
    private OrganisationReviewDetails leadOrganisationReviewDetails;
    private UserResource loggedInUser;

    public ProjectSpendProfileProjectSummaryViewModel(Long projectId,
                                                      Long applicationId,
                                                      String projectName,
                                                      List<OrganisationResource> partnerOrganisations,
                                                      OrganisationResource leadOrganisation,
                                                      boolean submitted,
                                                      Map<Long, OrganisationReviewDetails> editablePartners,
                                                      boolean approved,
                                                      boolean rejected,
                                                      boolean monitoringOfficer,
                                                      boolean moSpendProfileJourneyUpdateEnabled,
                                                      UserResource loggedInUser) {
        this.projectId = projectId;
        this.applicationId = applicationId;
        this.projectName = projectName;
        this.partnerOrganisations = partnerOrganisations;
        this.leadOrganisation = leadOrganisation;
        this.submitted = submitted;
        this.editablePartners = editablePartners;
        this.approved = approved;
        this.rejected = rejected;
        this.monitoringOfficer = monitoringOfficer;
        this.moSpendProfileJourneyUpdateEnabled = moSpendProfileJourneyUpdateEnabled;
        this.leadOrganisationReviewDetails = getLeadOrganisationReviewDetails(leadOrganisation, editablePartners);
        this.loggedInUser = loggedInUser;
    }

    private OrganisationReviewDetails getLeadOrganisationReviewDetails(OrganisationResource leadOrganisation, Map<Long, OrganisationReviewDetails> editablePartners) {
        return editablePartners.values().stream()
                .filter(organisationReviewDetails -> organisationReviewDetails.getOrganisationId() == leadOrganisation.getId())
                .findFirst()
                .orElse(null);
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Map<Long, OrganisationReviewDetails> getEditablePartners() {
        return editablePartners;
    }

    public List<OrganisationResource> getPartnerOrganisations() {
        return partnerOrganisations;
    }

    public void setPartnerOrganisations(List<OrganisationResource> partnerOrganisations) {
        this.partnerOrganisations = partnerOrganisations;
    }

    public OrganisationResource getLeadOrganisation() {
        return leadOrganisation;
    }

    public Boolean isMarkAsComplete() {
        return editablePartners.values()
                .stream()
                .allMatch(OrganisationReviewDetails::isMarkedComplete);
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public boolean isApproved() {
        return approved;
    }

    public boolean isRejected() {
        return rejected;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public boolean isMonitoringOfficer() {
        return monitoringOfficer;
    }

    public void setMonitoringOfficer(boolean monitoringOfficer) {
        this.monitoringOfficer = monitoringOfficer;
    }

    public boolean showMoSpendProfileJourney() {
        return moSpendProfileJourneyUpdateEnabled && monitoringOfficer;
    }

    public boolean isMoSpendProfileJourneyUpdateEnabled() {
        return moSpendProfileJourneyUpdateEnabled;
    }

    public boolean userCanReviewSpendProfile() {
        return submitted && !(approved || rejected);
    }

    public boolean isSpendProfileReviewedByMO() {
        return leadOrganisationReviewDetails != null
                && leadOrganisationReviewDetails.getReviewedBy().hasRole(Role.MONITORING_OFFICER);
    }

    public boolean isSpendProfileReviewedByIfsAdmin() {
        return leadOrganisationReviewDetails != null
                && leadOrganisationReviewDetails.getReviewedBy().hasRole(Role.IFS_ADMINISTRATOR);
    }

    public boolean loggedInUserIsMoOnProject() {
        return leadOrganisationReviewDetails.getReviewedBy().getId().equals(loggedInUser.getId());
    }

    public UserResource getLoggedInUser() {
        return loggedInUser;
    }

    public ZonedDateTime spendProfileReviewedOn() {
        return Optional.ofNullable(leadOrganisationReviewDetails)
                .map(OrganisationReviewDetails::getReviewedOn)
                .orElse(null);
    }

    public UserResource spendProfileReviewedBy() {
        return Optional.ofNullable(leadOrganisationReviewDetails)
                .map(OrganisationReviewDetails::getReviewedBy)
                .orElse(null);
    }

    public String userWhoApprovedSpendProfile() {
        return spendProfileReviewedBy().getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectSpendProfileProjectSummaryViewModel that = (ProjectSpendProfileProjectSummaryViewModel) o;

        return new EqualsBuilder()
                .append(submitted, that.submitted)
                .append(projectId, that.projectId)
                .append(applicationId, that.applicationId)
                .append(projectName, that.projectName)
                .append(editablePartners, that.editablePartners)
                .append(partnerOrganisations, that.partnerOrganisations)
                .append(approved, that.approved)
                .append(monitoringOfficer, that.monitoringOfficer)
                .append(moSpendProfileJourneyUpdateEnabled, that.moSpendProfileJourneyUpdateEnabled)
                .append(loggedInUser, that.loggedInUser)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(projectId)
                .append(applicationId)
                .append(projectName)
                .append(editablePartners)
                .append(partnerOrganisations)
                .append(submitted)
                .append(approved)
                .append(monitoringOfficer)
                .append(moSpendProfileJourneyUpdateEnabled)
                .append(loggedInUser)
                .toHashCode();
    }
}
