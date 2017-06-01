package org.innovateuk.ifs.application.viewmodel.finance;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.resource.FormInputType;

/**
 * View model for the capital usage form input.
 */
public class CapitalUsageCostViewModel extends AbstractCostViewModel {

    @Override
    protected FormInputType formInputType() {
        return FormInputType.CAPITAL_USAGE;
    }

    @Override
    public FinanceRowType getFinanceRowType() {
        return FinanceRowType.CAPITAL_USAGE;
    }
}
