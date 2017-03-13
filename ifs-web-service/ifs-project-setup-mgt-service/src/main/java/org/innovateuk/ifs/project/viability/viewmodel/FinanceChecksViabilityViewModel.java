package org.innovateuk.ifs.project.viability.viewmodel;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;

/**
 * View model for the Viability page
 */
public class FinanceChecksViabilityViewModel {

    private String organisationName;
    private boolean leadPartnerOrganisation;
    private Integer totalCosts;
    private Integer percentageGrant;
    private Integer fundingSought;
    private Integer otherPublicSectorFunding;
    private Integer contributionToProject;
    private String companyRegistrationNumber;
    private Long turnover;
    private Long headCount;

    private Long projectId;
    private Long organisationId;
    private boolean viabilityConfirmed;
    private boolean approved;
    private String approverName;
    private LocalDate approvalDate;
    private String organisationSizeDescription;


    public FinanceChecksViabilityViewModel(String organisationName, boolean leadPartnerOrganisation, Integer totalCosts,
                                           Integer percentageGrant, Integer fundingSought, Integer otherPublicSectorFunding,
                                           Integer contributionToProject, String companyRegistrationNumber,
                                           Long turnover, Long headCount,
                                           Long projectId, boolean viabilityConfirmed,
                                           boolean approved, String approverName, LocalDate approvalDate, Long organisationId,
                                           String organisationSizeDescription) {

        this.organisationName = organisationName;
        this.leadPartnerOrganisation = leadPartnerOrganisation;
        this.totalCosts = totalCosts;
        this.percentageGrant = percentageGrant;
        this.fundingSought = fundingSought;
        this.otherPublicSectorFunding = otherPublicSectorFunding;
        this.contributionToProject = contributionToProject;
        this.companyRegistrationNumber = companyRegistrationNumber;
        this.turnover = turnover;
        this.headCount = headCount;
        this.projectId = projectId;
        this.viabilityConfirmed = viabilityConfirmed;
        this.approved = approved;
        this.approverName = approverName;
        this.approvalDate = approvalDate;
        this.organisationId = organisationId;
        this.organisationSizeDescription = organisationSizeDescription;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public boolean isLeadPartnerOrganisation() {
        return leadPartnerOrganisation;
    }

    public String getCompanyRegistrationNumber() {
        return companyRegistrationNumber;
    }

    public Long getTurnover() {
        return turnover;
    }

    public Long getHeadCount() {
        return headCount;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getOrganisationId() { return organisationId; }

    public Integer getTotalCosts() {
        return totalCosts;
    }

    public Integer getPercentageGrant() {
        return percentageGrant;
    }

    public Integer getFundingSought() {
        return fundingSought;
    }

    public Integer getOtherPublicSectorFunding() {
        return otherPublicSectorFunding;
    }

    public Integer getContributionToProject() {
        return contributionToProject;
    }

    public boolean isReadOnly() {
        return viabilityConfirmed;
    }

    public boolean isShowApprovalMessage() {
        return isApproved();
    }

    public String getApproverName() {

        return StringUtils.trim(approverName);
    }

    public LocalDate getApprovalDate() {
        return approvalDate;
    }

    public boolean isShowSaveAndContinueButton() {
        return !isApproved();
    }

    public boolean isShowBackToFinanceCheckButton() {
        return isApproved();
    }

    private boolean isApproved() {
        return approved;
    }


    public String getOrganisationSizeDescription() {
        return organisationSizeDescription;
    }
}
