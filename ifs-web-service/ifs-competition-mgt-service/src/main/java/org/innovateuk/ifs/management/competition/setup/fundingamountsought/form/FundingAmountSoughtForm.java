package org.innovateuk.ifs.management.competition.setup.fundingamountsought.form;

import org.innovateuk.ifs.commons.validation.constraints.FieldRequiredIf;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@FieldRequiredIf(required = "fundingAmountSought", argument = "fundingAmountSoughtApplicable", predicate = true, message = "{validation.applicationfundingamountsoughtform.application.funding.amount.sought.required}")
public class FundingAmountSoughtForm extends CompetitionSetupForm {

    @NotNull(message = "{validation.applicationfundingamountsoughtform.application.funding.amount.sought.applicable.required}")
    private Boolean fundingAmountSoughtApplicable;

    @DecimalMin(value = "1", message = "{validation.applicationfundingamountsoughtform.application.funding.amount.sought.min}")
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

    public Boolean getFundingAmountSoughtApplicable() {
        return fundingAmountSoughtApplicable;
    }

    public void setFundingAmountSoughtApplicable(Boolean fundingAmountSoughtApplicable) {
        this.fundingAmountSoughtApplicable = fundingAmountSoughtApplicable;
    }

    public BigDecimal getFundingAmountSought() {
        return fundingAmountSought;
    }

    public void setFundingAmountSought(BigDecimal fundingAmountSought) {
        this.fundingAmountSought = fundingAmountSought;
    }
}