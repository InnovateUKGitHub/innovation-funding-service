package org.innovateuk.ifs.competition.form;

/*
    Cookie class to store both application id selection and filter values in one object.
 */
public class FundingNotificationSelectionCookie {
    private SelectApplicationsForEmailForm selectApplicationsForEmailForm;
    private ManageFundingApplicationsQueryForm manageFundingApplicationsQueryForm;

    public SelectApplicationsForEmailForm getSelectApplicationsForEmailForm() {
        return selectApplicationsForEmailForm;
    }

    public void setSelectApplicationsForEmailForm(SelectApplicationsForEmailForm selectApplicationsForEmailForm) {
        this.selectApplicationsForEmailForm = selectApplicationsForEmailForm;
    }

    public ManageFundingApplicationsQueryForm getManageFundingApplicationsQueryForm() {
        return manageFundingApplicationsQueryForm;
    }

    public void setManageFundingApplicationsQueryForm(ManageFundingApplicationsQueryForm manageFundingApplicationsQueryForm) {
        this.manageFundingApplicationsQueryForm = manageFundingApplicationsQueryForm;
    }
}
