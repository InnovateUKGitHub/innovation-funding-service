package org.innovateuk.ifs.project.financecheck.viewmodel;

import org.innovateuk.ifs.project.finance.resource.FinanceCheckSummariesResource;

import java.math.BigDecimal;
import java.util.List;

/**
 * View model for the finance checks summaries table.
 */
public class FinanceCheckSummariesViewModel {
    private List<FinanceCheckSummariesResource> financeCheckSummariesResources;
    private BigDecimal totalCost;
    private BigDecimal totalFundingSought;
    private BigDecimal totalOtherPublicSectorFunding;
    private BigDecimal totalContributionToProject;

    public FinanceCheckSummariesViewModel() {}

    public FinanceCheckSummariesViewModel(List<FinanceCheckSummariesResource> financeCheckSummariesResources) {
        this.financeCheckSummariesResources = financeCheckSummariesResources;
        this.totalCost = financeCheckSummariesResources.stream().map(of -> of.getFinanceCheckEligibilityResource().getTotalCost()).reduce(BigDecimal.ZERO, BigDecimal::add);
        this.totalFundingSought = financeCheckSummariesResources.stream().map(of -> of.getFinanceCheckEligibilityResource().getFundingSought()).reduce(BigDecimal.ZERO, BigDecimal::add);
        this.totalOtherPublicSectorFunding = financeCheckSummariesResources.stream().map(of -> of.getFinanceCheckEligibilityResource().getOtherPublicSectorFunding()).reduce(BigDecimal.ZERO, BigDecimal::add);
        this.totalContributionToProject = financeCheckSummariesResources.stream().map(of -> of.getFinanceCheckEligibilityResource().getContributionToProject()).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<FinanceCheckSummariesResource> getFinanceCheckSummariesResources() {
        return financeCheckSummariesResources;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public BigDecimal getTotalFundingSought() {
        return totalFundingSought;
    }

    public BigDecimal getTotalOtherPublicSectorFunding() {
        return totalOtherPublicSectorFunding;
    }

    public BigDecimal getTotalContributionToProject() {
        return totalContributionToProject;
    }
}
