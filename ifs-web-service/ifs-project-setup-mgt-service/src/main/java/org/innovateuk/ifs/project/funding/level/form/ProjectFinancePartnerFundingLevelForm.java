package org.innovateuk.ifs.project.funding.level.form;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ProjectFinancePartnerFundingLevelForm {

    @NotNull(message = "{validation.finance.funding.level.required}")
    @DecimalMin(value = "0", message = "{validation.finance.funding.level.min}")
    private BigDecimal fundingLevel;

    public ProjectFinancePartnerFundingLevelForm() {
    }

    public ProjectFinancePartnerFundingLevelForm(BigDecimal fundingLevel) {
        this.fundingLevel = fundingLevel;
    }

    public BigDecimal getFundingLevel() {
        return fundingLevel;
    }

    public void setFundingLevel(BigDecimal fundingLevel) {
        this.fundingLevel = fundingLevel;
    }
}
