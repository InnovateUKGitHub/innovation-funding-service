package org.innovateuk.ifs.project.fundingrules.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;

public class FinanceChecksConfirmFundingRulesForm extends BaseBindingResultTarget {

    private boolean confirmFundingRules;

    public FinanceChecksConfirmFundingRulesForm() {
    }

    public void setConfirmFundingRules(boolean confirmFundingRules) {
        this.confirmFundingRules = confirmFundingRules;
    }

    public boolean isConfirmFundingRules() {
        return confirmFundingRules;
    }
}