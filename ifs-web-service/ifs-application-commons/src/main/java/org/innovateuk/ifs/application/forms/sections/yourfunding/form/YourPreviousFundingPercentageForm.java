package org.innovateuk.ifs.application.forms.sections.yourfunding.form;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

public class YourPreviousFundingPercentageForm extends AbstractYourFundingPercentageForm<PreviousFundingRowForm> {
    @Override
    public FinanceRowType otherFundingType() {
        return FinanceRowType.PREVIOUS_FUNDING;
    }
}
