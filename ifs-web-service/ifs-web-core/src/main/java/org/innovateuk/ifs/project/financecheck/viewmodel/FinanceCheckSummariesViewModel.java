package org.innovateuk.ifs.project.financecheck.viewmodel;

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

    public FinanceCheckSummariesViewModel() {}

    public FinanceCheckSummariesViewModel(List<FinanceCheckEligibilityResource> financeCheckSummariesResources, List<PartnerOrganisationResource> partnerOrganisationResources) {
        this.financeCheckSummariesResources = financeCheckSummariesResources;
        this.organisationResources = partnerOrganisationResources;
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
