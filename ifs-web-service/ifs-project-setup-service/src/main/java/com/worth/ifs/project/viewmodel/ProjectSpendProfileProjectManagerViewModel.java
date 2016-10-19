package com.worth.ifs.project.viewmodel;

import com.worth.ifs.user.resource.OrganisationResource;

import java.util.List;
import java.util.Map;

/**
 * Module: innovation-funding-service
 * View model for project manager Spend profile
 **/
public class ProjectSpendProfileProjectManagerViewModel {
    private Long projectId;
    private String projectName;
    private Map<String, Boolean> partnerSpendProfileProgress;
    private Map<String, Boolean> editablePartners;
    private List<OrganisationResource> partnerOrganisations;
    private boolean submitted;

    public ProjectSpendProfileProjectManagerViewModel(Long projectId, String projectName, Map<String, Boolean> partnerSpendProfileProgress,
                                                      List<OrganisationResource> partnerOrganisations, boolean submitted, Map<String, Boolean> editablePartners) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.partnerSpendProfileProgress = partnerSpendProfileProgress;
        this.partnerOrganisations = partnerOrganisations;
        this.submitted = submitted;
        this.editablePartners = editablePartners;
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

    public Boolean isMarkAsComplete() {
        return partnerSpendProfileProgress.values()
                .stream()
                .allMatch(markAsComplete -> markAsComplete);
    }

    public boolean isSubmitted() {
        return submitted;
    }
}
