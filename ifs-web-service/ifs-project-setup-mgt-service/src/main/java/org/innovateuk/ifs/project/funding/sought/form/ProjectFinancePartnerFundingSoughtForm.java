package org.innovateuk.ifs.project.funding.sought.form;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ProjectFinancePartnerFundingSoughtForm {

    @NotNull(message = "{validation.finance.funding.sought.required}")
    @DecimalMin(value = "1", message = "{validation.finance.funding.sought.min}")
    private BigDecimal funding;

    public ProjectFinancePartnerFundingSoughtForm() {
    }

    public ProjectFinancePartnerFundingSoughtForm(BigDecimal funding) {
        this.funding = funding;
    }

    public BigDecimal getFunding() {
        return funding;
    }

    public void setFunding(BigDecimal funding) {
        this.funding = funding;
    }
}
