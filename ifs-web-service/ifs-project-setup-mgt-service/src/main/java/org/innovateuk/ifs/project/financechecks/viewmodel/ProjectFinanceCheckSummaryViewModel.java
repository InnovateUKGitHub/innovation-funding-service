package org.innovateuk.ifs.project.financechecks.viewmodel;

import org.innovateuk.ifs.project.finance.resource.FinanceCheckSummaryResource;

/**
 * View model backing the internal Finance Team members view of the Finance Check summary page
 */
public class ProjectFinanceCheckSummaryViewModel {

    private FinanceCheckSummaryResource financeCheckSummaryResource;

    public ProjectFinanceCheckSummaryViewModel(FinanceCheckSummaryResource financeCheckSummaryResource) {
        this.financeCheckSummaryResource = financeCheckSummaryResource;
    }

    public boolean isShowEnabledGenerateSpendProfilesButton() {
        return financeCheckSummaryResource.isFinanceChecksAllApproved() &&
                financeCheckSummaryResource.isBankDetailsApproved() &&
               !financeCheckSummaryResource.isSpendProfilesGenerated();
    }

    public boolean isShowDisabledGenerateSpendProfilesButton() {
        return (!financeCheckSummaryResource.isFinanceChecksAllApproved() ||
                !financeCheckSummaryResource.isBankDetailsApproved()) &&
                !financeCheckSummaryResource.isSpendProfilesGenerated();
    }

    public boolean isShowSpendProfilesGeneratedMessage() {
        return financeCheckSummaryResource.isSpendProfilesGenerated();
    }

    public FinanceCheckSummaryResource getFinanceCheckSummaryResource() {
        return financeCheckSummaryResource;
    }

    public void setFinanceCheckSummaryResource(FinanceCheckSummaryResource financeCheckSummaryResource) {
        this.financeCheckSummaryResource = financeCheckSummaryResource;
    }
}
