package org.innovateuk.ifs.project.viability.viewmodel;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.user.resource.OrganisationSize;

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

    // currently always null
    private Integer turnover;

    // currently always null
    private Integer headCount;

    private OrganisationSize organisationSize;

    private Long projectId;
    private boolean viabilityConfirmed;
    private boolean approved;
    private String approverName;
    private LocalDate approvalDate;

    public FinanceChecksViabilityViewModel(String organisationName, boolean leadPartnerOrganisation, Integer totalCosts,
                                           Integer percentageGrant, Integer fundingSought, Integer otherPublicSectorFunding,
                                           Integer contributionToProject, String companyRegistrationNumber,
                                           Integer turnover, Integer headCount, OrganisationSize organisationSize,
                                           Long projectId, boolean viabilityConfirmed,
                                           boolean approved, String approverName, LocalDate approvalDate) {

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
        this.organisationSize = organisationSize;
        this.projectId = projectId;
        this.viabilityConfirmed = viabilityConfirmed;
        this.approved = approved;
        this.approverName = approverName;
        this.approvalDate = approvalDate;
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

    public Integer getTurnover() {
        return turnover;
    }

    public Integer getHeadCount() {
        return headCount;
    }

    public OrganisationSize getOrganisationSize() {
        return organisationSize;
    }

    public Long getProjectId() {
        return projectId;
    }

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
}
