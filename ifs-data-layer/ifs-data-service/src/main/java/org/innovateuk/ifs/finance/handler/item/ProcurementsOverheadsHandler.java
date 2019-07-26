package org.innovateuk.ifs.finance.handler.item;

import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.domain.FinanceRow;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.ProcurementOverhead;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import javax.validation.constraints.NotNull;

/**
 * Handles the Procurement overhead costs, i.e. converts the costs to be stored into the database
 * or for sending it over.
 */
@Component
public class ProcurementsOverheadsHandler extends FinanceRowHandler<ProcurementOverhead> {
    public static final String COST_KEY = "procurement-overheads";

    @Override
    public void validate(@NotNull ProcurementOverhead procurementOverhead, @NotNull BindingResult bindingResult) {
        super.validate(procurementOverhead, bindingResult);
    }

    @Override
    public ApplicationFinanceRow toCost(ProcurementOverhead procurementOverhead) {
        return new ApplicationFinanceRow(procurementOverhead.getId(), COST_KEY, procurementOverhead.getItem(), "", procurementOverhead.getCompanyCost(), procurementOverhead.getProjectCost(),null, null);
    }

    @Override
    public ProjectFinanceRow toProjectCost(ProcurementOverhead procurementOverhead) {
        return new ProjectFinanceRow(procurementOverhead.getId(), COST_KEY, procurementOverhead.getItem(), "", procurementOverhead.getCompanyCost(), procurementOverhead.getProjectCost(),null, null);
    }

    @Override
    public FinanceRowItem toCostItem(FinanceRow cost) {
        return buildRowItem(cost);
    }

    private FinanceRowItem buildRowItem(FinanceRow cost){
        return new ProcurementOverhead(cost.getId(),cost.getItem(), cost.getQuantity(), cost.getCost());
    }
}
