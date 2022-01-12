package org.innovateuk.ifs.application.forms.sections.yourfunding.form;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.PreviousFunding;

public class YourPreviousFundingAmountForm extends AbstractYourFundingAmountForm<PreviousFunding, PreviousFundingRowForm> {
    @Override
    public FinanceRowType otherFundingType() {
        return FinanceRowType.PREVIOUS_FUNDING;
    }
}
