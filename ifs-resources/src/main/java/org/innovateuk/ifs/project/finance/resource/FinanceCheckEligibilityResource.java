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
    private int durationInMonths;
    private BigDecimal totalCost;
    private BigDecimal percentageGrant;
    private BigDecimal otherPublicSectorFunding;

    public FinanceCheckEligibilityResource() {

    }

    public FinanceCheckEligibilityResource(Long projectId, String projectName, Long organisationId, String organisationName, int durationInMonths, BigDecimal totalCost, BigDecimal otherPublicSectorFunding, BigDecimal percentageGrant) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.organisationId = organisationId;
        this.organisationName = organisationName;
        this.durationInMonths = durationInMonths;
        this.totalCost = totalCost;
        this.otherPublicSectorFunding = otherPublicSectorFunding;
        this.percentageGrant = percentageGrant;
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

    public Long getOrgansiationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public int getDurationInMonths() {
        return durationInMonths;
    }

    public void setDurationInMonths(int durationInMonths) {
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
}
