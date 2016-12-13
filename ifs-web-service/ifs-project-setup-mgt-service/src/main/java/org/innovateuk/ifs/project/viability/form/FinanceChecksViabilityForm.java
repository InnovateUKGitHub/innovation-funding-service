package org.innovateuk.ifs.project.viability.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;

/**
 * Form backing the checkboxes and dropdowns on the Viability page
 */
public class FinanceChecksViabilityForm extends BaseBindingResultTarget {

    private boolean creditReportConfirmed;
    private boolean viabilityConfirmed;
    private String ragStatus;

    // for Spring MVC
    FinanceChecksViabilityForm() {
    }

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

    public void setCreditReportConfirmed(boolean creditReportConfirmed) {
        this.creditReportConfirmed = creditReportConfirmed;
    }

    public void setViabilityConfirmed(boolean viabilityConfirmed) {
        this.viabilityConfirmed = viabilityConfirmed;
    }

    public void setRagStatus(String ragStatus) {
        this.ragStatus = ragStatus;
    }
}
