package com.worth.ifs.finance.handler.item;

import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.finance.resource.category.CostCategory;
import com.worth.ifs.finance.resource.cost.CostItem;
import com.worth.ifs.finance.resource.cost.CostType;

import java.util.EnumMap;

/**
 * Action to retrieve the finances of the organisations
 */
public interface OrganisationFinanceHandler {
    void initialiseCostType(ApplicationFinance applicationFinance, CostType costType);
    EnumMap<CostType,CostCategory> getOrganisationFinances(Long applicationFinanceId);
    EnumMap<CostType,CostCategory> getOrganisationFinanceTotals(Long id);
    Cost costItemToCost(CostItem costItem);
    CostItem costToCostItem(Cost cost);
}
