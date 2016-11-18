package com.worth.ifs.project.finance.resource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * A resource object to return finance check status for a project (for all partner organisations).
 */
public class FinanceCheckSummaryResource {
    private Long projectId;
    private Long competitionId;
    private String competitionName;
    private LocalDate projectStartDate;
    private int durationInMonths;
    private BigDecimal totalProjectCost;
    private BigDecimal grantAppliedFor;
    private BigDecimal otherPublicSectorFunding;
    private BigDecimal totalPercentageGrant;
    private boolean spendProfilesGenerated;
    private boolean financeChecksAllApproved;
    private String spendProfileGeneratedBy;
    private LocalDate spendProfileGeneratedDate;
    private List<FinanceCheckPartnerStatusResource> partnerStatusResources;

    public FinanceCheckSummaryResource() {
    }

    public FinanceCheckSummaryResource(Long projectId, Long competitionId, String competitionName, LocalDate projectStartDate, int durationInMonths, BigDecimal totalProjectCost, BigDecimal grantAppliedFor, BigDecimal otherPublicSectorFunding, BigDecimal totalPercentageGrant, boolean spendProfilesGenerated, List<FinanceCheckPartnerStatusResource> partnerStatusResources, boolean financeChecksAllApproved, String spendProfileGeneratedBy, LocalDate spendProfileGeneratedDate) {
        this.projectId = projectId;
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.partnerStatusResources = partnerStatusResources;
        this.projectStartDate = projectStartDate;
        this.durationInMonths = durationInMonths;
        this.totalProjectCost = totalProjectCost;
        this.grantAppliedFor = grantAppliedFor;
        this.otherPublicSectorFunding = otherPublicSectorFunding;
        this.totalPercentageGrant = totalPercentageGrant;
        this.spendProfilesGenerated = spendProfilesGenerated;
        this.financeChecksAllApproved = financeChecksAllApproved;
        this.spendProfileGeneratedBy = spendProfileGeneratedBy;
        this.spendProfileGeneratedDate = spendProfileGeneratedDate;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public List<FinanceCheckPartnerStatusResource> getPartnerStatusResources() {
        return partnerStatusResources;
    }

    public void setPartnerStatusResources(List<FinanceCheckPartnerStatusResource> partnerStatusResources) {
        this.partnerStatusResources = partnerStatusResources;
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

    public boolean isSpendProfilesGenerated() {
        return spendProfilesGenerated;
    }

    public void setSpendProfilesGenerated(boolean spendProfilesGenerated) {
        this.spendProfilesGenerated = spendProfilesGenerated;
    }

    public String getSpendProfileGeneratedBy() {
        return spendProfileGeneratedBy;
    }

    public LocalDate getSpendProfileGeneratedDate() {
        return spendProfileGeneratedDate;
    }

    public boolean isFinanceChecksAllApproved() {
        return financeChecksAllApproved;
    }

    public void setFinanceChecksAllApproved(boolean financeChecksAllApproved) {
        this.financeChecksAllApproved = financeChecksAllApproved;
    }

    public void setSpendProfileGeneratedBy(String spendProfileGeneratedBy) {
        this.spendProfileGeneratedBy = spendProfileGeneratedBy;
    }

    public void setSpendProfileGeneratedDate(LocalDate spendProfileGeneratedDate) {
        this.spendProfileGeneratedDate = spendProfileGeneratedDate;
    }
}
