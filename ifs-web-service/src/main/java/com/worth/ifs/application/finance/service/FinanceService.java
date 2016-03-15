package com.worth.ifs.application.finance.service;

import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.cost.CostItem;

import java.util.List;

/**
 * {@code FinanceService} handles the finances for each of the applications. These finances
 * consists of costs.
 */
public interface FinanceService {
    public ApplicationFinanceResource addApplicationFinance(Long userId, Long applicationId);
    public ApplicationFinanceResource getApplicationFinance(Long userId, Long applicationId);
    public ApplicationFinanceResource getApplicationFinanceDetails( Long userId, Long applicationId);
    public List<ApplicationFinanceResource> getApplicationFinanceTotals(Long applicationId);
    public CostItem addCost(Long applicationFinanceId , Long questionId);
    public List<CostItem> getCosts(Long applicationFinanceId);
}
