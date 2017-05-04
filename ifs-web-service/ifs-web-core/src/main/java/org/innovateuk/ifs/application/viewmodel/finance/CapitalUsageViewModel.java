package org.innovateuk.ifs.application.viewmodel.finance;


import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.resource.FormInputType;

public class CapitalUsageViewModel extends AbstractCostViewModel {

    @Override
    protected FormInputType formInputType() {
        return FormInputType.CAPITAL_USAGE;
    }

    @Override
    public FinanceRowType rowType() {
        return FinanceRowType.CAPITAL_USAGE;
    }
}
