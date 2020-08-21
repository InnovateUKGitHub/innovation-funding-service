package org.innovateuk.ifs.application.forms.sections.yourfunding.form;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

public class YourFundingAmountForm extends AbstractYourFundingAmountForm<OtherFundingRowForm> {
    @Override
    public FinanceRowType otherFundingType() {
        return FinanceRowType.OTHER_FUNDING;
    }
}