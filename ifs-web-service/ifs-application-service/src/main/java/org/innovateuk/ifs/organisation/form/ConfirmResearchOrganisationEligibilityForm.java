package org.innovateuk.ifs.organisation.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.NotNull;

/**
 * Form field model for the screen asking whether the Research organisation is eligible to continue with an application.
 */
public class ConfirmResearchOrganisationEligibilityForm extends BaseBindingResultTarget {

    @NotNull(message = "{validation.field.confirm.research.eligibility}")
    private Boolean confirmEligibility;

    public Boolean getConfirmEligibility() {
        return confirmEligibility;
    }

    public void setConfirmEligibility(Boolean confirmEligibility) {
        this.confirmEligibility = confirmEligibility;
    }
}
