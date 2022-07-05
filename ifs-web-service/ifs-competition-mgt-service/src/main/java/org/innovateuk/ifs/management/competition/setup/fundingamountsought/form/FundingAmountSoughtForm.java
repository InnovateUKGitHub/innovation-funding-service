package org.innovateuk.ifs.management.competition.setup.fundingamountsought.form;

import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class FundingAmountSoughtForm extends CompetitionSetupForm {

    @NotNull(message = "{validation.eligibilityform.researchCategoriesApplicable.required}")
    private boolean fundingAmountSoughtApplicable;

    private BigDecimal fundingAmountSought;


    public FundingAmountSoughtForm() {
    }

    public FundingAmountSoughtForm(boolean fundingAmountSoughtApplicable, BigDecimal fundingAmountSought) {
        this.fundingAmountSoughtApplicable = fundingAmountSoughtApplicable;
        this.fundingAmountSought = fundingAmountSought;
    }

    public FundingAmountSoughtForm(boolean fundingAmountSoughtApplicable) {
        this.fundingAmountSoughtApplicable = fundingAmountSoughtApplicable;
    }

    public boolean getFundingAmountSoughtApplicable() {
        return fundingAmountSoughtApplicable;
    }

    public void setFundingAmountSoughtApplicable(boolean fundingAmountSoughtApplicable) {
        this.fundingAmountSoughtApplicable = fundingAmountSoughtApplicable;
    }

    public BigDecimal getFundingAmountSought() {
        return fundingAmountSought;
    }

    public void setFundingAmountSought(BigDecimal fundingAmountSought) {
        this.fundingAmountSought = fundingAmountSought;
    }
}
