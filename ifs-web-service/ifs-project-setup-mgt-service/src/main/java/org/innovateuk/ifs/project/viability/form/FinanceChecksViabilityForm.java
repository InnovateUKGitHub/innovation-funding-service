package org.innovateuk.ifs.project.viability.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.innovateuk.ifs.project.finance.resource.ViabilityStatus;

/**
 * Form backing the checkboxes and dropdowns on the Viability page
 */
public class FinanceChecksViabilityForm extends BaseBindingResultTarget {

    private boolean creditReportConfirmed;
    private ViabilityStatus ragStatus;
    private boolean confirmViabilityChecked;

    // for Spring MVC
    FinanceChecksViabilityForm() {
    }

    public FinanceChecksViabilityForm(boolean creditReportConfirmed, ViabilityStatus ragStatus, boolean confirmViabilityChecked) {
        this.creditReportConfirmed = creditReportConfirmed;
        this.ragStatus = ragStatus;
        this.confirmViabilityChecked = confirmViabilityChecked;
    }

    public boolean isCreditReportConfirmed() {
        return creditReportConfirmed;
    }

    public ViabilityStatus getRagStatus() {
        return ragStatus;
    }

    public void setCreditReportConfirmed(boolean creditReportConfirmed) {
        this.creditReportConfirmed = creditReportConfirmed;
    }

    public void setRagStatus(ViabilityStatus ragStatus) {
        this.ragStatus = ragStatus;
    }

    public boolean isConfirmViabilityChecked() {
        return confirmViabilityChecked;
    }

    public void setConfirmViabilityChecked(boolean confirmViabilityChecked) {
        this.confirmViabilityChecked = confirmViabilityChecked;
    }
}
