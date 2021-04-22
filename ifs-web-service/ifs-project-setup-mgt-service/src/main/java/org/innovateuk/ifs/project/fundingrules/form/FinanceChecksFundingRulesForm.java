package org.innovateuk.ifs.project.fundingrules.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.AssertTrue;

public class FinanceChecksFundingRulesForm extends BaseBindingResultTarget {
    @AssertTrue(message = "{validation.fundingrules.override.required}")
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