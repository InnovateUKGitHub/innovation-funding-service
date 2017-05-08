package org.innovateuk.ifs.application.populator.finance;

import org.innovateuk.ifs.applicant.resource.AbstractApplicantResource;
import org.innovateuk.ifs.application.populator.forminput.AbstractFormInputPopulator;
import org.innovateuk.ifs.application.viewmodel.finance.FinancialYearEndViewModel;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.stereotype.Component;

@Component
public class FinancialYearEndPopulator extends AbstractFormInputPopulator<FinancialYearEndViewModel> {

    @Override
    public FormInputType type() {
        return FormInputType.FINANCIAL_YEAR_END;
    }

    @Override
    protected void populate(AbstractApplicantResource resource, FinancialYearEndViewModel viewModel) {

    }

    @Override
    protected FinancialYearEndViewModel createNew() {
        return new FinancialYearEndViewModel();
    }
}
