package org.innovateuk.ifs.application.viewmodel.finance;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.resource.FormInputType;

/**
 * Created by luke.harper on 08/05/2017.
 */
public class OtherCostViewModel extends AbstractCostViewModel {

    @Override
    protected FormInputType formInputType() {
        return FormInputType.OTHER_COSTS;
    }

    @Override
    public FinanceRowType getFinanceRowType() {
        return FinanceRowType.OTHER_COSTS;
    }
}
