package org.innovateuk.ifs.application.forms.sections.yourfunding.form;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.FINANCE;

public class YourFundingPercentageForm extends AbstractYourFundingForm {
    private Integer grantClaimPercentage;

    public Integer getGrantClaimPercentage() {
        return grantClaimPercentage;
    }

    public void setGrantClaimPercentage(Integer grantClaimPercentage) {
        this.grantClaimPercentage = grantClaimPercentage;
    }

    @Override
    protected FinanceRowType financeType() {
        return FINANCE;
    }
}