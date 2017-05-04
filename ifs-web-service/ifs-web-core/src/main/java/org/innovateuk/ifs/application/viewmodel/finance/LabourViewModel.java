package org.innovateuk.ifs.application.viewmodel.finance;


import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.resource.FormInputType;

public class LabourViewModel extends AbstractCostViewModel {

    @Override
    protected FormInputType formInputType() {
        return FormInputType.LABOUR;
    }

    @Override
    public FinanceRowType rowType() {
        return FinanceRowType.LABOUR;
    }
}
