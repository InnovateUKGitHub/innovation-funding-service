package org.innovateuk.ifs.project.funding.form;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ProjectFinancePartnerFundingForm {

    @NotNull(message = "{validation.finance.funding.sought.required}")
    @DecimalMin(value = "1", message = "{validation.finance.funding.sought.min}")
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
