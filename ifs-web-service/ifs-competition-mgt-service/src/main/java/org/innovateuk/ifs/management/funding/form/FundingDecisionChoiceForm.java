package org.innovateuk.ifs.management.funding.form;

import javax.validation.constraints.NotBlank;

/**
 * Contains the Funding Decision choice value.
 */
public class FundingDecisionChoiceForm {
    @NotBlank(message = "{validation.field.must.not.be.blank}")
    private String fundingDecision;

    public String getFundingDecision() {
        return fundingDecision;
    }

    public void setFundingDecision(String fundingDecision) {
        this.fundingDecision = fundingDecision;
    }
}
