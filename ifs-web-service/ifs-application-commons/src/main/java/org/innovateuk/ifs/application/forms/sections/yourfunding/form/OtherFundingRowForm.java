package org.innovateuk.ifs.application.forms.sections.yourfunding.form;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.OtherFunding;

public class OtherFundingRowForm extends BaseOtherFundingRowForm<OtherFunding> {

    public OtherFundingRowForm() {
    }

    public OtherFundingRowForm(OtherFunding otherFunding) {
        super(otherFunding);
    }

    @Override
    public FinanceRowType getRowType() {
        return FinanceRowType.OTHER_FUNDING;
    }

    @Override
    public OtherFunding toCost(Long financeId) {
        return new OtherFunding(getCostId(), null, getSource(), getDate(), getFundingAmount(), financeId);
    }
}
