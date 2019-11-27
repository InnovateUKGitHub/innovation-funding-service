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
    private boolean hasApplicationFinances;

    public FinanceCheckEligibilityResource() {

    }

    public FinanceCheckEligibilityResource(Long projectId, Long organisationId, Long durationInMonths, BigDecimal totalCost, BigDecimal percentageGrant, BigDecimal fundingSought, BigDecimal otherPublicSectorFunding, BigDecimal contributionToProject, boolean hasApplicationFinances) {
        this.projectId = projectId;
        this.organisationId = organisationId;
        this.durationInMonths = durationInMonths;
        this.totalCost = totalCost;
        this.percentageGrant = percentageGrant;
        this.fundingSought = fundingSought;
        this.otherPublicSectorFunding = otherPublicSectorFunding;
        this.contributionToProject = contributionToProject;
        this.hasApplicationFinances = hasApplicationFinances;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public Long getDurationInMonths() {
        return durationInMonths;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public BigDecimal getPercentageGrant() {
        return percentageGrant;
    }

    public BigDecimal getFundingSought() {
        return fundingSought;
    }

    public BigDecimal getOtherPublicSectorFunding() {
        return otherPublicSectorFunding;
    }

    public BigDecimal getContributionToProject() {
        return contributionToProject;
    }

    public boolean isHasApplicationFinances() {
        return hasApplicationFinances;
    }
}
