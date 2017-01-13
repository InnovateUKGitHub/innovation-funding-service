package org.innovateuk.ifs.project.finance.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * A resource object to return finance check eligibility data for a project (per partner organisations).
 */
public class FinanceCheckEligibilityResource {
    private Long projectId;
    private String projectName;
    private Long organisationId;
    private String organisationName;
    private String applicationId;
    private Long durationInMonths;
    private BigDecimal totalCost;
    private BigDecimal percentageGrant;
    private BigDecimal fundingSought;
    private BigDecimal otherPublicSectorFunding;
    private BigDecimal contributionToProject;

    public FinanceCheckEligibilityResource() {

    }

    public FinanceCheckEligibilityResource(Long projectId, String projectName, Long organisationId, String organisationName, String applicationId, Long durationInMonths, BigDecimal totalCost, BigDecimal percentageGrant, BigDecimal fundingSought, BigDecimal otherPublicSectorFunding, BigDecimal contributionToProject) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.organisationId = organisationId;
        this.organisationName = organisationName;
        this.applicationId = applicationId;
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

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getOrganisationId() { return organisationId; }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
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

    public void setFundingSought(BigDecimal contributionToProject) {this.fundingSought = fundingSought; }
}
