package org.innovateuk.ifs.application.populator.finance;

import org.innovateuk.ifs.applicant.resource.AbstractApplicantResource;
import org.innovateuk.ifs.application.populator.forminput.AbstractFormInputPopulator;
import org.innovateuk.ifs.application.viewmodel.finance.FinancialOverviewRowViewModel;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.stereotype.Component;

/**
 * Populator for financial overview form inputs.
 */
@Component
public class FinancialOverviewRowPopulator extends AbstractFormInputPopulator<FinancialOverviewRowViewModel> {

    @Override
    public FormInputType type() {
        return FormInputType.FINANCIAL_OVERVIEW_ROW;
    }

    @Override
    protected void populate(AbstractApplicantResource resource, FinancialOverviewRowViewModel viewModel) {

    }

    @Override
    protected FinancialOverviewRowViewModel createNew() {
        return new FinancialOverviewRowViewModel();
    }
}
