package org.innovateuk.ifs.management.competition.setup.fundingamountsought.form;

import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;

import javax.validation.constraints.NotNull;

public class ApplicationFundingAmountSoughtForm extends CompetitionSetupForm {

    @NotNull(message = "{validation.applicationfundingamountsoughtform.application.funding.amount.sought.required}")
    private Boolean fundingAmountSought;

    public ApplicationFundingAmountSoughtForm(Boolean fundingAmountSought) {
        this.fundingAmountSought = fundingAmountSought;
    }

    public Boolean getFundingAmountSought() {
        return fundingAmountSought;
    }

    public void setFundingAmountSought(Boolean fundingAmountSought) {
        this.fundingAmountSought = fundingAmountSought;
    }
}
