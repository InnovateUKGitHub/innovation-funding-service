package org.innovateuk.ifs.project.viability.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.innovateuk.ifs.project.finance.resource.ViabilityRagStatus;

/**
 * Form backing the checkboxes and dropdowns on the Viability page
 */
public class FinanceChecksViabilityForm extends BaseBindingResultTarget {

    private boolean creditReportConfirmed;
    private ViabilityRagStatus ragStatus;
    private boolean confirmViabilityChecked;

    public FinanceChecksViabilityForm() {
    }

    public FinanceChecksViabilityForm(boolean creditReportConfirmed, ViabilityRagStatus ragStatus, boolean confirmViabilityChecked) {
        this.creditReportConfirmed = creditReportConfirmed;
        this.ragStatus = ragStatus;
        this.confirmViabilityChecked = confirmViabilityChecked;
    }

    public boolean isCreditReportConfirmed() {
        return creditReportConfirmed;
    }

    public ViabilityRagStatus getRagStatus() {
        return ragStatus;
    }

    public void setCreditReportConfirmed(boolean creditReportConfirmed) {
        this.creditReportConfirmed = creditReportConfirmed;
    }

    public void setRagStatus(ViabilityRagStatus ragStatus) {
        this.ragStatus = ragStatus;
    }

    public boolean isConfirmViabilityChecked() {
        return confirmViabilityChecked;
    }

    public void setConfirmViabilityChecked(boolean confirmViabilityChecked) {
        this.confirmViabilityChecked = confirmViabilityChecked;
    }
}
