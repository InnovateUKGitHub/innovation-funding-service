package org.innovateuk.ifs.application.forms.sections.yourfunding.form;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.OtherFunding;

public class YourFundingAmountForm extends AbstractYourFundingAmountForm<OtherFunding, OtherFundingRowForm> {

    @Override
    public FinanceRowType otherFundingType() {
        return FinanceRowType.OTHER_FUNDING;
    }
}