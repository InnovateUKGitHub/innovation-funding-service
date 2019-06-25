package org.innovateuk.ifs.finance.handler;

import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.handler.item.FinanceRowHandler;
import org.innovateuk.ifs.finance.resource.category.ChangedFinanceRowPair;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import java.util.List;
import java.util.Map;

/**
 * Action to retrieve the finances of the organisations
 */
public interface OrganisationFinanceHandler {
    Iterable<ApplicationFinanceRow> initialiseCostType(ApplicationFinance applicationFinance, FinanceRowType costType);
    Map<FinanceRowType,FinanceRowCostCategory> getOrganisationFinances(long applicationFinanceId);
    ApplicationFinanceRow costItemToCost(FinanceRowItem costItem);
    ProjectFinanceRow costItemToProjectCost(FinanceRowItem costItem);
    FinanceRowItem costToCostItem(FinanceRow cost);
    FinanceRowHandler getCostHandler(FinanceRowType costType);
    List<FinanceRowItem> costsToCostItems(List<? extends FinanceRow> costs);
    ApplicationFinanceRow updateCost(ApplicationFinanceRow financeRow);
    ApplicationFinanceRow addCost(Long applicationFinanceId, Long questionId, ApplicationFinanceRow financeRow);
    Map<FinanceRowType, FinanceRowCostCategory> getProjectOrganisationFinances(long projectFinanceId);
    Map<FinanceRowType, List<ChangedFinanceRowPair>> getProjectOrganisationFinanceChanges(Long projectFinanceId);
}
