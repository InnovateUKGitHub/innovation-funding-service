package org.innovateuk.ifs.project.eligibility.viewmodel;


import org.innovateuk.ifs.project.finance.resource.EligibilityStatus;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;

import java.time.LocalDate;

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

    private boolean eligibilityApproved;
    private EligibilityStatus eligibilityStatus;
    private String approverFirstName;
    private String approverLastName;
    private LocalDate approvalDate;

    public FinanceChecksEligibilityViewModel(FinanceCheckEligibilityResource eligibilityOverview, String organisationName, String projectName,
                                             String applicationId, boolean leadPartnerOrganisation, Long projectId,
                                             boolean eligibilityApproved, EligibilityStatus eligibilityStatus, String approverFirstName,
                                             String approverLastName, LocalDate approvalDate) {
        this.eligibilityOverview = eligibilityOverview;
        this.organisationName = organisationName;
        this.projectName = projectName;
        this.applicationId = applicationId;
        this.leadPartnerOrganisation = leadPartnerOrganisation;
        this.projectId = projectId;

        this.eligibilityApproved = eligibilityApproved;
        this.eligibilityStatus = eligibilityStatus;
        this.approverFirstName = approverFirstName;
        this.approverLastName = approverLastName;
        this.approvalDate = approvalDate;
    }

    public boolean isReadOnly() {
        return eligibilityApproved;
    }

    private boolean isApproved() {
        return eligibilityApproved;
    }

    public boolean isShowSaveAndContinueButton() {
        return !isApproved();
    }

    public boolean isShowBackToFinanceCheckButton() {
        return isApproved();
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

    public boolean isShowApprovalMessage() {
        return isApproved();
    }

    public String getApproverName() { return getApproverFirstName().concat(getApproverLastName()); }

    public boolean isEligibilityApproved() {
        return eligibilityApproved;
    }

    public void setEligibilityApproved(boolean eligibilityApproved) {
        this.eligibilityApproved = eligibilityApproved;
    }

    public EligibilityStatus getEligibilityStatus() {
        return eligibilityStatus;
    }

    public void setEligibilityStatus(EligibilityStatus eligibilityStatus) {
        this.eligibilityStatus = eligibilityStatus;
    }

    public String getApproverFirstName() {
        return approverFirstName;
    }

    public void setApproverFirstName(String approverFirstName) {
        this.approverFirstName = approverFirstName;
    }

    public String getApproverLastName() {
        return approverLastName;
    }

    public void setApproverLastName(String approverLastName) {
        this.approverLastName = approverLastName;
    }

    public LocalDate getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDate approvalDate) {
        this.approvalDate = approvalDate;
    }
}
