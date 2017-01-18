package org.innovateuk.ifs.project.finance.resource;

import java.math.BigDecimal;

/**
 * A resource object to return finance check eligibility data for a project (per partner organisations).
 */
public class FinanceCheckEligibilityResource {
    private Long projectId;
    private Long organisationId;
    private Long durationInMonths;
    private BigDecimal totalCost;
    private BigDecimal percentageGrant;
    private BigDecimal fundingSought;
    private BigDecimal otherPublicSectorFunding;
    private BigDecimal contributionToProject;

    public FinanceCheckEligibilityResource() {

    }

    public FinanceCheckEligibilityResource(Long projectId, Long organisationId, Long durationInMonths, BigDecimal totalCost, BigDecimal percentageGrant, BigDecimal fundingSought, BigDecimal otherPublicSectorFunding, BigDecimal contributionToProject) {
        this.projectId = projectId;
        this.organisationId = organisationId;
        this.durationInMonths = durationInMonths;
        this.totalCost = totalCost;
        this.percentageGrant = percentageGrant;
        this.fundingSought = fundingSought;
        this.otherPublicSectorFunding = otherPublicSectorFunding;
        this.contributionToProject = contributionToProject;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getOrganisationId() { return organisationId; }

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


    public BigDecimal getOtherPublicSectorFunding() {
        return otherPublicSectorFunding;
    }

    public void setOtherPublicSectorFunding(BigDecimal otherPublicSectorFunding) {
        this.otherPublicSectorFunding = otherPublicSectorFunding;
    }

    public BigDecimal getPercentageGrant() {
        return percentageGrant;
    }

    public void setPercentageGrant(BigDecimal percentageGrant) {
        this.percentageGrant = percentageGrant;
    }

    public BigDecimal getContributionToProject() { return contributionToProject; }

    public void setContributionToProject(BigDecimal contributionToProject) {this.contributionToProject = contributionToProject; }

    public BigDecimal getFundingSought() { return fundingSought; }

    public void setFundingSought(BigDecimal fundingSought) {this.fundingSought = fundingSought; }
}
