package org.innovateuk.ifs.application.viewmodel.finance;


import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.resource.FormInputType;

public class MaterialsViewModel extends AbstractCostViewModel {

    @Override
    protected FormInputType formInputType() {
        return FormInputType.MATERIALS;
    }

    @Override
    public FinanceRowType rowType() {
        return FinanceRowType.MATERIALS;
    }
}
