package org.innovateuk.ifs.project.viability.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;

/**
 * Form backing the checkboxes and dropdowns on the Viability page
 */
public class FinanceChecksViabilityForm extends BaseBindingResultTarget {

    private boolean creditReportConfirmed;
    private String ragStatus;

    // for Spring MVC
    FinanceChecksViabilityForm() {
    }

    public FinanceChecksViabilityForm(boolean creditReportConfirmed, boolean viabilityConfirmed, String ragStatus) {
        this.creditReportConfirmed = creditReportConfirmed;
        this.ragStatus = ragStatus;
    }

    public boolean isCreditReportConfirmed() {
        return creditReportConfirmed;
    }

    public boolean isViabilityConfirmedChecked() {
        return ragStatus != null;
    }

    public String getRagStatus() {
        return ragStatus;
    }

    public void setCreditReportConfirmed(boolean creditReportConfirmed) {
        this.creditReportConfirmed = creditReportConfirmed;
    }

    public void setRagStatus(String ragStatus) {
        this.ragStatus = ragStatus;
    }
}
