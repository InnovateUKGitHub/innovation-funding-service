package org.innovateuk.ifs.project.fundingrules.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;

public class FinanceChecksFundingRulesForm extends BaseBindingResultTarget {

    private boolean overrideFundingRules;

    public FinanceChecksFundingRulesForm() {
    }

    public void setOverrideFundingRules(boolean overrideFundingRules) {
        this.overrideFundingRules = overrideFundingRules;
    }

    public boolean isOverrideFundingRules() {
        return overrideFundingRules;
    }

}