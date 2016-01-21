package com.worth.ifs.application.finance.service;

import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.cost.CostItem;

import java.util.List;

/**
 * {@code FinanceService} handles the finances for each of the applications. These finances
 * consists of costs.
 */
public interface FinanceService {
    public ApplicationFinanceResource addApplicationFinance(Long applicationId, Long userId);
    public ApplicationFinanceResource getApplicationFinance(Long applicationId, Long userId);
    public ApplicationFinanceResource getApplicationFinanceDetails(Long applicationId, Long userId);
    public List<ApplicationFinanceResource> getApplicationFinanceTotals(Long applicationId);
    public void addCost(Long applicationFinanceId , Long questionId);
    public List<CostItem> getCosts(Long applicationFinanceId);
}
