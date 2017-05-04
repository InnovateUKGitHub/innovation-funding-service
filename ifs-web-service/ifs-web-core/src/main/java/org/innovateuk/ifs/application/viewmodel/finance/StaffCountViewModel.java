package org.innovateuk.ifs.application.viewmodel.finance;


import org.innovateuk.ifs.application.viewmodel.forminput.AbstractFormInputViewModel;
import org.innovateuk.ifs.form.resource.FormInputType;

public class StaffCountViewModel extends AbstractFormInputViewModel {

    @Override
    protected FormInputType formInputType() {
        return FormInputType.STAFF_COUNT;
    }

}
