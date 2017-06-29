package org.innovateuk.ifs.competition.form;

/**
    Cookie class to store both application id selection and filter values in one object.
 */
public class FundingNotificationSelectionCookie {
    private FundingNotificationSelectionForm fundingNotificationSelectionForm;
    private FundingNotificationFilterForm fundingNotificationFilterForm;

    public FundingNotificationSelectionCookie() {
        this.fundingNotificationSelectionForm = new FundingNotificationSelectionForm();
        this.fundingNotificationFilterForm = new FundingNotificationFilterForm();
    }

    public FundingNotificationSelectionCookie(FundingNotificationSelectionForm fundingNotificationSelectionForm) {
        this.fundingNotificationSelectionForm = fundingNotificationSelectionForm;
        this.fundingNotificationFilterForm = new FundingNotificationFilterForm();
    }

    public FundingNotificationSelectionForm getFundingNotificationSelectionForm() {
        return fundingNotificationSelectionForm;
    }

    public void setFundingNotificationSelectionForm(FundingNotificationSelectionForm fundingNotificationSelectionForm) {
        this.fundingNotificationSelectionForm = fundingNotificationSelectionForm;
    }

    public FundingNotificationFilterForm getFundingNotificationFilterForm() {
        return fundingNotificationFilterForm;
    }

    public void setFundingNotificationFilterForm(FundingNotificationFilterForm fundingNotificationFilterForm) {
        this.fundingNotificationFilterForm = fundingNotificationFilterForm;
    }
}
