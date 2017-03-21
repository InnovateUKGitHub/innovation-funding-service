package org.innovateuk.ifs.project.financecheck.eligibility.viewmodel;


import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.project.finance.resource.EligibilityRagStatus;
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
    private Long applicationId;
    private Long projectId;
    private Long organisationId;

    private boolean eligibilityApproved;
    private EligibilityRagStatus eligibilityRagStatus;
    private String approverFirstName;
    private String approverLastName;
    private LocalDate approvalDate;

    private boolean externalView;
    private boolean isUsingJesFinances;
    private FileDetailsViewModel jesFileDetails;

    public FinanceChecksEligibilityViewModel(FinanceCheckEligibilityResource eligibilityOverview, String organisationName, String projectName,
                                             Long applicationId, boolean leadPartnerOrganisation, Long projectId, Long organisationId,
                                             boolean eligibilityApproved, EligibilityRagStatus eligibilityRagStatus, String approverFirstName,
                                             String approverLastName, LocalDate approvalDate, boolean externalView, boolean isUsingJesFinances, FileDetailsViewModel jesFileDetailsViewModel) {
        this.eligibilityOverview = eligibilityOverview;
        this.organisationName = organisationName;
        this.projectName = projectName;
        this.applicationId = applicationId;
        this.leadPartnerOrganisation = leadPartnerOrganisation;
        this.projectId = projectId;
        this.organisationId = organisationId;

        this.eligibilityApproved = eligibilityApproved;
        this.eligibilityRagStatus = eligibilityRagStatus;
        this.approverFirstName = approverFirstName;
        this.approverLastName = approverLastName;
        this.approvalDate = approvalDate;

        this.externalView = externalView;
        this.isUsingJesFinances = isUsingJesFinances;
        this.jesFileDetails = jesFileDetailsViewModel;
    }

    public boolean isApproved() {
        return eligibilityApproved;
    }

    public boolean isShowSaveAndContinueButton() {
        return !isApproved();
    }

    public boolean isShowBackToFinanceCheckButton() {
        return isApproved();
    }

    public boolean isShowApprovalMessage() {
        return isApproved();
    }

    public String getApproverName()
    {
        return StringUtils.trim(getApproverFirstName() + " " + getApproverLastName());
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

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public boolean isEligibilityApproved() {
        return eligibilityApproved;
    }

    public void setEligibilityApproved(boolean eligibilityApproved) {
        this.eligibilityApproved = eligibilityApproved;
    }

    public EligibilityRagStatus getEligibilityRagStatus() {
        return eligibilityRagStatus;
    }

    public void setEligibilityRagStatus(EligibilityRagStatus eligibilityRagStatus) {
        this.eligibilityRagStatus = eligibilityRagStatus;
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

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public boolean isExternalView() {
        return externalView;
    }

    public void setExternalView(boolean externalView) {
        this.externalView = externalView;
    }

    public boolean isUsingJesFinances() {
        return isUsingJesFinances;
    }

    public void setUsingJesFinances(boolean usingJesFinances) {
        isUsingJesFinances = usingJesFinances;
    }

    public FileDetailsViewModel getJesFileDetails() {
        return jesFileDetails;
    }

    public void setJesFileDetails(FileDetailsViewModel jesFileDetails) {
        this.jesFileDetails = jesFileDetails;
    }

}
