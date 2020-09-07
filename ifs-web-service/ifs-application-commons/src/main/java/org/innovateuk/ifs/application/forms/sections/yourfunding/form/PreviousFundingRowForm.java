package org.innovateuk.ifs.application.forms.sections.yourfunding.form;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.PreviousFunding;

public class PreviousFundingRowForm extends BaseOtherFundingRowForm<PreviousFunding>{

    public PreviousFundingRowForm() {
    }

    public PreviousFundingRowForm(PreviousFunding previousFunding) {
        super(previousFunding);
    }

    @Override
    public FinanceRowType getRowType() {
        return FinanceRowType.PREVIOUS_FUNDING;
    }

    @Override
    public PreviousFunding toCost(Long financeId) {
        return new PreviousFunding(getCostId(), null, getSource(), getDate(), getFundingAmount(), financeId);
    }
}
