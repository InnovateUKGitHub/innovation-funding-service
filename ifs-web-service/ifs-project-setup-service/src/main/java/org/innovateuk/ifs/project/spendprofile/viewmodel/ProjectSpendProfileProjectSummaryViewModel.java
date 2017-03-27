package org.innovateuk.ifs.project.spendprofile.viewmodel;

import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;
import java.util.Map;

/**
 * Module: innovation-funding-service
 * View model for project manager Spend profile
 **/
public class ProjectSpendProfileProjectSummaryViewModel {
    private Long projectId;
    private Long applicationId;
    private String projectName;
    private Map<String, Boolean> partnerSpendProfileProgress;
    private Map<String, Boolean> editablePartners;
    private List<OrganisationResource> partnerOrganisations;
    private OrganisationResource leadOrganisation;
    private boolean submitted;
    private boolean approved;

    public ProjectSpendProfileProjectSummaryViewModel(Long projectId, Long applicationId, String projectName,
                                                      Map<String, Boolean> partnerSpendProfileProgress,
                                                      List<OrganisationResource> partnerOrganisations,
                                                      OrganisationResource leadOrganisation, boolean submitted,
                                                      Map<String, Boolean> editablePartners,
                                                      boolean approved) {
        this.projectId = projectId;
        this.applicationId = applicationId;
        this.projectName = projectName;
        this.partnerSpendProfileProgress = partnerSpendProfileProgress;
        this.partnerOrganisations = partnerOrganisations;
        this.leadOrganisation = leadOrganisation;
        this.submitted = submitted;
        this.editablePartners = editablePartners;
        this.approved = approved;
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

    public Map<String, Boolean> getPartnerSpendProfileProgress() {
        return partnerSpendProfileProgress;
    }

    public Map<String, Boolean> getEditablePartners() {
        return editablePartners;
    }

    public void setPartnerSpendProfileProgress(Map<String, Boolean> partnerSpendProfileProgress) {
        this.partnerSpendProfileProgress = partnerSpendProfileProgress;
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
        return partnerSpendProfileProgress.values()
                .stream()
                .allMatch(markAsComplete -> markAsComplete);
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public boolean isApproved() { return approved; }

    public Long getApplicationId() {
        return applicationId;
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
                .append(partnerSpendProfileProgress, that.partnerSpendProfileProgress)
                .append(editablePartners, that.editablePartners)
                .append(partnerOrganisations, that.partnerOrganisations)
                .append(approved, that.approved)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(projectId)
                .append(applicationId)
                .append(projectName)
                .append(partnerSpendProfileProgress)
                .append(editablePartners)
                .append(partnerOrganisations)
                .append(submitted)
                .append(approved)
                .toHashCode();
    }
}
