package org.innovateuk.ifs.project.funding.level.form;

import java.math.BigDecimal;

public class ProjectFinancePartnerFundingLevelForm {

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
