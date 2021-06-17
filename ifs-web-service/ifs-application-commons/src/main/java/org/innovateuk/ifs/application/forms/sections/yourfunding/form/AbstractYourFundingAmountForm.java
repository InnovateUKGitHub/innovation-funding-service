package org.innovateuk.ifs.application.forms.sections.yourfunding.form;

import org.innovateuk.ifs.finance.resource.cost.BaseOtherFunding;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import java.math.BigDecimal;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.GRANT_CLAIM_AMOUNT;

public abstract class AbstractYourFundingAmountForm<R extends BaseOtherFunding,
        T extends BaseOtherFundingRowForm<R>> extends AbstractYourFundingForm<R, T> {

    private BigDecimal amount;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public FinanceRowType financeType() {
        return GRANT_CLAIM_AMOUNT;
    }
}