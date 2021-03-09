package org.innovateuk.ifs.application.finance.viewmodel;

import java.util.List;

public class ProjectFinanceChangesFinanceSummaryViewModel {
    private CostChangeViewModel totalCosts;
    private FundingRulesChangeViewModel fundingRules;
    private List<CostChangeViewModel> entries;

    public ProjectFinanceChangesFinanceSummaryViewModel(CostChangeViewModel totalCosts, FundingRulesChangeViewModel fundingRules, List<CostChangeViewModel> entries) {
        this.totalCosts = totalCosts;
        this.fundingRules = fundingRules;
        this.entries = entries;
    }

    public CostChangeViewModel getTotalCosts() {
        return totalCosts;
    }

    public FundingRulesChangeViewModel getFundingRules() {
        return fundingRules;
    }

    public List<CostChangeViewModel> getEntries() {
        return entries;
    }

    public boolean hasChanges() {
        if (totalCosts != null && totalCosts.isProjectCostDifferent()) {
            return true;
        }
        if (fundingRules != null && fundingRules.isRulesDifferent()) {
            return true;
        }
        return entries.stream().anyMatch(entry -> entry.isProjectCostDifferent());
    }
}
