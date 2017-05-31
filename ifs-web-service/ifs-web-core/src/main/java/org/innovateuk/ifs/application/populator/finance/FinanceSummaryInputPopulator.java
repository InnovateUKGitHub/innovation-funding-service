package org.innovateuk.ifs.application.populator.finance;

import org.innovateuk.ifs.application.populator.forminput.AbstractFormInputPopulator;
import org.innovateuk.ifs.application.viewmodel.finance.FinanceSummaryViewModel;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.stereotype.Component;

/**
 * Populator for finance summary form inputs.
 */
@Component
public class FinanceSummaryInputPopulator extends AbstractFormInputPopulator<FinanceSummaryViewModel> {

    @Override
    public FormInputType type() {
        return FormInputType.FINANCIAL_SUMMARY;
    }

    @Override
    protected FinanceSummaryViewModel createNew() {
        return new FinanceSummaryViewModel();
    }

}
