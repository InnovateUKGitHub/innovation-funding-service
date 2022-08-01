package org.innovateuk.ifs.management.decision.form;

/**
 * Contains both application id selection and filter values in one object.
 */
public class DecisionSelectionCookie {
    private DecisionSelectionForm decisionSelectionForm;
    private DecisionFilterForm decisionFilterForm;

    public DecisionSelectionCookie() {
        this.decisionSelectionForm = new DecisionSelectionForm();
        this.decisionFilterForm = new DecisionFilterForm();
    }

    public DecisionSelectionCookie(DecisionSelectionForm decisionSelectionForm) {
        this.decisionSelectionForm = decisionSelectionForm;
        this.decisionFilterForm = new DecisionFilterForm();
    }

    public DecisionSelectionForm getDecisionSelectionForm() {
        return decisionSelectionForm;
    }

    public void setDecisionSelectionForm(DecisionSelectionForm decisionSelectionForm) {
        this.decisionSelectionForm = decisionSelectionForm;
    }

    public DecisionFilterForm getDecisionFilterForm() {
        return decisionFilterForm;
    }

    public void setDecisionFilterForm(DecisionFilterForm decisionFilterForm) {
        this.decisionFilterForm = decisionFilterForm;
    }
}
