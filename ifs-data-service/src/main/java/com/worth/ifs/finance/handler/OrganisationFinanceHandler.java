package com.worth.ifs.finance.handler;

import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.resource.category.CostCategory;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.CostType;

import java.util.List;
import java.util.Map;

/**
 * Action to retrieve the finances of the organisations
 */
public interface OrganisationFinanceHandler {
    Iterable<Cost> initialiseCostType(ApplicationFinance applicationFinance, CostType costType);
    Map<CostType,CostCategory> getOrganisationFinances(Long applicationFinanceId);
    Map<CostType,CostCategory> getOrganisationFinanceTotals(Long id, Competition competition);
    Cost costItemToCost(CostItem costItem);
    CostItem costToCostItem(Cost cost);

    List<CostItem> costToCostItem(List<Cost> costs);

    List<Cost> costItemsToCost(List<CostItem> costItems);
}
