package org.innovateuk.ifs.management.decision.form;

import javax.validation.constraints.NotBlank;

/**
 * Contains the Funding Decision choice value.
 */
public class DecisionChoiceForm {
    @NotBlank(message = "{validation.field.must.not.be.blank}")
    private String decision;

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }
}
