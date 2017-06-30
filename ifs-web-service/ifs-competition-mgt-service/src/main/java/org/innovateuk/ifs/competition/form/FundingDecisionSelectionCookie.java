package org.innovateuk.ifs.competition.form;

/**
 * Contains both application id selection and filter values in one object.
 */
public class FundingDecisionSelectionCookie {
    private FundingDecisionSelectionForm fundingDecisionSelectionForm;
    private FundingDecisionFilterForm fundingDecisionFilterForm;

    public FundingDecisionSelectionCookie() {
        this.fundingDecisionSelectionForm = new FundingDecisionSelectionForm();
        this.fundingDecisionFilterForm = new FundingDecisionFilterForm();
    }

    public FundingDecisionSelectionCookie(FundingDecisionSelectionForm fundingDecisionSelectionForm) {
        this.fundingDecisionSelectionForm = fundingDecisionSelectionForm;
        this.fundingDecisionFilterForm = new FundingDecisionFilterForm();
    }

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
