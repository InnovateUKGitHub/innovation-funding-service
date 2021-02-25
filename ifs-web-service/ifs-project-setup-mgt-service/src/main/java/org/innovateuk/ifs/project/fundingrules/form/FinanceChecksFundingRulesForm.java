package org.innovateuk.ifs.project.fundingrules.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;

public class FinanceChecksFundingRulesForm extends BaseBindingResultTarget {

    private boolean changeToStateAid;
    private boolean confirmFundingRules;

    // for Spring MVC
    FinanceChecksFundingRulesForm() {
    }

    public FinanceChecksFundingRulesForm(boolean changeToStateAid) {
        this.changeToStateAid = changeToStateAid;
    }

    public boolean isChangeToStateAid() {
        return changeToStateAid;
    }

    public void setConfirmFundingRules(boolean confirmFundingRules) {
        this.confirmFundingRules = confirmFundingRules;
    }

    public boolean isConfirmFundingRules() {
        return confirmFundingRules;
    }
}