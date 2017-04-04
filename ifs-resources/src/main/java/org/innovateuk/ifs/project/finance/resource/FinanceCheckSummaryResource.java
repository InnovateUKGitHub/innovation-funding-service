package org.innovateuk.ifs.project.finance.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * A resource object to return finance check status for a project (for all partner organisations).
 */
public class FinanceCheckSummaryResource {
    private Long projectId;
    private String projectName;
    private Long competitionId;
    private String competitionName;
    private LocalDate projectStartDate;
    private int durationInMonths;
    private BigDecimal totalProjectCost;
    private BigDecimal grantAppliedFor;
    private BigDecimal otherPublicSectorFunding;
    private BigDecimal totalPercentageGrant;
    private boolean spendProfilesGenerated;
    private boolean bankDetailsApproved;
    private String spendProfileGeneratedBy;
    private LocalDate spendProfileGeneratedDate;
    private List<FinanceCheckPartnerStatusResource> partnerStatusResources;
    private BigDecimal researchParticipationPercentage;
    private BigDecimal competitionMaximumResearchPercentage;

    public FinanceCheckSummaryResource() {
    }

    public FinanceCheckSummaryResource(FinanceCheckOverviewResource overviewResource, Long competitionId, String competitionName, boolean spendProfilesGenerated,
                                       List<FinanceCheckPartnerStatusResource> partnerStatusResources, boolean bankDetailsApproved,
                                       String spendProfileGeneratedBy, LocalDate spendProfileGeneratedDate) {
        this.projectId = overviewResource.getProjectId();
        this.projectName = overviewResource.getProjectName();
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.partnerStatusResources = partnerStatusResources;
        this.projectStartDate = overviewResource.getProjectStartDate();
        this.durationInMonths = overviewResource.getDurationInMonths();
        this.totalProjectCost = overviewResource.getTotalProjectCost();
        this.grantAppliedFor = overviewResource.getGrantAppliedFor();
        this.otherPublicSectorFunding = overviewResource.getOtherPublicSectorFunding();
        this.totalPercentageGrant = overviewResource.getTotalPercentageGrant();
        this.spendProfilesGenerated = spendProfilesGenerated;
        this.bankDetailsApproved = bankDetailsApproved;
        this.spendProfileGeneratedBy = spendProfileGeneratedBy;
        this.spendProfileGeneratedDate = spendProfileGeneratedDate;
        this.researchParticipationPercentage = overviewResource.getResearchParticipationPercentage();
        this.competitionMaximumResearchPercentage = overviewResource.getCompetitionMaximumResearchPercentage();
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

    @JsonIgnore
    public boolean isFinanceChecksAllApproved() {
        return isViabilityAllApprovedOrNotRequired() && isEligibilityAllApprovedOrNotRequired();
    }

    private boolean isViabilityAllApprovedOrNotRequired() {

        List<Viability> relevantStatuses = asList(
                Viability.APPROVED,
                Viability.NOT_APPLICABLE);

        return partnerStatusResources.stream().allMatch(org -> relevantStatuses.contains(org.getViability()));
    }

    private boolean isEligibilityAllApprovedOrNotRequired() {

        List<Eligibility> relevantStatuses = asList(
                Eligibility.APPROVED,
                Eligibility.NOT_APPLICABLE);

        return partnerStatusResources.stream().allMatch(org -> relevantStatuses.contains(org.getEligibility()));
    }

    public void setSpendProfileGeneratedBy(String spendProfileGeneratedBy) {
        this.spendProfileGeneratedBy = spendProfileGeneratedBy;
    }

    public void setSpendProfileGeneratedDate(LocalDate spendProfileGeneratedDate) {
        this.spendProfileGeneratedDate = spendProfileGeneratedDate;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public BigDecimal getResearchParticipationPercentage() {
        return researchParticipationPercentage;
    }

    public void setResearchParticipationPercentage(BigDecimal researchParticipationPercentage) {
        this.researchParticipationPercentage = researchParticipationPercentage;
    }

    public BigDecimal getCompetitionMaximumResearchPercentage() {
        return competitionMaximumResearchPercentage;
    }

    public void setCompetitionMaximumResearchPercentage(BigDecimal competitionMaximumResearchPercentage) {
        this.competitionMaximumResearchPercentage = competitionMaximumResearchPercentage;
    }

    public boolean isBankDetailsApproved() {
        return bankDetailsApproved;
    }

    public void setBankDetailsApproved(boolean bankDetailsApproved) {
        this.bankDetailsApproved = bankDetailsApproved;
    }
}
