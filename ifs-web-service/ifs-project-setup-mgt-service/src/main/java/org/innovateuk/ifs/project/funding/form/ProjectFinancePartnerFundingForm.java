package org.innovateuk.ifs.project.funding.form;

import java.math.BigDecimal;

public class ProjectFinancePartnerFundingForm {

    private BigDecimal funding;

    public ProjectFinancePartnerFundingForm() {
    }

    public ProjectFinancePartnerFundingForm(BigDecimal funding) {
        this.funding = funding;
    }

    public BigDecimal getFunding() {
        return funding;
    }

    public void setFunding(BigDecimal funding) {
        this.funding = funding;
    }
}
