package org.innovateuk.ifs.application.forms.sections.yourfunding.form;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import java.math.BigDecimal;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.FINANCE;

public class YourFundingPercentageForm extends AbstractYourFundingForm {

    private Boolean requestingFunding;
    private BigDecimal grantClaimPercentage;

    public Boolean getRequestingFunding() {
        return requestingFunding;
    }

    public void setRequestingFunding(Boolean requestingFunding) {
        this.requestingFunding = requestingFunding;
    }

    public BigDecimal getGrantClaimPercentage() {
        return grantClaimPercentage;
    }

    public void setGrantClaimPercentage(BigDecimal grantClaimPercentage) {
        this.grantClaimPercentage = grantClaimPercentage;
    }

    @Override
    protected FinanceRowType financeType() {
        return FINANCE;
    }
}