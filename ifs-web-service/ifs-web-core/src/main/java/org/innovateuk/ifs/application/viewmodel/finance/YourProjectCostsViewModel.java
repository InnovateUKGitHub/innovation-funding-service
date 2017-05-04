package org.innovateuk.ifs.application.viewmodel.finance;


import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.resource.FormInputType;

import java.util.List;

public class YourProjectCostsViewModel extends AbstractCostViewModel {

    //userOrganisation
    //organisationFinanceTotal
    private List<AbstractFormInputViewModel> formInputViewModels;

    @Override
    protected FormInputType formInputType() {
        return FormInputType.YOUR_FINANCE;
    }

    @Override
    public FinanceRowType rowType() {
        return FinanceRowType.YOUR_FINANCE;
    }
}
