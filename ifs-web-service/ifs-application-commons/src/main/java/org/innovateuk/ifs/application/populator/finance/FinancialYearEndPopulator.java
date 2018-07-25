package org.innovateuk.ifs.application.populator.finance;

import org.innovateuk.ifs.application.populator.forminput.AbstractFormInputPopulator;
import org.innovateuk.ifs.application.viewmodel.finance.FinancialYearEndViewModel;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.springframework.stereotype.Component;

/**
 * Populator for financial year end form inputs.
 */
@Component
public class FinancialYearEndPopulator extends AbstractFormInputPopulator<FinancialYearEndViewModel> {

    @Override
    public FormInputType type() {
        return FormInputType.FINANCIAL_YEAR_END;
    }

    @Override
    protected FinancialYearEndViewModel createNew() {
        return new FinancialYearEndViewModel();
    }
}
