package org.innovateuk.ifs.project.fundingrules.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.AssertTrue;

public class FinanceChecksConfirmFundingRulesForm extends BaseBindingResultTarget {

    @AssertTrue
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