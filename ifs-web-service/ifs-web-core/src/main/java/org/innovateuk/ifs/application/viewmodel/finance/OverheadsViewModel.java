package org.innovateuk.ifs.application.viewmodel.finance;


import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.resource.FormInputType;

public class OverheadsViewModel extends AbstractCostViewModel {

    @Override
    protected FormInputType formInputType() {
        return FormInputType.OVERHEADS;
    }

    @Override
    public FinanceRowType rowType() {
        return FinanceRowType.OVERHEADS;
    }
}
