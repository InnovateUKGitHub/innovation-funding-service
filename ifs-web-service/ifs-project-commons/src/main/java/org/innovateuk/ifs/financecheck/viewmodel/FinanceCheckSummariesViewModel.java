package org.innovateuk.ifs.financecheck.viewmodel;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;

import java.math.BigDecimal;
import java.util.List;

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

    public List<FinanceCheckEligibilityResource> getFinanceCheckSummariesResources() {
        return financeCheckSummariesResources;
    }

    public List<PartnerOrganisationResource> getOrganisationResources() {
        return organisationResources;
    }

    public BigDecimal getTotalCost() {
        return financeCheckSummariesResources.stream().map(FinanceCheckEligibilityResource::getTotalCost).reduce(BigDecimal.ZERO, BigDecimal::add);
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

    public PartnerOrganisationResource getPartnerFromSummary(Long organisationId) {
        return getOrganisationResources().stream().filter(org -> organisationId.equals(org.getOrganisation())).findFirst().orElse(new PartnerOrganisationResource());
    }
}
