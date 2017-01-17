package org.innovateuk.ifs.project.eligibility.viewmodel;


import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;

/**
 * View model backing the internal Finance Team members view of the Finance Check Eligibility page
 */
public class FinanceChecksEligibilityViewModel {
    private FinanceCheckEligibilityResource eligibilityOverview;
    private String organisationName;
    private boolean leadPartnerOrganisation;
    private String projectName;
    private String applicationId;
    private Long projectId;

    public FinanceChecksEligibilityViewModel(FinanceCheckEligibilityResource eligibilityOverview, String organisationName, String projectName, String applicationId, boolean leadPartnerOrganisation, Long projectId) {
        this.eligibilityOverview = eligibilityOverview;
        this.organisationName = organisationName;
        this.projectName = projectName;
        this.applicationId = applicationId;
        this.leadPartnerOrganisation = leadPartnerOrganisation;
        this.projectId = projectId;
    }

    public FinanceCheckEligibilityResource getEligibilityOverview() {
        return eligibilityOverview;
    }

    public void setEligibilityOverview(FinanceCheckEligibilityResource eligibilityResource) {
        this.eligibilityOverview = eligibilityResource;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public boolean isLeadPartnerOrganisation() {
        return leadPartnerOrganisation;
    }

    public void setLeadPartnerOrganisation(boolean leadPartnerOrganisation) {
        this.leadPartnerOrganisation = leadPartnerOrganisation;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
