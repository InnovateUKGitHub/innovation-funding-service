package org.innovateuk.ifs.management.competition.setup.fundingamountsought.form;

import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;

import javax.validation.constraints.NotNull;

public class FundingAmountSoughtForm extends CompetitionSetupForm {

    @NotNull(message = "{validation.eligibilityform.researchCategoriesApplicable.required}")
    private Boolean fundingAmountSoughtApplicable;

    public FundingAmountSoughtForm() {
    }

    public FundingAmountSoughtForm(Boolean fundingAmountSoughtApplicable) {
        this.fundingAmountSoughtApplicable = fundingAmountSoughtApplicable;
    }

    public Boolean getFundingAmountSoughtApplicable() {
        return fundingAmountSoughtApplicable;
    }

    public void setFundingAmountSoughtApplicable(Boolean fundingAmountSoughtApplicable) {
        this.fundingAmountSoughtApplicable = fundingAmountSoughtApplicable;
    }
}
