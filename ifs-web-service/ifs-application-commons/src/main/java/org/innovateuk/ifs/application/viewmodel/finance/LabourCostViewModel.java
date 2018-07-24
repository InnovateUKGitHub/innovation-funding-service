package org.innovateuk.ifs.application.viewmodel.finance;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.resource.FormInputType;

/**
 * View model for labour costs form input.
 */
public class LabourCostViewModel extends AbstractCostViewModel {

    @Override
    protected FormInputType formInputType() {
        return FormInputType.LABOUR;
    }

    @Override
    public FinanceRowType getFinanceRowType() {
        return FinanceRowType.LABOUR;
    }
}
