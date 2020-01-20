package org.innovateuk.ifs.project.funding.level.form;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowItem.MAX_DECIMAL;

public class ProjectFinancePartnerFundingLevelForm {

    @NotNull(message = "{validation.finance.funding.level.required}")
    @DecimalMin(value = "1", message = "{validation.finance.funding.level.min}")
    @Digits(integer = MAX_DECIMAL, fraction = 2, message ="{validation.finance.percentage}")
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
