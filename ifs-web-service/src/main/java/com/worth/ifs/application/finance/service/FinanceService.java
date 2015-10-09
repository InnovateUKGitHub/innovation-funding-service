package com.worth.ifs.application.finance.service;

import com.worth.ifs.application.finance.CostCategory;
import com.worth.ifs.application.finance.CostType;
import com.worth.ifs.application.finance.model.OrganisationFinance;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;

import java.util.EnumMap;
import java.util.List;

/**
 * {@code FinanceService} handles the finances for each of the applications. These finances
 * consists of costs.
 */
public interface FinanceService {
    public ApplicationFinance addApplicationFinance(Long applicationId, Long userId);
    public ApplicationFinance getApplicationFinance(Long applicationId, Long userId);
    public List<ApplicationFinance> getApplicationFinances(Long applicationId);
    public void addCost(Long applicationFinanceId , Long questionId);
    public List<Cost> getCosts(Long applicationFinanceId);
}
