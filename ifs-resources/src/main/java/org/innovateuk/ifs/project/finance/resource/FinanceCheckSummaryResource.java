package org.innovateuk.ifs.project.finance.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.LOAN;

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
    private BigDecimal fundingAppliedFor;
    private BigDecimal otherPublicSectorFunding;
    private BigDecimal totalPercentageGrant;
    private boolean spendProfilesGenerated;
    private boolean bankDetailsApproved;
    private String spendProfileGeneratedBy;
    private LocalDate spendProfileGeneratedDate;
    private List<FinanceCheckPartnerStatusResource> partnerStatusResources;
    private BigDecimal researchParticipationPercentage;
    private BigDecimal competitionMaximumResearchPercentage;
    private Long applicationId;
    private boolean h2020;
    private FundingType fundingType;
    private boolean hasGrantClaimPercentage;

    public FinanceCheckSummaryResource() {
    }

    public FinanceCheckSummaryResource(FinanceCheckOverviewResource overviewResource, Long competitionId, String competitionName, boolean spendProfilesGenerated,
                                       List<FinanceCheckPartnerStatusResource> partnerStatusResources, boolean bankDetailsApproved,
                                       String spendProfileGeneratedBy, LocalDate spendProfileGeneratedDate, Long applicationId, boolean h2020, FundingType fundingType, boolean hasGrantClaimPercentage) {
        this.projectId = overviewResource.getProjectId();
        this.projectName = overviewResource.getProjectName();
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.partnerStatusResources = partnerStatusResources;
        this.projectStartDate = overviewResource.getProjectStartDate();
        this.durationInMonths = overviewResource.getDurationInMonths();
        this.totalProjectCost = overviewResource.getTotalProjectCost();
        this.grantAppliedFor = overviewResource.getGrantAppliedFor();
        this.fundingAppliedFor = overviewResource.getFundingAppliedFor();
        this.otherPublicSectorFunding = overviewResource.getOtherPublicSectorFunding();
        this.totalPercentageGrant = overviewResource.getTotalPercentageGrant();
        this.spendProfilesGenerated = spendProfilesGenerated;
        this.bankDetailsApproved = bankDetailsApproved;
        this.spendProfileGeneratedBy = spendProfileGeneratedBy;
        this.spendProfileGeneratedDate = spendProfileGeneratedDate;
        this.researchParticipationPercentage = overviewResource.getResearchParticipationPercentage();
        this.competitionMaximumResearchPercentage = overviewResource.getCompetitionMaximumResearchPercentage();
        this.applicationId = applicationId;
        this.h2020 = h2020;
        this.fundingType = fundingType;
        this.hasGrantClaimPercentage = hasGrantClaimPercentage;
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

    public boolean isH2020() {
        return h2020;
    }

    public void setH2020(boolean h2020) {
        this.h2020 = h2020;
    }

    public FundingType getFundingType() {
        return fundingType;
    }

    public void setFundingType(FundingType fundingType) {
        this.fundingType = fundingType;
    }

    public boolean isHasGrantClaimPercentage() {
        return hasGrantClaimPercentage;
    }

    public void setHasGrantClaimPercentage(boolean hasGrantClaimPercentage) {
        this.hasGrantClaimPercentage = hasGrantClaimPercentage;
    }

    @JsonIgnore
    public boolean isFinanceChecksAllApproved() {
        return isViabilityAllApprovedOrNotRequired() && isEligibilityAllApprovedOrNotRequired();
    }

    private boolean isViabilityAllApprovedOrNotRequired() {

        List<ViabilityState> relevantStatuses = asList(
                ViabilityState.APPROVED,
                ViabilityState.NOT_APPLICABLE);

        return partnerStatusResources.stream().allMatch(org -> relevantStatuses.contains(org.getViability()));
    }

    private boolean isEligibilityAllApprovedOrNotRequired() {

        List<EligibilityState> relevantStatuses = asList(
                EligibilityState.APPROVED,
                EligibilityState.NOT_APPLICABLE);

        return partnerStatusResources.stream().allMatch(org -> relevantStatuses.contains(org.getEligibility()));
    }

    public boolean isAllEligibilityAndViabilityInReview() {
        return partnerStatusResources
                .stream()
                .allMatch(partner ->
                        partner.getViability().isInReviewOrNotApplicable() && partner.getEligibility().isInReviewOrNotApplicable());
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

    public Long getApplicationId() { return applicationId; }

    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    public BigDecimal getFundingAppliedFor() {
        return fundingAppliedFor;
    }

    public void setFundingAppliedFor(BigDecimal fundingAppliedFor) {
        this.fundingAppliedFor = fundingAppliedFor;
    }

    @JsonIgnore
    public boolean isLoan() {
        return fundingType == LOAN;
    }
}
