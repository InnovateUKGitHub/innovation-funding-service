package org.innovateuk.ifs.application.finance.viewmodel;

/**
 * Base viewmodel for financesViewModel
 */
public abstract class BaseFinanceViewModel {
    protected String financeView;


    public String getFinanceView() {
        return financeView;
    }

    public void setFinanceView(String financeView) {
        this.financeView = financeView;
    }

    public Boolean getHasOrganisationFinance() {
        return Boolean.FALSE;
    }
}
