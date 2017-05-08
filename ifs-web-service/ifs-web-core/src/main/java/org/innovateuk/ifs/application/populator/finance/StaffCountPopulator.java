package org.innovateuk.ifs.application.populator.finance;

import org.innovateuk.ifs.applicant.resource.AbstractApplicantResource;
import org.innovateuk.ifs.application.populator.forminput.AbstractFormInputPopulator;
import org.innovateuk.ifs.application.viewmodel.finance.StaffCountViewModel;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.stereotype.Component;

@Component
public class StaffCountPopulator extends AbstractFormInputPopulator<StaffCountViewModel> {

    @Override
    public FormInputType type() {
        return FormInputType.STAFF_COUNT;
    }

    @Override
    protected void populate(AbstractApplicantResource resource, StaffCountViewModel viewModel) {

    }

    @Override
    protected StaffCountViewModel createNew() {
        return new StaffCountViewModel();
    }
}
