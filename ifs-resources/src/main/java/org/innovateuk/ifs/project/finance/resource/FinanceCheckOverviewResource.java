package org.innovateuk.ifs.project.finance.resource;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FinanceCheckOverviewResource {
    private Long projectId;
    private String projectName;
    private LocalDate projectStartDate;
    private int durationInMonths;
    private BigDecimal totalProjectCost;
    private BigDecimal grantAppliedFor;
    private BigDecimal otherPublicSectorFunding;
    private BigDecimal totalPercentageGrant;


    public FinanceCheckOverviewResource() {
    }

    public FinanceCheckOverviewResource(Long projectId, String projectName,LocalDate projectStartDate, int durationInMonths, BigDecimal totalProjectCost, BigDecimal grantAppliedFor, BigDecimal otherPublicSectorFunding, BigDecimal totalPercentageGrant) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.projectStartDate = projectStartDate;
        this.durationInMonths = durationInMonths;
        this.totalProjectCost = totalProjectCost;
        this.grantAppliedFor = grantAppliedFor;
        this.otherPublicSectorFunding = otherPublicSectorFunding;
        this.totalPercentageGrant = totalPercentageGrant;
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

    public LocalDate getProjectStartDate() {
        return projectStartDate;
    }

    public void setProjectStartDate(LocalDate projectStartDate) {
        this.projectStartDate = projectStartDate;
    }

    public int getDurationInMonths() {
        return durationInMonths;
    }

    public void setDurationInMonths(int durationInMonths) {
        this.durationInMonths = durationInMonths;
    }

    public BigDecimal getTotalProjectCost() {
        return totalProjectCost;
    }

    public void setTotalProjectCost(BigDecimal totalProjectCost) {
        this.totalProjectCost = totalProjectCost;
    }

    public BigDecimal getGrantAppliedFor() {
        return grantAppliedFor;
    }

    public void setGrantAppliedFor(BigDecimal grantAppliedFor) {
        this.grantAppliedFor = grantAppliedFor;
    }

    public BigDecimal getOtherPublicSectorFunding() {
        return otherPublicSectorFunding;
    }

    public void setOtherPublicSectorFunding(BigDecimal otherPublicSectorFunding) {
        this.otherPublicSectorFunding = otherPublicSectorFunding;
    }

    public BigDecimal getTotalPercentageGrant() {
        return totalPercentageGrant;
    }

    public void setTotalPercentageGrant(BigDecimal totalPercentageGrant) {
        this.totalPercentageGrant = totalPercentageGrant;
    }
}
