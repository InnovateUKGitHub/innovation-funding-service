package org.innovateuk.ifs.application.populator.finance;

import org.innovateuk.ifs.applicant.resource.AbstractApplicantResource;
import org.innovateuk.ifs.application.populator.forminput.AbstractFormInputPopulator;
import org.innovateuk.ifs.application.viewmodel.finance.FinancialStaffCountViewModel;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.stereotype.Component;

@Component
public class FinancialStaffCountPopulator extends AbstractFormInputPopulator<FinancialStaffCountViewModel> {

    @Override
    public FormInputType type() {
        return FormInputType.FINANCIAL_STAFF_COUNT;
    }

    @Override
    protected void populate(AbstractApplicantResource resource, FinancialStaffCountViewModel viewModel) {

    }

    @Override
    protected FinancialStaffCountViewModel createNew() {
        return new FinancialStaffCountViewModel();
    }
}
