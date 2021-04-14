package org.innovateuk.ifs.project.fundingrules.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.AssertTrue;

public class FinanceChecksConfirmFundingRulesForm extends BaseBindingResultTarget {

    @AssertTrue(message = "{validation.fundingrules.confirm.required}")
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