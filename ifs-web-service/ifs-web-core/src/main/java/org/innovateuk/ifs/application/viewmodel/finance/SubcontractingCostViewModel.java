package org.innovateuk.ifs.application.viewmodel.finance;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.resource.FormInputType;

/**
 * Created by luke.harper on 08/05/2017.
 */
public class SubcontractingCostViewModel extends AbstractCostViewModel {

    @Override
    protected FormInputType formInputType() {
        return FormInputType.SUBCONTRACTING;
    }

    @Override
    public FinanceRowType getFinanceRowType() {
        return FinanceRowType.SUBCONTRACTING_COSTS;
    }
}
