package org.innovateuk.ifs.application.viewmodel.finance;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.resource.FormInputType;

/**
 * View model for other funding costs form input.
 */
public class OtherFundingCostViewModel extends AbstractCostViewModel {

    @Override
    protected FormInputType formInputType() {
        return FormInputType.OTHER_FUNDING;
    }

    @Override
    public FinanceRowType getFinanceRowType() {
        return FinanceRowType.OTHER_FUNDING;
    }
}
