package org.innovateuk.ifs.application.forms.sections.yourfunding.form;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

public class YourPreviousFundingAmountForm extends AbstractYourFundingAmountForm<PreviousFundingRowForm> {
    @Override
    public FinanceRowType otherFundingType() {
        return FinanceRowType.PREVIOUS_FUNDING;
    }
}
