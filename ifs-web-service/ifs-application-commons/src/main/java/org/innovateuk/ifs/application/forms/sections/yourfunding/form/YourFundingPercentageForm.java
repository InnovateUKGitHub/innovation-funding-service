package org.innovateuk.ifs.application.forms.sections.yourfunding.form;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

public class YourFundingPercentageForm extends AbstractYourFundingPercentageForm<OtherFundingRowForm> {
    @Override
    public FinanceRowType otherFundingType() {
        return FinanceRowType.OTHER_FUNDING;
    }
}