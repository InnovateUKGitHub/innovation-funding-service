package org.innovateuk.ifs.project.viability.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;

/**
 * Form backing the checkboxes and dropdowns on the Viability page
 */
public class FinanceChecksViabilityForm extends BaseBindingResultTarget {

    private boolean creditReportConfirmed;
    private boolean viabilityConfirmed;
    private String ragStatus;

    public FinanceChecksViabilityForm(boolean creditReportConfirmed, boolean viabilityConfirmed, String ragStatus) {
        this.creditReportConfirmed = creditReportConfirmed;
        this.viabilityConfirmed = viabilityConfirmed;
        this.ragStatus = ragStatus;
    }

    public boolean isCreditReportConfirmed() {
        return creditReportConfirmed;
    }

    public boolean isViabilityConfirmed() {
        return viabilityConfirmed;
    }

    public String getRagStatus() {
        return ragStatus;
    }
}
