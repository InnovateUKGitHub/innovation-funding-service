package org.innovateuk.ifs.competition.form;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * TODO: Add description
 */
public class FundingDecisionChoiceForm {
    @NotEmpty
    private String fundingDecision;

    public String getFundingDecision() {
        return fundingDecision;
    }

    public void setFundingDecision(String fundingDecision) {
        this.fundingDecision = fundingDecision;
    }
}
