package org.innovateuk.ifs.competition.form;

/**
 * Contains both application id selection and filter values in one object.
 */
public class FundingDecisionSelectionCookie {
    private FundingDecisionSelectionForm fundingDecisionSelectionForm = new FundingDecisionSelectionForm();
    private FundingDecisionFilterForm fundingDecisionFilterForm = new FundingDecisionFilterForm();

    public FundingDecisionSelectionForm getFundingDecisionSelectionForm() {
        return fundingDecisionSelectionForm;
    }

    public void setFundingDecisionSelectionForm(FundingDecisionSelectionForm fundingDecisionSelectionForm) {
        this.fundingDecisionSelectionForm = fundingDecisionSelectionForm;
    }

    public FundingDecisionFilterForm getFundingDecisionFilterForm() {
        return fundingDecisionFilterForm;
    }

    public void setFundingDecisionFilterForm(FundingDecisionFilterForm fundingDecisionFilterForm) {
        this.fundingDecisionFilterForm = fundingDecisionFilterForm;
    }
}
