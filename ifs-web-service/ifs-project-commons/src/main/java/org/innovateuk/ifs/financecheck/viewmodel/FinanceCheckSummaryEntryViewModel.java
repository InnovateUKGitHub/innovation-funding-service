package org.innovateuk.ifs.financecheck.viewmodel;

import java.math.BigDecimal;

public class FinanceCheckSummaryEntryViewModel {

    private Long projectId;
    private Long organisationId;
    private Long durationInMonths;
    private BigDecimal totalCost;
    private BigDecimal percentageGrant;
    private BigDecimal fundingSought;
    private BigDecimal otherPublicSectorFunding;
    private BigDecimal contributionToProject;
    private BigDecimal contributionPercentage;
    private boolean hasApplicationFinances;

    public FinanceCheckSummaryEntryViewModel(Long projectId, Long organisationId, Long durationInMonths, BigDecimal totalCost,
                                             BigDecimal percentageGrant, BigDecimal fundingSought,
                                             BigDecimal otherPublicSectorFunding, BigDecimal contributionToProject,
                                             BigDecimal contributionPercentage, boolean hasApplicationFinances) {
        this.projectId = projectId;
        this.organisationId = organisationId;
        this.durationInMonths = durationInMonths;
        this.totalCost = totalCost;
        this.percentageGrant = percentageGrant;
        this.fundingSought = fundingSought;
        this.otherPublicSectorFunding = otherPublicSectorFunding;
        this.contributionToProject = contributionToProject;
        this.contributionPercentage = contributionPercentage;
        this.hasApplicationFinances = hasApplicationFinances;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public Long getDurationInMonths() {
        return durationInMonths;
    }

    public void setDurationInMonths(Long durationInMonths) {
        this.durationInMonths = durationInMonths;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public BigDecimal getPercentageGrant() {
        return percentageGrant;
    }

    public void setPercentageGrant(BigDecimal percentageGrant) {
        this.percentageGrant = percentageGrant;
    }

    public BigDecimal getFundingSought() {
        return fundingSought;
    }

    public void setFundingSought(BigDecimal fundingSought) {
        this.fundingSought = fundingSought;
    }

    public BigDecimal getOtherPublicSectorFunding() {
        return otherPublicSectorFunding;
    }

    public void setOtherPublicSectorFunding(BigDecimal otherPublicSectorFunding) {
        this.otherPublicSectorFunding = otherPublicSectorFunding;
    }

    public BigDecimal getContributionToProject() {
        return contributionToProject;
    }

    public void setContributionToProject(BigDecimal contributionToProject) {
        this.contributionToProject = contributionToProject;
    }

    public BigDecimal getContributionPercentage() {
        return contributionPercentage;
    }

    public void setContributionPercentage(BigDecimal contributionPercentage) {
        this.contributionPercentage = contributionPercentage;
    }

    public boolean isHasApplicationFinances() {
        return hasApplicationFinances;
    }

    public void setHasApplicationFinances(boolean hasApplicationFinances) {
        this.hasApplicationFinances = hasApplicationFinances;
    }
}
