package org.innovateuk.ifs.project.viability.viewmodel;

import org.apache.commons.lang3.StringUtils;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.project.finance.resource.ViabilityState;
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
    private ViabilityState viabilityState;
    private boolean approved;
    private String approverName;
    private LocalDate approvalDate;
    private String resetName;
    private LocalDate resetDate;
    private String organisationSizeDescription;
    private Long applicationId;
    private String projectName;
    private final boolean projectIsActive;
    private final boolean collaborativeProject;
    private final boolean loanCompetition;
    private final boolean procurementCompetition;
    private final boolean viabilityReadyToConfirm;
    private final boolean hasGrantClaimPercentage;
    private final boolean ktpCompetition;
    private final boolean resetableGolState;

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
                                           ViabilityState viabilityState,
                                           boolean approved,
                                           String approverName,
                                           LocalDate approvalDate,
                                           String resetName,
                                           LocalDate resetDate,
                                           Long organisationId,
                                           String organisationSizeDescription,
                                           List<ProjectFinanceResource> projectFinances,
                                           boolean resetableGolState) {

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
        this.viabilityState = viabilityState;
        this.approved = approved;
        this.approverName = approverName;
        this.approvalDate = approvalDate;
        this.resetName = resetName;
        this.resetDate = resetDate;
        this.organisationId = organisationId;
        this.organisationSizeDescription = organisationSizeDescription;
        this.applicationId = project.getApplication();
        this.projectName = project.getName();
        this.projectIsActive = project.getProjectState().isActive();
        this.collaborativeProject = project.isCollaborativeProject();
        this.loanCompetition = competition.isLoan();
        this.procurementCompetition  = competition.isProcurement();
        this.viabilityReadyToConfirm = hasAllFundingLevelsWithinMaximum(projectFinances);
        this.hasGrantClaimPercentage = competition.getFinanceRowTypes().contains(FinanceRowType.FINANCE);
        this.ktpCompetition = competition.isKtp();
        this.resetableGolState = resetableGolState;
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
        return viabilityState == ViabilityState.APPROVED || !projectIsActive;
    }

    public boolean isShowApprovalMessage() {
        return isApproved();
    }

    public boolean isShowResetMessage() {
        return ViabilityState.REVIEW == viabilityState && resetDate != null && resetName != null;
    }

    public String getApproverName() {
        return StringUtils.trim(approverName);
    }

    public String getResetName() {
        return resetName;
    }

    public LocalDate getResetDate() {
        return resetDate;
    }

    public boolean isCanReset() {
        return approved && projectIsActive && resetableGolState;
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

    public boolean isKtpCompetition() {
        return ktpCompetition;
    }

    public boolean isProcurementCompetition() {
        return procurementCompetition;
    }

    public boolean isViabilityReadyToConfirm() {
        return viabilityReadyToConfirm;
    }

    public boolean isHasGrantClaimPercentage() {
        return hasGrantClaimPercentage;
    }

    private boolean hasAllFundingLevelsWithinMaximum(List<ProjectFinanceResource> finances) {
        return finances.stream().allMatch(finance ->
                BigDecimal.valueOf(finance.getMaximumFundingLevel()).compareTo(finance.getGrantClaimPercentage()) >=0);
    }
}
