package org.innovateuk.ifs.application.forms.sections.yourfunding.form;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.PreviousFunding;

public class YourPreviousFundingPercentageForm extends AbstractYourFundingPercentageForm<PreviousFunding, PreviousFundingRowForm> {
    @Override
    public FinanceRowType otherFundingType() {
        return FinanceRowType.PREVIOUS_FUNDING;
    }
}
