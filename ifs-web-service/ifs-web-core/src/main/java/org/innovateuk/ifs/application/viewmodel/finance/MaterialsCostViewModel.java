package org.innovateuk.ifs.application.viewmodel.finance;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.resource.FormInputType;

/**
 * View model for materials costs form input.
 */
public class MaterialsCostViewModel extends AbstractCostViewModel {

    @Override
    protected FormInputType formInputType() {
        return FormInputType.MATERIALS;
    }

    @Override
    public FinanceRowType getFinanceRowType() {
        return FinanceRowType.MATERIALS;
    }
}
