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
    //actions
    Iterable<ApplicationFinanceRow> initialiseCostType(ApplicationFinance applicationFinance, FinanceRowType costType);
    ApplicationFinanceRow updateCost(ApplicationFinanceRow financeRow);
    ApplicationFinanceRow addCost(ApplicationFinanceRow financeRow);

    //mapping
    ApplicationFinanceRow toApplicationDomain(FinanceRowItem costItem);
    ProjectFinanceRow toProjectDomain(FinanceRowItem costItem);
    FinanceRowItem toResource(FinanceRow cost);
    List<FinanceRowItem> toResources(List<? extends FinanceRow> costs);

    //getting
    FinanceRowHandler getCostHandler(FinanceRowType costType);
    Map<FinanceRowType,FinanceRowCostCategory> getOrganisationFinances(long applicationFinanceId);
    Map<FinanceRowType, FinanceRowCostCategory> getProjectOrganisationFinances(long projectFinanceId);
    Map<FinanceRowType, List<ChangedFinanceRowPair>> getProjectOrganisationFinanceChanges(Long projectFinanceId);
}
