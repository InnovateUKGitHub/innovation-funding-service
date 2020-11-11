package org.innovateuk.ifs.financecheck.viewmodel;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * View model for the finance checks summaries table.
 */
public class FinanceCheckSummariesViewModel {
    private List<FinanceCheckEligibilityResource> financeCheckSummariesResources;
    private List<PartnerOrganisationResource> organisationResources;
    private FundingType fundingType;

    public FinanceCheckSummariesViewModel() {}

    public FinanceCheckSummariesViewModel(List<FinanceCheckEligibilityResource> financeCheckSummariesResources, List<PartnerOrganisationResource> organisationResources, FundingType fundingType) {
        this.financeCheckSummariesResources = financeCheckSummariesResources;
        this.organisationResources = organisationResources;
        this.fundingType = fundingType;
    }

    public FundingType getFundingType() {
        return fundingType;
    }

    public List<FinanceCheckSummaryEntryViewModel> getFinanceCheckSummariesResources() {
        if (FundingType.KTP == fundingType) {

            Optional<PartnerOrganisationResource> leadOrg = organisationResources.stream()
                    .filter(PartnerOrganisationResource::isLeadOrganisation)
                    .findFirst();

            Optional<FinanceCheckEligibilityResource> leadSummary = financeCheckSummariesResources.stream()
                    .filter(summary -> isLead(summary, leadOrg.get()))
                    .findFirst();

            if (!leadOrg.isPresent() || !leadSummary.isPresent()) {
                return map(financeCheckSummariesResources);
            }

            return financeCheckSummariesResources.stream().map(summary -> {
                boolean isLead = isLead(summary, leadOrg.get());

                BigDecimal contributionToProject;
                BigDecimal percentageContributionToProject;
                if (isLead) {
                    contributionToProject = new BigDecimal("0.00");
                    percentageContributionToProject = new BigDecimal("0.0");
                } else {
                    contributionToProject = leadSummary.get().getContributionToProject();
                    percentageContributionToProject = getPercentageContributionToProject(leadSummary.get());
                }

                return new FinanceCheckSummaryEntryViewModel(
                        summary.getProjectId(),
                        summary.getOrganisationId(),
                        summary.getDurationInMonths(),
                        summary.getTotalCost(),
                        summary.getPercentageGrant(),
                        summary.getFundingSought(),
                        summary.getOtherPublicSectorFunding(),
                        contributionToProject,
                        percentageContributionToProject,
                        summary.isHasApplicationFinances());
            }).collect(Collectors.toList());
        }
        return map(financeCheckSummariesResources);
    }

    private List<FinanceCheckSummaryEntryViewModel> map(List<FinanceCheckEligibilityResource> resources) {
        return resources.stream().map(summary -> new FinanceCheckSummaryEntryViewModel(
                summary.getProjectId(),
                summary.getOrganisationId(),
                summary.getDurationInMonths(),
                summary.getTotalCost(),
                summary.getPercentageGrant(),
                summary.getFundingSought(),
                summary.getOtherPublicSectorFunding(),
                summary.getContributionToProject(),
                getPercentageContributionToProject(summary),
                summary.isHasApplicationFinances())).collect(Collectors.toList());
    }

    private boolean isLead(FinanceCheckEligibilityResource resource, PartnerOrganisationResource lead) {
        return resource.getOrganisationId().equals(lead.getOrganisation());
    }

    public List<PartnerOrganisationResource> getOrganisationResources() {
        return organisationResources;
    }

    public BigDecimal getTotalCost() {
        return financeCheckSummariesResources.stream().map(FinanceCheckEligibilityResource::getTotalCost).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalPercentageGrant() {
        return financeCheckSummariesResources.stream().map(FinanceCheckEligibilityResource::getPercentageGrant).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalFundingSought() {
        return financeCheckSummariesResources.stream().map(FinanceCheckEligibilityResource::getFundingSought).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalOtherPublicSectorFunding() {
        return financeCheckSummariesResources.stream().map(FinanceCheckEligibilityResource::getOtherPublicSectorFunding).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalContributionToProject() {
        return financeCheckSummariesResources.stream().map(FinanceCheckEligibilityResource::getContributionToProject).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getPercentageTotalContributionToProject() {
        BigDecimal totalContribution = getTotalContributionToProject();
        BigDecimal totalCost = getTotalCost();
        if (totalContribution.signum() == 0 || totalCost.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return totalContribution.multiply(new BigDecimal(100)).divide(totalCost, 1, RoundingMode.HALF_UP);
    }

    public PartnerOrganisationResource getPartnerFromSummary(Long organisationId) {
        return getOrganisationResources().stream().filter(org -> organisationId.equals(org.getOrganisation())).findFirst().orElse(new PartnerOrganisationResource());
    }

    private BigDecimal getPercentageContributionToProject(FinanceCheckEligibilityResource resource) {
        if (resource.getTotalCost().signum() == 0 || resource.getContributionToProject().signum() == 0) {
            return BigDecimal.ZERO;
        }
        return resource.getContributionToProject().multiply(new BigDecimal(100)).divide(resource.getTotalCost(), 1, RoundingMode.HALF_UP);
    }

}
