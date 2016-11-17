package com.worth.ifs.finance.handler;

import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.ApplicationFinanceRow;
import com.worth.ifs.finance.handler.item.FinanceRowHandler;
import com.worth.ifs.finance.resource.category.FinanceRowCostCategory;
import com.worth.ifs.finance.resource.cost.FinanceRowItem;
import com.worth.ifs.finance.resource.cost.FinanceRowType;

import java.util.List;
import java.util.Map;

/**
 * Action to retrieve the finances of the organisations
 */
public interface OrganisationFinanceHandler {
    Iterable<ApplicationFinanceRow> initialiseCostType(ApplicationFinance applicationFinance, FinanceRowType costType);
    Map<FinanceRowType,FinanceRowCostCategory> getOrganisationFinances(Long applicationFinanceId);
    Map<FinanceRowType,FinanceRowCostCategory> getOrganisationFinanceTotals(Long id, Competition competition);
    ApplicationFinanceRow costItemToCost(FinanceRowItem costItem);
    FinanceRowItem costToCostItem(ApplicationFinanceRow cost);
    FinanceRowHandler getCostHandler(FinanceRowType costType);
    List<FinanceRowItem> costToCostItem(List<ApplicationFinanceRow> costs);

    List<ApplicationFinanceRow> costItemsToCost(List<FinanceRowItem> costItems);
}
