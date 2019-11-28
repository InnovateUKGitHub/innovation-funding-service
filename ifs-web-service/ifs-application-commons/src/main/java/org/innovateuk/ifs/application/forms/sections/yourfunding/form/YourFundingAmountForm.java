package org.innovateuk.ifs.application.forms.sections.yourfunding.form;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import java.math.BigDecimal;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.GRANT_CLAIM_AMOUNT;

public class YourFundingAmountForm extends AbstractYourFundingForm {

    private BigDecimal amount;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    protected FinanceRowType financeType() {
        return GRANT_CLAIM_AMOUNT;
    }
}