package org.innovateuk.ifs.management.competition.setup.applicationexpressionofinterest.form;

import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;

import javax.validation.constraints.NotNull;

/**
 * Form to capture the selection of an Application expression of interest in Competition Setup.
 */
public class ApplicationExpressionOfInterestForm extends CompetitionSetupForm {

    @NotNull(message = "{validation.applicationexpressionofinterestform.application.expression.of.interest.required}")
    private Boolean expressionOfInterest;

    public ApplicationExpressionOfInterestForm() {
    }

    public ApplicationExpressionOfInterestForm(Boolean expressionOfInterest) {
        this.expressionOfInterest = expressionOfInterest;
    }

    public Boolean getExpressionOfInterest() {
        return expressionOfInterest;
    }

    public void setExpressionOfInterest(Boolean expressionOfInterest) {
        this.expressionOfInterest = expressionOfInterest;
    }
}
