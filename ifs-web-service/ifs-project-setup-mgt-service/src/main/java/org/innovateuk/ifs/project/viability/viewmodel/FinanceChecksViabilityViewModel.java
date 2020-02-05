package org.innovateuk.ifs.project.viability.viewmodel;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.resource.ProjectResource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * View model for the Viability page
 */
public class FinanceChecksViabilityViewModel {

    private String organisationName;
    private boolean leadPartnerOrganisation;
    private Integer totalCosts;
    private BigDecimal percentageGrant;
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
    private Long applicationId;
    private String projectName;
    private final boolean projectIsActive;
    private final boolean collaborativeProject;
    private final boolean loanCompetition;
    private final boolean viabilityReadyToConfirm;


    public FinanceChecksViabilityViewModel(ProjectResource project,
                                           CompetitionResource competition,
                                           String organisationName,
                                           boolean leadPartnerOrganisation,
                                           Integer totalCosts,
                                           BigDecimal percentageGrant,
                                           Integer fundingSought,
                                           Integer otherPublicSectorFunding,
                                           Integer contributionToProject,
                                           String companyRegistrationNumber,
                                           Long turnover,
                                           Long headCount,
                                           Long projectId,
                                           boolean viabilityConfirmed,
                                           boolean approved,
                                           String approverName,
                                           LocalDate approvalDate,
                                           Long organisationId,
                                           String organisationSizeDescription,
                                           List<ProjectFinanceResource> projectFinances) {

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
        this.applicationId = project.getApplication();
        this.projectName = project.getName();
        this.projectIsActive = project.getProjectState().isActive();
        this.collaborativeProject = project.isCollaborativeProject();
        this.loanCompetition = competition.isLoan();
        this.viabilityReadyToConfirm = hasAllFundingLevelsWithinMaximum(projectFinances);
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

    public Long getOrganisationId() {
        return organisationId;
    }

    public Integer getTotalCosts() {
        return totalCosts;
    }

    public BigDecimal getPercentageGrant() {
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
        return viabilityConfirmed || !projectIsActive;
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
        return !isApproved() && projectIsActive;
    }

    public boolean isShowBackToFinanceCheckButton() {
        return isApproved() || !projectIsActive;
    }

    private boolean isApproved() {
        return approved;
    }

    public String getOrganisationSizeDescription() {
        return organisationSizeDescription;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public boolean isProjectIsActive() {
        return projectIsActive;
    }

    public boolean isCollaborativeProject() {
        return collaborativeProject;
    }

    public boolean isLoanCompetition() {
        return loanCompetition;
    }

    public boolean isViabilityReadyToConfirm() {
        return viabilityReadyToConfirm;
    }

    private boolean hasAllFundingLevelsWithinMaximum(List<ProjectFinanceResource> finances) {
        return finances.stream().allMatch(finance -> {
            int fundingLevel = finance.getGrantClaimPercentage();
            return finance.getMaximumFundingLevel() >= fundingLevel;
        });
    }
}
