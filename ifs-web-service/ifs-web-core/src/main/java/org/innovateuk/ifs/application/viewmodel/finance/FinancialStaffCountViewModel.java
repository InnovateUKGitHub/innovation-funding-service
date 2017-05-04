package org.innovateuk.ifs.application.viewmodel.finance;


import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;
import org.innovateuk.ifs.form.resource.FormInputType;

public class FinancialStaffCountViewModel extends AbstractFormInputViewModel {

    @Override
    protected FormInputType formInputType() {
        return FormInputType.FINANCIAL_STAFF_COUNT;
    }

}
