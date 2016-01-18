package com.worth.ifs.finance.handler.item;

import com.worth.ifs.finance.resource.category.CostCategory;
import com.worth.ifs.finance.resource.cost.CostType;

import java.util.EnumMap;

public interface OrganisationFinanceHandler {
    EnumMap<CostType,CostCategory> getOrganisationFinances(Long applicationFinanceId);
    EnumMap<CostType,CostCategory> getOrganisationFinanceTotals(Long id);
}
