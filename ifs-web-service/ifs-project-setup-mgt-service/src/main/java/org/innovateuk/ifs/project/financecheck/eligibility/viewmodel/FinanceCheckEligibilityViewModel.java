package org.innovateuk.ifs.project.financecheck.eligibility.viewmodel;


import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;

/**
 * View model backing the internal Finance Team members view of the Finance Check Eligibility page
 */
public class FinanceCheckEligibilityViewModel {
    private FinanceCheckEligibilityResource financesOverview;


    public FinanceCheckEligibilityResource getFinanceCheckSummaryResource() {
        return financesOverview;
    }

    public void FinanceCheckEligibilityResource(FinanceCheckEligibilityResource financesOverview) {
        this.financesOverview = financesOverview;
    }

}
